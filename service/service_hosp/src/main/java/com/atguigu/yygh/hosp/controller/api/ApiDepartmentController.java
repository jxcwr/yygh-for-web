package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.util.MD5;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.model.hosp.Department;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月20日 14:19
 */
@Api("上传科室的接口")
@RequestMapping("/api/hosp")
@RestController
public class ApiDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * @description:  保存科室信息
     * @author 靳雪超
     * @date: 2022/9/20 14:22
     * @return com.atguigu.yygh.hosp.result.Result
     */
    @ApiOperation("保存科室信息")
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest httpServletRequest){

        Map<String, Object> resultMap = HttpRequestHelper.switchMap(httpServletRequest.getParameterMap());

        String requestSignKey = (String) resultMap.get("sign");
        if (requestSignKey == null){
            return Result.fail();
        }
        String hosCode = (String) resultMap.get("hoscode");

        if (requestSignKey.equals(MD5.encrypt(departmentService.getSignKey(hosCode)))){

            departmentService.save(resultMap);
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    ///api/hosp/schedule/list
    /**
     * @description:  科室信息的分页查询
     * @author 靳雪超
     * @date: 2022/9/20 15:27
     * @return com.atguigu.yygh.hosp.result.Result
     */
    @ApiOperation("科室信息的分页查询")
    @PostMapping("/department/list")
    public Result<Page> pageDepartment(HttpServletRequest httpServletRequest){
        Map<String, Object> requestMap = HttpRequestHelper.switchMap(httpServletRequest.getParameterMap());

        String pageNum = (String) requestMap.get("page");
        String pageSize = (String) requestMap.get("limit");
        String hosCode = (String) requestMap.get("hoscode");


        Page<Department> page = departmentService.page(Integer.valueOf(pageNum),Integer.valueOf(pageSize),hosCode);
        return Result.ok(page);
    }

    ///api/hosp/department/remove
    /**
     * @description:  移除科室，根据医院编码和科室编码
     * @author 靳雪超
     * @date: 2022/9/20 21:03
     * @return com.atguigu.yygh.hosp.result.Result
     */
    @ApiOperation("移除科室，根据医院编码和科室编码")
    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest httpServletRequest){
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(httpServletRequest.getParameterMap());

        //校验
        String signKey = (String) resultMap.get("sign");
        if (signKey == null){
            return Result.fail();
        }

        String hoscode = (String) resultMap.get("hoscode");
        String depcode = (String) resultMap.get("depcode");
        if (!signKey.equals(MD5.encrypt(departmentService.getSignKey(hoscode))) || !departmentService.deleteDepartment(hoscode,depcode)) {

            return Result.fail();
        } else {
            return Result.ok();
        }



    }
}
