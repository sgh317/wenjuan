package com.wenjuan.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wenjuan.bean.MessageBean;
import com.wenjuan.dao.UserLoginLogMapper;
import com.wenjuan.dao.UserMapper;
import com.wenjuan.dao.WeiXinMapper;
import com.wenjuan.model.User;
import com.wenjuan.model.UserLoginLog;
import com.wenjuan.model.UserSession;
import com.wenjuan.model.WeiXin;
import com.wenjuan.service.UserGroupService;
import com.wenjuan.service.UserService;
import com.wenjuan.service.UserSessionService;
import com.wenjuan.wx.WechatSign;
import com.wenjuan.wx.WeixinUtil;

import net.sf.json.JSONObject;

/**
 * Created by YG on 2016/8/22.
 */
@RestController
@RequestMapping(value = "/wx")
public class WeiXinController {
    Logger logger = Logger.getLogger(WeiXinController.class);
    
    @Autowired
    UserMapper userMapper;
    @Autowired
    WeiXinMapper weixinMapper;

    @Autowired
    @Qualifier("userService")
    private UserService userService;
    @Autowired
    @Qualifier("userGroupService")
    private UserGroupService userGroupService;

    @Autowired
    @Qualifier("userSessionService")
    private UserSessionService userSessionService;
    @Resource
    private UserLoginLogMapper userLoginLogMapper;

    /**
     * 微信 判断用户是否存在  返回账号密码
     * @param req
     * @param res
     * @param openid
     * @param ACCESS_TOKEN
     * @param addToAllGroup
     * @throws Exception
     */
    @RequestMapping("wxLogin")
    public void login(HttpServletRequest req,HttpServletResponse res,String openid,String ACCESS_TOKEN, @RequestParam(defaultValue = "true") boolean addToAllGroup)throws Exception{
        JSONObject jo=new JSONObject();
        WeiXin wx=weixinMapper.selectAll(openid);
        if(wx!=null){
            //openid 已注册
            if(wx.getUserid()!=null){
                //已绑定
                User user=userService.selectByPrimaryKey(Integer.parseInt(wx.getUserid()));
                jo.put("msg","success");
                jo.put("userName",user.getName());
                jo.put("passWord",user.getPassword());
                res.getWriter().write(jo.toString());
            }else{
                //未绑定号码的用户  已用openid注册过的用户
                jo.put("msg","success");
                jo.put("userName",wx.getOpenId());
                jo.put("passWord","123456789");
                res.getWriter().write(jo.toString());
            }
        }else{
            //openid 未注册
           /* String url="https://api.weixin.qq.com/sns/userinfo";
            String param="access_token="+ACCESS_TOKEN+"&openid="+openid+"&lang=zh_CN";
            HttpUtil H=new HttpUtil();
            String result=H.sendGet(url,param);
            //TODO: 2016/8/29   获取基本信息
            System.out.println("***"+result);
            JSONObject obj = new JSONObject().fromObject(result);
            */
            Map<String, Object> obj = WeixinUtil.getUserInfo(openid, ACCESS_TOKEN);
            logger.info("=======================================================");
            logger.info(obj);
            logger.info(String.valueOf(obj.get("privilege")));
            WeiXin wxUser=new WeiXin();
            wxUser.setNickname(String.valueOf(obj.get("nickname")));
            wxUser.setOpenId(String.valueOf(obj.get("openid")));
            wxUser.setCity(String.valueOf(obj.get("city")));
            wxUser.setCountry(String.valueOf(obj.get("country")));
            wxUser.setPrivilege(String.valueOf(obj.get("privilege")));
            wxUser.setProvince(String.valueOf(obj.get("province")));
            wxUser.setSex(String.valueOf(obj.get("sex")));
            wxUser.setHeadImgURL(String.valueOf(obj.get("headimgurl")));
            wxUser.setUnionId(String.valueOf(obj.get("unionid")));
            wxUser.setAccessToken(ACCESS_TOKEN);
            
            weixinMapper.insert(wx);
            //用户注册
            User user=new User();
            user.setId(null);
            user.setScore(null);
            user.setRegisterTime(null);
            user.setAllowArticle(null);
            user.setPassword("123456789");
            user.setName(wx.getOpenId());
            user.setNickname(wx.getNickname());
            user.setAddBackground(false);
            user.setRole(1);
            user.setLocation(wx.getCity());
                int effectRow = userService.insert(user);
                if (addToAllGroup) {
                    userGroupService.addUserToAllGroup(user.getId());
                }
                if (effectRow > 0) {
                    //添加扩展列默认值
                    propertyController ec = new propertyController();
                    ec.insertUser(user.getName());
                }
            //未绑定号码的用户  已用openid注册过的用户
            jo.put("msg","success");
            jo.put("userName",wx.getOpenId());
            jo.put("passWord","123456789");
            res.getWriter().write(jo.toString());
        }

        }

    /**
     * 绑定用户与微信
     * @param tel  需要绑定的号码
     * @param name 当前登录用户名
     * @param password 密码
     */
    @RequestMapping("/wxRgister")
    public void UserOrWeiXin(String tel,String name,@RequestParam(required = false) String password,HttpServletResponse res)throws  Exception{
        User user=userService.selectByName(tel);

        WeiXin wx=weixinMapper.selectAll(name);
        if(user!=null){
            //老用户更新
            wx.setUserid(user.getId().toString());
            weixinMapper.UpdateUserid(wx);
        }else{
            user=userService.selectByName(name);
            //新用户更新openid为账号的用户  及更新opeinid  的userid
            user.setName(tel);
            user.setPassword(password);
            user.setScore(null);
            user.setRegisterTime(null);
            user.setAllowArticle(null);
            user.setAddBackground(false);
            userService.updateByPrimaryKey(user);
            wx.setUserid(user.getId().toString());
            weixinMapper.UpdateUserid(wx);

        }

        res.getWriter().write("success");

    }
//区分新老用户
    @RequestMapping("/User")
    public void User(String tel,HttpServletResponse res)throws  Exception{
     User user=userService.selectByName(tel);
        if(user!=null){
            res.getWriter().write("1");
        }else{
            res.getWriter().write("2");
        }
    }
    
    
    /**
     * 获取微信配置信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/configdata")
    public MessageBean getWXConfigData(HttpServletRequest request)
    {
        MessageBean messageBean = MessageBean.SUCCESS.clone();
        
        try {
			String url = request.getRequestURL().toString();
			
        	String ticket = WeixinUtil.getJsapiTicket();
	        	
        	Map<String, String> res = WechatSign.sign(ticket, url);
        	logger.info(res);
	        messageBean.setExtra(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return messageBean;
    }
    

    /**
     * 微信OAuth2.0授权 登陆（自动注册）
     * @param request
     * @return
     * @throws Exception 
     */
    @RequestMapping("/authdeny2")
    public void authdeny2(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String redirectURL = "http://app550.com/h5/vch5/page/tab-webview-main.html";
    	
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        
        logger.info("code: " + code);	// 获取用户信息凭证（5分钟内有效）
        logger.info("state: " + state);	// url内所带参数（后期可作为跳转URL的key使用）
        
        if(!"authdeny".equals(code)){
        	Map<String, Object> atMap = WeixinUtil.getUserAccessToken(code);	// 获取微信授权accessToken
        	// access_token	refresh_token	openid
        	String accessToken = String.valueOf(atMap.get("access_token"));
        	String refreshToken = String.valueOf(atMap.get("refresh_token"));
        	String openId = String.valueOf(atMap.get("openid"));
        	
        	logger.info(atMap);
        	
        	if(openId != null && !"".equals(openId))
        	{
        		WeiXin wx = weixinMapper.selectAll(openId);	// 查询数据库中是否存在openid对应数据
        		
        		// 重新获取，用于更新微信用户信息
            	Map<String, Object> uiMap = WeixinUtil.getUserInfo(openId, accessToken);	// 获取微信授权用户信息
            	logger.info("=======================================================");
            	logger.info(uiMap);
            	logger.info(String.valueOf(uiMap.get("privilege")));
                WeiXin wxUser=new WeiXin();
                wxUser.setNickname(String.valueOf(uiMap.get("nickname")));
                wxUser.setOpenId(String.valueOf(uiMap.get("openid")));
                wxUser.setCity(String.valueOf(uiMap.get("city")));
                wxUser.setCountry(String.valueOf(uiMap.get("country")));
                wxUser.setPrivilege(String.valueOf(uiMap.get("privilege")));
                wxUser.setProvince(String.valueOf(uiMap.get("province")));
                wxUser.setSex(String.valueOf(uiMap.get("sex")));
                wxUser.setHeadImgURL(String.valueOf(uiMap.get("headimgurl")));
                wxUser.setUnionId(String.valueOf(uiMap.get("unionid")));
                wxUser.setAccessToken(accessToken);
                wxUser.setRefreshToken(refreshToken);

                //用户信息
                User user = null;
                boolean isnew = false;
                // openid已登陆过系统
                if(wx != null)
                {
                	// 判断是否已关联用户信息
                	if(wx.getUserid() != null && !"".equals(wx.getUserid()))
                	{
                		user = userService.selectByPrimaryKey(Integer.parseInt(wx.getUserid()));
                	}
                }
                else
                {
                	wx = wxUser;
                	isnew = true;
                }
                if(user == null)
                {
                	user=new User();
                    user.setId(null);
                    user.setScore(null);
                    user.setRegisterTime(null);
                    user.setAllowArticle(null);
                    user.setPassword("123456789");
                    user.setName(wx.getOpenId());
                    user.setNickname(wx.getNickname());
                    user.setAddBackground(false);
                    user.setRole(1);
                    user.setLocation(wx.getCity());
                    
                    int effectRow = userService.insert(user);
                    userGroupService.addUserToAllGroup(user.getId());	// 添加所有组权限
                    if (effectRow > 0) {
                        //添加扩展列默认值
                        propertyController ec = new propertyController();
                        ec.insertUser(user.getName());
                    }
                }
                wx.setUserid(String.valueOf(user.getId()));
                wx.setLastRefreshTime(new Date());
                if(isnew)
                {
                    weixinMapper.insert(wx);
                }
                else
                {
                	weixinMapper.update(wx);
                }

                // 处理登陆用户信息
                UserSession userSession = userSessionService.selectByPrimaryKey(user.getId());
                if (userSession == null) {
                    String token = org.apache.commons.lang3.RandomStringUtils.random(31, true, true);
                    userSession = new UserSession();
                    userSession.setToken(token);
                    userSession.setUserId(user.getId());
                    userSessionService.insert(userSession);
                }
                try {
                    UserLoginLog userLoginLog = new UserLoginLog();
                    userLoginLog.setUserid(user.getId());
                    userLoginLog.setIp(request.getRemoteAddr());
                    userLoginLogMapper.insert(userLoginLog);
                } catch (RuntimeException r) {
                }
                
                // 处理cookie信息
        		Cookie loginCookie = new Cookie("_wx_token", userSession.getToken());
        		loginCookie.setDomain("ngrok.natapp.cn");
        		loginCookie.setPath("/");
        		loginCookie.setMaxAge(1*24*60*60*1000);	// 1天有效期
        		response.addCookie(loginCookie);
        	}
        	else
        	{
        		redirectURL = "";	// TODO：返回错误界面（微信授权信息获取失败）
        	}
        }
        else
        {
        	redirectURL = "http://www.baidu.com";	// TODO：重定向至错误界面（授权失败）
        }
        
        response.sendRedirect(redirectURL);
    }
}
