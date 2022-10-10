package com.atguigu.yygh.hosp.controller.admin;

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
 * @date 2022年09月22日 18:38
 */
@Api("排班信息树")
@RestController
@RequestMapping("/admin/hosp/department")

public class AdminDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation("")
    @GetMapping("/getDepartmentList/{hoscode}")
    public R getDepartmentList(@PathVariable("hoscode") String hoscode) {

        List<DepartmentVo> list = departmentService.getDepartmentList(hoscode);
        return R.ok().data("list", list);
    }
}
