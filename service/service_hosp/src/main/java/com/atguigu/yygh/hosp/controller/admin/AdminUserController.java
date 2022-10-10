package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.acl.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月15日 20:44
 */
@RestController
@RequestMapping("/admin/user")
@Api(tags = "用户登录")
public class AdminUserController {

    /**
     * @description:  校验用户名密码，并返回token信息
     * @author 靳雪超
     * @date: 2022/9/15 20:50
     * @param user
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("校验用户名密码，并返回token信息")
    @PostMapping("/login")
    public R login(@ApiParam(name = "user",value = "用户名，密码") @RequestBody User user){

        return R.ok().data("token", "admin-token");
    }

    /**
     * @description:  校验携带的token信息
     * @author 靳雪超
     * @date: 2022/9/15 20:57
     * @param token
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("校验携带的token信息")
    @GetMapping("/info")
    public R info(@ApiParam(name = "token",value = "携带的token") @RequestParam("token") String token){

        return R.ok()
                .data("roles", "[admin]")
                .data("introduction", "I am a super administrator")
                .data("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name", "Super Admin");
    }
}
