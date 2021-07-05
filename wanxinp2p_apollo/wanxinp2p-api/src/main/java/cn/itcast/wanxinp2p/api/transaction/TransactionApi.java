package cn.itcast.wanxinp2p.api.transaction;

import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

/**
 * 交易中心服务API
 *
 * @Author: wong
 * @Date: 2021/7/5
 */
public interface TransactionApi {

    /**
     * 借款人发标
     * @param projectDTO
     * @return
     */
    RestResponse<ProjectDTO> createProject(ProjectDTO projectDTO);

}
