package com.atguigu.yygh.hosp.controller.admin;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.common.util.MD5;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author jxc
 * @since 2022-09-14
 */
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@Api(tags = "医院设置信息")
public class AdminHospitalSetController {

    //http://localhost:8201/admin/hosp/hospitalSet
    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * @description: 返回所有医院信息
     * @author 靳雪超
     * @date: 2022/9/14 14:24
 * @return java.util.List<com.atguigu.yygh.model.hosp.HospitalSet>
     */
    @ApiOperation("查询所有的医院设置信息")
    @GetMapping("/findAll")
    public R findAll(){
        List<HospitalSet> list = hospitalSetService.list();

        return R.ok().data("items", list);
    }
    
    /**
     * @description: 通过id删除医院信息 
     * @author 靳雪超
     * @date: 2022/9/14 15:52
 * @param id 
 * @return boolean 
     */
    @ApiOperation("通过id删除医院信息")
    @DeleteMapping("/deleteById/{id}")
    public R deleteById(@ApiParam(name = "id",value = "医院id")
                                  @PathVariable("id") Long id){

        return R.ok().data("删除状态", hospitalSetService.removeById(id));
    }

    /**
     * @description:  带查询条件的分页
     * @author 靳雪超
     * @date: 2022/9/15 11:39
     * @param pageNum
     * @param pageSize
     * @param hospitalSetQueryVo
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("带查询条件的分页")
    @PostMapping("/page/{pageNum}/{pageSize}")
    public R getPageInfo(@ApiParam(name = "pageNum",value = "当前页码") @PathVariable("pageNum") Integer pageNum ,
                         @ApiParam(name = "pageSize",value = "每页的的记录数") @PathVariable("pageSize") Integer pageSize,
                         @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        Page<HospitalSet> page=new Page<>();
        QueryWrapper<HospitalSet> queryWrapper=new QueryWrapper();
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())){
            queryWrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())){
            queryWrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }

        hospitalSetService.page(page, queryWrapper);
        return R.ok().data("total", page.getTotal()).data("list", page.getRecords());
    }

    /**
     * @description:  新增医院设置
     * @author 靳雪超
     * @date: 2022/9/15 12:30
     * @param hospitalSet
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("新增医院设置")
    @PostMapping("/save")
    public R saveHospitalSet(@ApiParam(name = "hospitalSet",value = "hospitalSet实体") @RequestBody HospitalSet hospitalSet){

        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);

        //签名秘钥
        Random random=new Random();
        hospitalSet.setSignKey(MD5.encrypt((System.currentTimeMillis()+""+random.nextInt(1000))));
        return R.ok().data("保存成功状态", hospitalSetService.save(hospitalSet));
    }

    /**
     * @description:  通过id查询，回显数据
     * @author 靳雪超
     * @date: 2022/9/15 12:32
     * @param id
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("通过id查询，回显数据")
    @GetMapping("/getHospSet/{id}")
    public R getById(@ApiParam(name = "id", value = "医院id", required = true) @PathVariable("id") Long id){

        return R.ok().data("list", hospitalSetService.getById(id));
    }

    /**
     * @description:  根据ID修改医院设置
     * @author 靳雪超
     * @date: 2022/9/15 12:34
     * @param hospitalSet
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("根据ID修改医院设置")
    @PutMapping("/updateHospSet")
    public R updateById(@ApiParam(name = "hospitalSet", value = "医院设置对象", required = true) @RequestBody HospitalSet hospitalSet){

        return R.ok().data("更新状态", hospitalSetService.updateById(hospitalSet));
    }

    /**
     * @description:  更改锁定状态
     * @author 靳雪超
     * @date: 2022/9/15 14:07
     * @param id
     * @param status
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("更改锁定状态")
    @PutMapping("/updateStatus/{id}/{status}")
    public R updateStatus(@ApiParam(name = "",value = "") @PathVariable("id") Long id,
                          @ApiParam(name="",value = "") @PathVariable("status") Integer status){
        HospitalSet hospitalSet=new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok().data("更改操作", hospitalSetService.updateById(hospitalSet));
    }

    /**
     * @description:  批量删除医院设置
     * @author 靳雪超
     * @date: 2022/9/15 14:11
     * @param ids
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("批量删除医院设置")
    @DeleteMapping("/deleteBatch")
    public R deleteBatch(@RequestBody List<Long> ids){

        return R.ok().data("批量删除操作", hospitalSetService.removeByIds(ids));
    }
}

