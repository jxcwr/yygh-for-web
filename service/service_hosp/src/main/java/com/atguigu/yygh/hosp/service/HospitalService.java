package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void saveHospital(Map<String, Object> resultMap);

    String getHospitalSetSignKey(String hosCode);

    Hospital getHospitalInfo(String hoscode);

    List<Hospital> pageByHospital(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo);

    boolean updateStatus(String id, Integer status);

    Hospital showHospitalInfo(String id);

    String getHospName(String hoscode);

    List<Hospital> findHospitalByNameLike(String hosname);

    Hospital getHospitalDetailInfo(String hoscode);
}
