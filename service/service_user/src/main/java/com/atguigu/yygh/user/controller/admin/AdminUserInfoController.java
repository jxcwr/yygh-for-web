package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月04日 20:41
 */
@Api(tags = "管理员对就诊人管理，认证相关接口")
@RestController()
@RequestMapping("/admin/userinfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * @param page
     * @param limit
     * @param userInfoQueryVo
     * @return com.atguigu.yygh.common.result.R
     * @description: 用户列表（条件查询带分页）
     * @author 靳雪超
     * @date: 2022/10/4 20:47
     */
    @ApiOperation("用户列表（条件查询带分页）")
    @GetMapping("/{page}/{limit}")
    public R page(@PathVariable Long page,
                  @PathVariable Long limit,
                  UserInfoQueryVo userInfoQueryVo) {

        IPage<UserInfo> userInfoPage = userInfoService.selectPage(page, limit, userInfoQueryVo);
//        return R.ok().data("page", userInfoPage.getRecords()).data("total", userInfoPage.getTotal());
        return R.ok().data("pageModel", userInfoPage);
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 管理员而更改用户的锁定状态
     * @author 靳雪超
     * @date: 2022/10/5 9:16
     */
    @ApiOperation("管理员而更改用户的锁定状态")
    @GetMapping("/lock/{id}/{status}")
    public R lockStatus(@PathVariable("id") Long userId,
                        @PathVariable("status") Integer status) {

        userInfoService.lock(userId, status);
        return R.ok();
    }

    /**
     * @param userId
     * @return com.atguigu.yygh.common.result.R
     * @description: 查询用户的详细信息和他添加的就诊人信息
     * @author 靳雪超
     * @date: 2022/10/5 9:26
     */
    @ApiOperation("查询用户的详细信息和他添加的就诊人信息")
    @GetMapping("/show/{id}")
    public R showUserInfoAndPatient(@PathVariable("id") Long userId) {
        Map<String, Object> map = userInfoService.showUserInfoAndPatient(userId);
        return R.ok().data(map);
    }

    @ApiOperation("认证审批")
    @GetMapping("approval/{userId}/{authStatus}")
    public R approval(@PathVariable("userId") Long userId,
                      @PathVariable("authStatus") Integer authStatus) {

        userInfoService.approval(userId, authStatus);
        return R.ok();
    }
}
