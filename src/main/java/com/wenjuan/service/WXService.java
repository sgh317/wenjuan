package com.wenjuan.service;

import java.util.Map;

/**
 * 微信相关服务
 * Created by DiGua
 */
public interface WXService {

	/**
     * 根据code获取成员信息，和重定向连接
     * @param access_token 调用接口凭证
     * @param code   通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @param msg   url内所带参数（后期可作为跳转URL的key使用）
     */
	public Map<String, Object> getLoginAuthInfo(String accessToken, String code, String msg);
}
