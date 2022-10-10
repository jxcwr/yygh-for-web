package com.atguigu.yygh.cmn.controller;


import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author jxc
 * @since 2022-09-16
 */
@RestController
@RequestMapping("admin/cmn/dict")
@Api(tags = "数据字典")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * @description:  根据父级id查询子级列
     * @author 靳雪超
     * @date: 2022/9/16 14:29
     * @param pid
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("根据父级id查询子级列")
    @GetMapping("/findChildList/{pid}")
    public R findChildList(@ApiParam(name = "pid",value = "父级的id") @PathVariable("pid") Long pid){

        return R.ok().data("list", dictService.getChildListById(pid));
    }

    /**
     * @description:  数据字典的下载
     * @author 靳雪超
     * @date: 2022/9/19 10:23
     * @param response
     */
    @ApiOperation("数据字典Excel的下载")
    @GetMapping("/download")
    public void downloadExcel(HttpServletResponse response){
        dictService.exportData(response);
    }

    /**
     * @description:  数据字典Excel的上传
     * @author 靳雪超
     * @date: 2022/9/19 11:07
     * @return com.atguigu.yygh.common.result.R
     */
    @ApiOperation("数据字典Excel的上传")
    @GetMapping("/upload")
    public R uploadExcel(MultipartFile file){
        return R.ok().data("上传状态", dictService.importData(file));
    }

    /**
     * @description:  远程调用接口，得到医院等级
     * @author 靳雪超
     * @date: 2022/9/21 11:21
     * @param parentDictCode
     * @param value
     * @return java.lang.String
     */
    @ApiOperation("远程调用接口，得到医院等级")
    @GetMapping(value = "/getName/{parentDictCode}/{value}")
    String getName(@PathVariable("parentDictCode") String parentDictCode, @PathVariable("value") Long value){
        return dictService.getName(parentDictCode,value);
    }

    /**
     * @description:  远程调用接口，得到医院省市区
     * @author 靳雪超
     * @date: 2022/9/21 11:22
     * @param value
     * @return java.lang.String
     */
    @ApiOperation("远程调用接口，得到医院省市区")
    @GetMapping(value = "/getValue/{value}")
    String getValue(@PathVariable("value") Long value){

        return dictService.getValue(value);
    }
}

