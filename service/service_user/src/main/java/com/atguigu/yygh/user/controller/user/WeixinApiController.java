package com.atguigu.yygh.user.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.user.prop.WechatProperties;
import com.atguigu.yygh.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月28日 11:19
 */
@Api
@Controller
@RequestMapping("/user/userinfo/wechat")
public class WeixinApiController {

    @Autowired
    private WechatProperties wechatProperties;
    @Autowired
    private UserInfoService userInfoService;

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 得到微信登录的参数
     * @author 靳雪超
     * @date: 2022/9/28 11:21
     */
    @ApiOperation("得到微信登录的参数")
    @RequestMapping("/login")
    @ResponseBody
    public R getWeChatLoginParam() throws UnsupportedEncodingException {

        String uri = URLEncoder.encode(wechatProperties.getRedirecturl(), "utf-8");

        Map<String, Object> map = new HashMap<>();
        map.put("appid", wechatProperties.getAppid());
        map.put("scope", "snsapi_login");
        map.put("redirect_uri", uri);
        map.put("state", System.currentTimeMillis() + "");

        return R.ok().data(map);
    }


    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 微信服务器的回调接口
     * @author 靳雪超是个屁
     * @date: 2022/9/28 14:09
     */
    @ApiOperation("微信服务器的回调接口")
    @GetMapping("/callback")
    public String callback(String code, String state) throws Exception {

        String appid = wechatProperties.getAppid();
        String appsecret = wechatProperties.getAppsecret();
        return userInfoService.loginWechat(code, state, appid, appsecret);
    }
}
