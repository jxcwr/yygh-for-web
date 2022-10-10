package com.atguigu.yygh.user.controller.user;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.util.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author jxc
 * @since 2022-09-27
 */
@Api(tags = "用户信息")
@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 用户登录接口
     * @author 靳雪超
     * @date: 2022/9/27 15:11
     */
    @ApiOperation("用户登录接口")
    @PostMapping("/login")
    public R userLogin(@RequestBody LoginVo loginVo) {

        Map<String, Object> map = userInfoService.login(loginVo);

        return R.ok().data(map);
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 获取登录用户的详细信息，做信息认证
     * @author 靳雪超
     * @date: 2022/9/30 17:12
     */
    @ApiOperation("获取登录用户的详细信息，做信息认证")
    @GetMapping("/auth/getUserInfo")
    public R getUserInfoDetail(@RequestHeader String token) {

        UserInfo userInfo = userInfoService.getUserInfoDetail(token);
        return R.ok().data("userInfo", userInfo);
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 用户的实名认证
     * @author 靳雪超
     * @date: 2022/10/4 10:03
     */
    @ApiOperation("用户的实名认证")
    @PutMapping("/auth/userAuth")
    public R userAuth(@ApiParam(name = "token", value = "用户携带的token信息") @RequestHeader String token,
                      @ApiParam(name = "userAuthVo", value = "表单的信息直接封装实体对象") UserAuthVo userAuthVo) {
        Long userId = JwtHelper.getUserId(token);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        BeanUtils.copyProperties(userAuthVo, userInfo);
        userInfo.setAuthStatus(1);
        userInfoService.updateById(userInfo);
        return R.ok();
    }
}

