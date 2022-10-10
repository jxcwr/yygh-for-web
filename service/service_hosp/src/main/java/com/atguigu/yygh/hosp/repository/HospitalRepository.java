package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author 靳雪超
 * @description: mongodb
 * @date: 2022/9/19 21:33
 */
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    Hospital findByHoscode(String hoscode);

    List<Hospital> findHospitalByHosnameLike(String hosname);
}
