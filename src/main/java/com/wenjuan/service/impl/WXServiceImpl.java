package com.wenjuan.service.impl;

import com.wenjuan.dao.UserMapper;
import com.wenjuan.dao.UserSessionMapper;
import com.wenjuan.model.User;
import com.wenjuan.model.UserView;
import com.wenjuan.service.UserService;
import com.wenjuan.service.WXService;
import com.wenjuan.utils.HxUtil;
import com.wenjuan.vo.UserBriefInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service("wxService")
public class WXServiceImpl implements WXService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserSessionMapper userSessionMapper;

    private Random random = new Random(System.currentTimeMillis());

	public String getUserId(String accessToken, String code, String agentId) {
		String UserId = "";
//        CODE_TO_USERINFO = CODE_TO_USERINFO.replace("ACCESS_TOKEN", access_token).replace("CODE", code).replace("AGENTID", agentid);
//        JSONObject jsonobject = WeixinUtil.httpRequest(CODE_TO_USERINFO, "GET", null);
//        if (null != jsonobject) {
//            UserId = jsonobject.getString("UserId");
//            if (!"".equals(UserId)) {
//                System.out.println("获取信息成功，o(∩_∩)o ————UserID:" + UserId);
//            } else {
//                int errorrcode = jsonobject.getInt("errcode");
//                String errmsg = jsonobject.getString("errmsg");
//                String error = "错误码：" + errorrcode + "————" + "错误信息：" + errmsg;
//                log.error(error);
//            }
//        } else {
//            log.error("获取授权失败了");
//        }
        return UserId;
	}

	/**
	 * 还需要考虑重定向URL是在程序内处理还是直接在js内处理
	 */
	@Override
	public Map<String, Object> getLoginAuthInfo(String accessToken, String code, String msg) {
		// 获取openid
		
		// 判断是否已绑定用户信息
		
		// 1、已绑定
//			查询用户信息并返回（根据msg判断跳回至哪个界面）
		
		// 2、未绑定
		
		// 2.1 已有微信相关信息数据
//			直接跳转至信息绑定界面，绑定
		
		// 2.2 无微信相关信息数据
//		 	获取微信用户信息
//			跳转至绑定界面，绑定
		
		
		
		return null;
	}

    
    
}
