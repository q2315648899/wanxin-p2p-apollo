package cn.itcast.wanxinp2p.transaction.service;

import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;

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

}