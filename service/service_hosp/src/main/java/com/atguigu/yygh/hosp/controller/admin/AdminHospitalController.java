package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月21日 11:45
 */
@Api("医院相关接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class AdminHospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * @description:  带条件的分页查询
     * @author 靳雪超
     * @date: 2022/9/21 12:10
     * @param pageNum
     * @param pageSize
     * @param hospitalQueryVo
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("带条件的分页查询")
    @GetMapping("/page/{pageNum}/{pageSize}")
    public R pageByHospital(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HospitalQueryVo hospitalQueryVo){

        List<Hospital> list=hospitalService.pageByHospital(pageNum,pageSize,hospitalQueryVo);
        return R.ok().data("total", list.size()).data("list", list);
    }

    /**
     * @description:  更新医院的上线状态
     * @author 靳雪超
     * @date: 2022/9/21 20:34
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("更新医院的上线状态")
    @GetMapping("/update/{id}/{status}")
    public R updateStatus(@PathVariable("id") String id, @PathVariable("status")Integer status){

        if (id == null || "".equals(id) || (status!=0 && status !=1) || !hospitalService.updateStatus(id,status)){
            return R.error();
        }

       return R.ok();
    }
    
    /** 
     * @description:  展示医院详情功能接口
     * @author 靳雪超
     * @date: 2022/9/21 21:08  
     * @return com.atguigu.yygh.common.result.R 
     */
    @ApiOperation("展示医院详情功能接口")
    @GetMapping("/show/{id}")
    public R showHospitalInfo(@PathVariable("id") String id){

        if (id == null || "".equals(id)){
            return R.error();
        }

        return R.ok().data("hospital", hospitalService.showHospitalInfo(id));
    }
}
