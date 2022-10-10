package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
 * @date 2022年09月26日 19:49
 */
@Api(tags = "用户医院显示接口")
@RestController
@RequestMapping("/user/hosp/hospital")
public class UserHospitalController {

    @Autowired
    private HospitalService hospitalService;


    /**
     * @param hospitalQueryVo
     * @return com.atguigu.yygh.common.result.R
     * @description: 地区选择的查询
     * @author 靳雪超
     * @date: 2022/9/26 20:01
     */
    @ApiOperation("地区选择的查询")
    @GetMapping("/list")
    public R getHospitalList(HospitalQueryVo hospitalQueryVo) {

        List<Hospital> list = hospitalService.pageByHospital(1, 1000000, hospitalQueryVo);

        return R.ok().data("list", list);
    }

    /**
     * @param hosname
     * @return com.atguigu.yygh.common.result.R
     * @description: 带搜索条件的模糊查询
     * @author 靳雪超
     * @date: 2022/9/26 20:04
     */
    @ApiOperation("带搜索条件的模糊查询")
    @GetMapping("/search/{hosname}")
    public R findByName(@ApiParam(name = "hosname", value = "医院名称", required = true) @PathVariable("hosname") String hosname) {

        List<Hospital> list = hospitalService.findHospitalByNameLike(hosname);

        return R.ok().data("list", list);
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 在用户界面上显示医院的详细信息
     * @author 靳雪超
     * @date: 2022/9/27 8:58
     */
    @ApiOperation("在用户界面上显示医院的详细信息")
    @GetMapping("/detail/{hoscode}")
    public R getHospitalInfo(@PathVariable("hoscode") String hoscode) {

        Hospital hospital = hospitalService.getHospitalDetailInfo(hoscode);

        return R.ok().data("hospital", hospital);
    }
}
