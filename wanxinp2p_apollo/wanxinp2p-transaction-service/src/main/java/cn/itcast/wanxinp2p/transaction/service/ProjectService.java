package cn.itcast.wanxinp2p.transaction.service;

import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectQueryDTO;
import cn.itcast.wanxinp2p.common.domain.PageVO;

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

}