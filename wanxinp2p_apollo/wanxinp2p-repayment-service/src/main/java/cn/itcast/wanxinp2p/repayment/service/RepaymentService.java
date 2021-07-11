package cn.itcast.wanxinp2p.repayment.service;

import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;
import cn.itcast.wanxinp2p.repayment.entity.RepaymentDetail;
import cn.itcast.wanxinp2p.repayment.entity.RepaymentPlan;

import java.util.List;

public interface RepaymentService {

    /**
     * 启动还款
     * @param projectWithTendersDTO
     * @return
     */
    String startRepayment(ProjectWithTendersDTO projectWithTendersDTO);

    /**
     * 查询到期还款计划
     * @param date 格式为：yyyy-MM-dd
     * @return
     */
    List<RepaymentPlan> selectDueRepayment(String date);

    /**
     * 根据还款计划生成还款明细并保存
     * @param repaymentPlan
     * @return
     */
    RepaymentDetail saveRepaymentDetail(RepaymentPlan repaymentPlan);

    /**
     * 执行还款
     */
    void executeRepayment(String date);


}
