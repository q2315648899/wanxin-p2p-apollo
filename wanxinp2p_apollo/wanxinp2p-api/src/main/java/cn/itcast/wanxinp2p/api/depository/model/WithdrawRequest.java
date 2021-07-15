package cn.itcast.wanxinp2p.api.depository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <P>
 * 封装提现请求数据
 * </p>
 */
@Data
@Accessors(chain = true)
@ApiModel("用户提现请求信息")
public class WithdrawRequest {

	@ApiModelProperty("标识")
	private Long id;

	@ApiModelProperty("用户编号")
	private String userNo;

	@ApiModelProperty("请求流水号")
	private String requestNo;

	@ApiModelProperty("提现金额")
	private BigDecimal amount;

	@ApiModelProperty("平台佣金")
	private BigDecimal commission;

	@ApiModelProperty("页面回调URL")
	private String callbackURL;

	@ApiModelProperty("银行卡号")
	private String cardNumber;

	@ApiModelProperty("银行预留手机号")
	private String mobile;
}
