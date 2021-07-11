package cn.itcast.wanxinp2p.repayment.service;

import cn.itcast.wanxinp2p.api.depository.model.UserAutoPreTransactionRequest;
import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.api.transaction.model.TenderDTO;
import cn.itcast.wanxinp2p.common.domain.*;
import cn.itcast.wanxinp2p.common.util.CodeNoUtil;
import cn.itcast.wanxinp2p.common.util.DateUtil;
import cn.itcast.wanxinp2p.repayment.agent.DepositoryAgentApiAgent;
import cn.itcast.wanxinp2p.repayment.entity.ReceivablePlan;
import cn.itcast.wanxinp2p.repayment.entity.RepaymentDetail;
import cn.itcast.wanxinp2p.repayment.entity.RepaymentPlan;
import cn.itcast.wanxinp2p.repayment.mapper.PlanMapper;
import cn.itcast.wanxinp2p.repayment.mapper.ReceivablePlanMapper;
import cn.itcast.wanxinp2p.repayment.mapper.RepaymentDetailMapper;
import cn.itcast.wanxinp2p.repayment.model.EqualInterestRepayment;
import cn.itcast.wanxinp2p.repayment.util.RepaymentUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RepaymentServiceImpl implements RepaymentService {

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private ReceivablePlanMapper receivablePlanMapper;

    @Autowired
    private RepaymentDetailMapper repaymentDetailMapper;

    @Autowired
    private DepositoryAgentApiAgent depositoryAgentApiAgent;

    @Transactional(rollbackFor = BusinessException.class)
    @Override
    public String startRepayment(ProjectWithTendersDTO projectWithTendersDTO) {

        //1.生成借款人还款计划
        //1.1 获取标的信息
        ProjectDTO projectDTO = projectWithTendersDTO.getProject();

        //1.2 获取投标信息
        List<TenderDTO> tenders = projectWithTendersDTO.getTenders();

        //1.3 计算还款的月数
        Double ceil = Math.ceil(projectDTO.getPeriod() / 30.0);
        Integer month = ceil.intValue();

        //1.4 还款方式，只针对等额本息
        String repaymentWay = projectDTO.getRepaymentWay();

        if (repaymentWay.equals(RepaymentWayCode.FIXED_REPAYMENT.getCode())) {
            //1.5 生成还款计划
            EqualInterestRepayment fixedRepayment = RepaymentUtil.fixedRepayment(projectDTO.getAmount(), projectDTO.getBorrowerAnnualRate(), month, projectDTO.getCommissionAnnualRate());

            //1.6 保存还款计划
            List<RepaymentPlan> planList = saveRepaymentPlan(projectDTO, fixedRepayment);

            //2.生成投资人应收明细
            //2.1 根据投标信息生成应收明细
            tenders.forEach(tenderDTO -> {
                EqualInterestRepayment receipt = RepaymentUtil.fixedRepayment(tenderDTO.getAmount(), tenderDTO.getProjectAnnualRate(), month, projectWithTendersDTO.getCommissionInvestorAnnualRate());

                //2.2 保存应收明细到数据库
                planList.forEach(plan -> {
                    saveRreceivablePlan(plan, tenderDTO, receipt);
                });

            });

        } else {
            return "-1";
        }

        return DepositoryReturnCode.RETURN_CODE_00000.getCode();
    }

    @Override
    public List<RepaymentPlan> selectDueRepayment(String date) {
        return planMapper.selectDueRepayment(date);
    }

    @Override
    public RepaymentDetail saveRepaymentDetail(RepaymentPlan repaymentPlan) {
        //1. 进行查询
        RepaymentDetail repaymentDetail = repaymentDetailMapper.selectOne(Wrappers.<RepaymentDetail>lambdaQuery().eq(RepaymentDetail::getRepaymentPlanId, repaymentPlan.getId()));

        //2. 查不到数据才进行保存
        if (repaymentDetail == null) {
            repaymentDetail = new RepaymentDetail();
            // 还款计划项标识
            repaymentDetail.setRepaymentPlanId(repaymentPlan.getId());
            // 实还本息
            repaymentDetail.setAmount(repaymentPlan.getAmount());
            // 实际还款时间
            repaymentDetail.setRepaymentDate(LocalDateTime.now());
            // 请求流水号
            repaymentDetail.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
            // 未同步
            repaymentDetail.setStatus(StatusCode.STATUS_OUT.getCode());
            // 保存数据
            repaymentDetailMapper.insert(repaymentDetail);
        }

        return repaymentDetail;
    }

    @Override
    public void executeRepayment(String date) {
        //查询到期的还款计划
        List<RepaymentPlan> repaymentPlanList = selectDueRepayment(date);

        //生成还款明细(未同步)
        repaymentPlanList.forEach(repaymentPlan -> {
            RepaymentDetail repaymentDetail = saveRepaymentDetail(repaymentPlan);

            //还款预处理
            String preRequestNo = repaymentDetail.getRequestNo();
            Boolean preRepaymentResult = preRepayment(repaymentPlan, preRequestNo);
            if (preRepaymentResult) {
                // 未完待续... ...
                System.out.println("还款预处理成功");
            }
        });

    }

    @Override
    public Boolean preRepayment(RepaymentPlan repaymentPlan, String preRequestNo) {
        //1. 构造请求数据
        UserAutoPreTransactionRequest userAutoPreTransactionRequest = generateUserAutoPreTransactionRequest(repaymentPlan, preRequestNo);

        //2. 请求存管代理服务
        RestResponse<String> restResponse = depositoryAgentApiAgent.userAutoPreTransaction(userAutoPreTransactionRequest);

        //3. 返回结果
        return restResponse.getResult().equals(DepositoryReturnCode.RETURN_CODE_00000.getCode());
    }

    /**
     * 构造存管代理服务预处理请求数据
     *
     * @param repaymentPlan
     * @param preRequestNo
     * @return
     */
    private UserAutoPreTransactionRequest generateUserAutoPreTransactionRequest(RepaymentPlan repaymentPlan, String preRequestNo) {
        // 构造请求数据
        UserAutoPreTransactionRequest userAutoPreTransactionRequest = new UserAutoPreTransactionRequest();
        // 冻结金额
        userAutoPreTransactionRequest.setAmount(repaymentPlan.getAmount());
        // 预处理业务类型
        userAutoPreTransactionRequest.setBizType(PreprocessBusinessTypeCode.REPAYMENT.getCode());
        // 标的号
        userAutoPreTransactionRequest.setProjectNo(repaymentPlan.getProjectNo());
        // 请求流水号
        userAutoPreTransactionRequest.setRequestNo(preRequestNo);
        // 标的用户编码
        userAutoPreTransactionRequest.setUserNo(repaymentPlan.getUserNo());
        // 关联业务实体标识
        userAutoPreTransactionRequest.setId(repaymentPlan.getId());
        // 返回结果
        return userAutoPreTransactionRequest;
    }

    //保存还款计划到数据库
    public List<RepaymentPlan> saveRepaymentPlan(ProjectDTO projectDTO, EqualInterestRepayment fixedRepayment) {
        List<RepaymentPlan> repaymentPlanList = new ArrayList<>();
        // 获取每期利息
        final Map<Integer, BigDecimal> interestMap = fixedRepayment.getInterestMap();
        // 平台收取利息
        final Map<Integer, BigDecimal> commissionMap = fixedRepayment.getCommissionMap();
        // 获取每期本金
        fixedRepayment.getPrincipalMap().forEach((k, v) -> {
            // 还款计划封装数据
            final RepaymentPlan repaymentPlan = new RepaymentPlan();
            // 标的id
            repaymentPlan.setProjectId(projectDTO.getId());
            // 发标人用户标识
            repaymentPlan.setConsumerId(projectDTO.getConsumerId());
            // 发标人用户编码
            repaymentPlan.setUserNo(projectDTO.getUserNo());
            // 标的编码
            repaymentPlan.setProjectNo(projectDTO.getProjectNo());
            // 期数
            repaymentPlan.setNumberOfPeriods(k);
            // 当期还款利息
            repaymentPlan.setInterest(interestMap.get(k));
            // 还款本金
            repaymentPlan.setPrincipal(v);
            // 本息 = 本金 + 利息
            repaymentPlan.setAmount(repaymentPlan.getPrincipal().add(repaymentPlan.getInterest()));
            // 应还时间 = 当前时间 + 期数( 单位月 )
            repaymentPlan.setShouldRepaymentDate(DateUtil.localDateTimeAddMonth(DateUtil.now(), k));
            // 应还状态, 当前业务为待还
            repaymentPlan.setRepaymentStatus("0");
            // 计划创建时间
            repaymentPlan.setCreateDate(DateUtil.now());
            // 设置平台佣金( 借款人让利 ) 注意这个地方是 具体佣金
            repaymentPlan.setCommission(commissionMap.get(k));
            // 保存到数据库
            planMapper.insert(repaymentPlan);
            repaymentPlanList.add(repaymentPlan);
        });
        return repaymentPlanList;
    }

    //保存应收明细到数据库
    private void saveRreceivablePlan(RepaymentPlan repaymentPlan,
                                     TenderDTO tender,
                                     EqualInterestRepayment receipt) {
        // 应收本金
        final Map<Integer, BigDecimal> principalMap = receipt.getPrincipalMap();
        // 应收利息
        final Map<Integer, BigDecimal> interestMap = receipt.getInterestMap();
        // 平台收取利息
        final Map<Integer, BigDecimal> commissionMap = receipt.getCommissionMap();
        // 封装投资人应收明细
        ReceivablePlan receivablePlan = new ReceivablePlan();
        // 投标信息标识
        receivablePlan.setTenderId(tender.getId());
        // 设置期数
        receivablePlan.setNumberOfPeriods(repaymentPlan.getNumberOfPeriods());
        // 投标人用户标识
        receivablePlan.setConsumerId(tender.getConsumerId());
        // 投标人用户编码
        receivablePlan.setUserNo(tender.getUserNo());
        // 还款计划项标识
        receivablePlan.setRepaymentId(repaymentPlan.getId());
        // 应收利息
        receivablePlan.setInterest(interestMap.get(repaymentPlan.getNumberOfPeriods()));
        // 应收本金
        receivablePlan.setPrincipal(principalMap.get(repaymentPlan.getNumberOfPeriods()));
        // 应收本息 = 应收本金 + 应收利息
        receivablePlan.setAmount(receivablePlan.getInterest().add(receivablePlan.getPrincipal()));
        // 应收时间
        receivablePlan.setShouldReceivableDate(repaymentPlan.getShouldRepaymentDate());
        // 应收状态, 当前业务为未收
        receivablePlan.setReceivableStatus(0);
        // 创建时间
        receivablePlan.setCreateDate(DateUtil.now());
        // 设置投资人让利, 注意这个地方是具体佣金
        receivablePlan.setCommission(commissionMap.get(repaymentPlan.getNumberOfPeriods()));
        // 保存到数据库
        receivablePlanMapper.insert(receivablePlan);
    }
}
