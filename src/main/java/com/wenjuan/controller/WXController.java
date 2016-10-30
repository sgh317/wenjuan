package com.wenjuan.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wenjuan.bean.MessageBean;
import com.wenjuan.model.User;
import com.wenjuan.service.WXService;
import com.wenjuan.wx.AccessToken;
import com.wenjuan.wx.WechatSign;
import com.wenjuan.wx.WeixinUtil;

//@RestController
//@RequestMapping(value = "/wx")
public class WXController {
    Logger logger = Logger.getLogger(WXController.class);
    
//    @Autowired
//    @Qualifier("wxService")
    private WXService wxService;
    
    /**
     * 获取微信配置信息
     * @param request
     * @return
     */
//    @RequestMapping(value = "/configdata")
//    public MessageBean getWXConfigData(HttpServletRequest request)
//    {
//        MessageBean messageBean = MessageBean.SUCCESS.clone();
//        
//        try {
//			String url = request.getRequestURL().toString();
//			
//
//			AccessToken token = WeixinUtil.getAccessToken(WeixinUtil.APP_ID, WeixinUtil.APP_SECRET);
//			System.out.println(token.getExpiresIn());
//			System.out.println(token.getToken());
//
//        	String ticket = WeixinUtil.getJsapiTicket(token.getToken());
//	        	
//        	Map<String, String> res = WechatSign.sign(ticket, url);
//        	System.out.println(res);
//	        messageBean.setExtra(res);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        
//        return messageBean;
//    }
    
//    
//    /**
//     * 微信OAuth2.0授权 登陆（自动注册）
//     * @param request
//     * @return
//     */
//    @RequestMapping("/authdeny2")
//    public MessageBean authdeny2(HttpServletRequest request) {
//        MessageBean messageBean = null;
//        
//        String code = request.getParameter("code");
//        String state = request.getParameter("state");
//        
//        System.out.println("code: " + code);	// 获取用户信息凭证（5分钟内有效）
//        System.out.println("state: " + state);	// url内所带参数（后期可作为跳转URL的key使用）
//        
//        if(!"authdeny".equals(code)){
//            String access_token =  WeixinUtil.getAccessToken(WeixinUtil.APP_ID, WeixinUtil.APP_SECRET).getToken();
//            Map<String, Object> user = wxService.getLoginAuthInfo(access_token, code, state); //第3步
//        }
//        
//        return messageBean;
//    }

}
