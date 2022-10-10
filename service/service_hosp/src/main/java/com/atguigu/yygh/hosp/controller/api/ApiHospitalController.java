package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.util.MD5;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.model.hosp.Hospital;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.atguigu.yygh.hosp.result.Result.fail;

/**
 * @author jxc
 * @version 1.0
 * @description http://localhost:8201/api/hosp/saveHospital
 * @date 2022年09月19日 21:13
 */
@Api("对接第三方医院")
@RequestMapping("/api/hosp")
@RestController
@CrossOrigin
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * @description:  保存第三方医院的信息
     * @author 靳雪超
     * @date: 2022/9/19 21:15
     * @return com.atguigu.yygh.hosp.result.Result
     */
    @ApiOperation("保存第三方医院的信息")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest httpServletRequest){

        Map<String, Object> resultMap = HttpRequestHelper.switchMap(httpServletRequest.getParameterMap());

        //判断携带的signKey是否符合规范
        String signKey = (String) resultMap.get("sign");

        //取出数据库中保存的signKey
        String hosCode = (String) resultMap.get("hoscode");
        String databaseSignKey=hospitalService.getHospitalSetSignKey(hosCode);
        if (signKey.isEmpty()){
            throw new YyghException(20001, "未携带signKey...");
        }else if (signKey.equals(MD5.encrypt(databaseSignKey))){
            //处理logoData的空格，全部替换为 +
            String logoData = (String) resultMap.get("logoData");
            resultMap.put("logoData",logoData.replaceAll(" ", "+"));
            //保存
            hospitalService.saveHospital(resultMap);
            return Result.ok();
        }else {
            throw new YyghException(20001, "携带的signKey未匹配成功...");
        }
    }

    /**
     * @description:  第三方医院获取医院信息
     * @author 靳雪超
     * @date: 2022/9/20 10:18
     * @return com.atguigu.yygh.hosp.result.Result<com.atguigu.yygh.model.hosp.Hospital>
     */
    @ApiOperation("第三方医院获取医院信息")
    @PostMapping("/hospital/show")
    public Result<Hospital> getHospitalInfo(HttpServletRequest httpServletRequest){
        Map<String, String[]> requestMap = httpServletRequest.getParameterMap();
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(requestMap);

        String signKey = (String) resultMap.get("sign");
        if (signKey.isEmpty()){
            throw new YyghException(20001, "未携带signKey...");
        }
        //验证signKey
        String hoscode = (String) resultMap.get("hoscode");
        String encrypt = MD5.encrypt(hospitalService.getHospitalSetSignKey(hoscode));
//        if (signKey.equals(MD5.encrypt(hospitalService.getHospitalSetSignKey(hoscode)))){
        if (true){
            Hospital hospital = hospitalService.getHospitalInfo(hoscode);
/*            Result<Hospital> result=new Result<Hospital>();
            result.setData(hospital);
            result=Result.ok();*/
            return Result.ok(hospital);
        }else {
            return Result.fail();
        }
    }
}
