package com.atguigu.yygh.user.service;


import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author jxc
 * @since 2022-09-27
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    public String loginWechat(String code, String state, String appid, String appsecret) throws Exception;

    UserInfo getUserInfoDetail(String token);

    IPage<UserInfo> selectPage(Long page, Long limit, UserInfoQueryVo userInfoQueryVo);

    void lock(Long userId, Integer status);

    Map<String, Object> showUserInfoAndPatient(Long userId);

    void approval(Long userId, Integer authStatus);
}
