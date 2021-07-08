package cn.itcast.wanxinp2p.transaction.service;

import cn.itcast.wanxinp2p.api.transaction.model.*;
import cn.itcast.wanxinp2p.common.domain.PageVO;

import java.util.List;

/**
 * <P>
 * 交易中心service接口
 * </p>
 */
public interface ProjectService {
    /**
     * 创建标的
     * *
     @param project
      * @return ProjectDTO
     */
    ProjectDTO createProject(ProjectDTO project);

    /**
     * 根据分页条件检索标的信息
     * *
     @param projectQueryDTO
      * @param order 升序或降序
     * @param pageNo 当前页数
     * @param pageSize 每页记录数
     * @param sortBy 依赖的排序字段
     * @return
     */
    PageVO<ProjectDTO> queryProjectsByQueryDTO(ProjectQueryDTO projectQueryDTO,
                                               String order, Integer pageNo, Integer pageSize, String sortBy);

    /**
     * 管理员审核标的信息
     *
     * @param id 标的id
     * @param approveStatus 审批状态
     * @return String
     */
    String projectsApprovalStatus(Long id, String approveStatus);

    /**
     * 标的信息检索
     * @param projectQueryDTO
     * @param order
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    PageVO<ProjectDTO> queryProjects(ProjectQueryDTO projectQueryDTO, String order, Integer pageNo, Integer pageSize, String sortBy);

    /**
     * 通过ids获取多个标的（ids：可能是多个标的id拼接在一起的字符串）
     * @param ids
     * @return
     */
    List<ProjectDTO> queryProjectsIds(String ids);

    /**
     * 根据标的id查询投标记录
     * @param id
     * @return
     */
    List<TenderOverviewDTO> queryTendersByProjectId(Long id);

    /**
     * 用户投标
     * @param projectInvestDTO
     * @return
     */
    TenderDTO createTender(ProjectInvestDTO projectInvestDTO);
}