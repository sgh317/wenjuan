package com.wenjuan.controller;

import com.wenjuan.bean.MessageBean;
import com.wenjuan.dao.AppVersionMapper;
import com.wenjuan.dao.OcenterDistrictMapper;
import com.wenjuan.model.User;
import com.wenjuan.model.VerifyCode;
import com.wenjuan.service.VerifyCodeService;
import com.wenjuan.utils.ApplicationContextUtil;
import com.wenjuan.utils.CheckUtil;
import com.wenjuan.utils.JavaSmsApi;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/index")
public class IndexController {
    @Resource
    private AppVersionMapper appVersionMapper;

    @Resource
    private OcenterDistrictMapper ocenterDistrictMapper;

    @Autowired
    @Qualifier("verifyCodeService")
    private VerifyCodeService verifyCodeService;

    /**
     * 检查服务器版本
     *
     * @param platCode 平台号，apple为2，android为1
     * @return
     */
    @RequestMapping("/checkVersion")
    public MessageBean checkVersion(@RequestParam int platCode) {
        MessageBean messageBean = MessageBean.SUCCESS.clone();
        messageBean.setExtra(appVersionMapper.selectLastVersion(platCode));
        return messageBean;
    }

    /**
     * 获取地址，只支持中国
     *
     * @param level      等级。1为省、直辖市级，2为市级，3为区级，默认为1
     * @param parentCode 父级代码、level为1时不需设置。默认为-1
     * @return
     */
    @RequestMapping("/location")
    public MessageBean getLocation(@RequestParam(defaultValue = "1") int level, @RequestParam(defaultValue = "-1") int parentCode) {
        MessageBean messageBean = MessageBean.SUCCESS.clone();
        messageBean.setExtraList(ocenterDistrictMapper.selectByLevel(level, parentCode).toArray());
        return messageBean;
    }

    @RequestMapping("/testEncode")
    public MessageBean testEncode(@RequestParam String body) {
        MessageBean messageBean = MessageBean.SUCCESS.clone();
        messageBean.setExtra(body);
        return messageBean;
    }

    /**
     * 发送短信验证码
     *
     * @param type 0为用户注册验证
     * @return
     */
    @RequestMapping("/sendVerifyCode")
    public MessageBean sendVerifyCode(@RequestParam String tel, @RequestParam byte type) {
        if (!CheckUtil.isLegalTel(tel)) {
            return MessageBean.getInvalidField("电话号码");
        }
        User user = ApplicationContextUtil.getUserService().selectByName(tel);
        if (user != null) {
            return MessageBean.USER_ALREADY_EXIST;
        }
        MessageBean messageBean;
        VerifyCode verifyCode = verifyCodeService.selectValidVerifyCodeByTel(tel, type);
        if (verifyCode == null) {
            verifyCode = new VerifyCode();
            verifyCode.setUsage(type);
            verifyCode.setTel(tel);
            verifyCode.setCode(RandomStringUtils.random(4, false, true));
            if (JavaSmsApi.SendRegisterCode(tel, verifyCode.getCode())) {
                messageBean = MessageBean.SUCCESS.clone();
                messageBean.setExtra(verifyCode.getCode());
                verifyCodeService.insert(verifyCode);
            } else {
                messageBean = MessageBean.SEND_SMS_ERROR;
            }
        } else {
            messageBean = MessageBean.SUCCESS.clone();
            messageBean.setExtra(verifyCode.getCode());
        }
        return messageBean;
    }

}
