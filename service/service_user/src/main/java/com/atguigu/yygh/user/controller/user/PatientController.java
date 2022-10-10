package com.atguigu.yygh.user.controller.user;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.util.JwtHelper;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author jxc
 * @since 2022-10-04
 */
@Api(tags = "就诊人的信息管理")
@RestController
@RequestMapping("/user/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 查询用户添加的就诊人的信息
     * @author 靳雪超
     * @date: 2022/10/4 15:54
     */
    @ApiOperation("查询用户添加的就诊人的信息")
    @GetMapping("/auth/findAll")
    public R findList(@RequestHeader String token) {

        Long userId = JwtHelper.getUserId(token);
        List<Patient> list = patientService.findAllByUserId(userId);
        return R.ok().data("list", list);
    }

    /**
     * @param id
     * @return com.atguigu.yygh.common.result.R
     * @description: 根据id查询就诊人信息
     * @author 靳雪超
     * @date: 2022/10/4 16:01
     */
    @ApiOperation("根据id查询就诊人信息")
    @GetMapping("/auth/get/{id}")
    public R getAuthById(@PathVariable("id") Long id) {
        Patient patient = patientService.getAuthDetailById(id);
        return R.ok().data("patient", patient);
    }


    /**
     * @param patient
     * @return com.atguigu.yygh.common.result.R
     * @description: 添加就诊人信息
     * @author 靳雪超
     * @date: 2022/10/4 17:09
     */
    @ApiOperation("添加就诊人信息")
    @PostMapping("/auth/save")
    public R save(@RequestHeader String token, @RequestBody Patient patient) {
        Long userId = JwtHelper.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 修改就诊人信息
     * @author 靳雪超
     * @date: 2022/10/4 17:10
     */
    @ApiOperation("修改就诊人信息")
    @PostMapping("/auth/update")
    public R updateById(@RequestBody Patient patient) {
        patientService.saveOrUpdate(patient);
        return R.ok();
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 修改就诊人信息
     * @author 靳雪超
     * @date: 2022/10/4 17:14
     */
    @ApiOperation("修改就诊人信息")
    @DeleteMapping("/auth/remove/{id}")
    public R removeById(@PathVariable("id") Long id) {
        patientService.removeById(id);
        return R.ok();
    }

    /**
     * @param id
     * @return com.atguigu.yygh.model.user.Patient
     * @description: 获取就诊人
     * @author 靳雪超
     * @date: 2022/10/5 21:16
     */
    @ApiOperation(value = "获取就诊人")
    @GetMapping("/inner/get/{id}")
    public Patient getPatientOrder(
            @ApiParam(name = "id", value = "就诊人id", required = true)
            @PathVariable("id") Long id) {
        return patientService.getById(id);
    }
}

