package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月27日 9:07
 */
@Api(tags = "用户医院科室显示接口")
@RestController
@RequestMapping("/user/hosp/department")
public class UserDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation("")
    @GetMapping("/findAll/{hoscode}")
    public R findAllDepartment(@PathVariable("hoscode") String hoscode) {

        List<DepartmentVo> departmentList = departmentService.getDepartmentList(hoscode);

        return R.ok().data("list", departmentList);
    }
}
