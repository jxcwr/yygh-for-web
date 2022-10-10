package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月19日 22:03
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    private final DictFeignClient dictFeignClient;
    private final HospitalSetMapper hospitalSetMapper;
    private HospitalRepository hospitalRepository;

    /**
     * @return null
     * @description: 提倡setter注入
     * @author 靳雪超
     * @date: 2022/9/19 22:13
     */

    @Autowired
    public HospitalServiceImpl(HospitalSetMapper hospitalSetMapper, DictFeignClient dictFeignClient) {
        this.hospitalSetMapper = hospitalSetMapper;
        this.dictFeignClient = dictFeignClient;
    }

    @Autowired
    public void setHospitalRepository(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    public void saveHospital(Map<String, Object> resultMap) {

        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Hospital.class);

        Hospital byHosCode = hospitalRepository.findByHoscode(hospital.getHoscode());

        if (byHosCode == null) {

            //0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            //逻辑删除(1:已删除，0:未删除)
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            hospital.setStatus(byHosCode.getStatus());
            hospital.setCreateTime(byHosCode.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(byHosCode.getIsDeleted());
            hospital.setId(byHosCode.getId()); //根据id更新
            hospitalRepository.save(hospital);
        }

    }

    @Override
    public String getHospitalSetSignKey(String hosCode) {
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hosCode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(queryWrapper);
        if (hospitalSet != null) {
            return hospitalSet.getSignKey();
        } else {
            throw new YyghException(20001, "signKey不存在...");
        }
    }

    @Override
    public Hospital getHospitalInfo(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(20001, "不存在此医院...");
        }
        return hospital;
    }

    @Override
    public List<Hospital> pageByHospital(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
//                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询,全部字段
                .withMatcher("hosname", ExampleMatcher.GenericPropertyMatchers.contains())   //针对特定的字段
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        Example<Hospital> example = Example.of(hospital, matcher);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        pages.getContent().stream().forEach(item -> {

            //转化省市区
            /*item.setProvinceCode(dictFeignClient.getValue(Long.parseLong(item.getProvinceCode())));
            item.setCityCode(dictFeignClient.getValue(Long.parseLong(item.getCityCode())));
            item.setDistrictCode(dictFeignClient.getValue(Long.parseLong(item.getDistrictCode())));*/

            switchHospitalInfo(item);

            //转换医院等级
//            item.setHostype(dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), Long.parseLong(item.getHostype())));


        });
        return pages.getContent();
    }

    @Override
    public boolean updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        if (hospital == null) {
            return false;
        }

        hospital.setStatus(status);

        hospitalRepository.save(hospital);

        return true;
    }

    @Override
    public Hospital showHospitalInfo(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();

        switchHospitalInfo(hospital);
        return hospital;

    }

    @Override
    public String getHospName(String hoscode) {
        Hospital byHoscode = hospitalRepository.findByHoscode(hoscode);
        return byHoscode.getHosname();
    }

    @Override
    public List<Hospital> findHospitalByNameLike(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
//        return null;
    }

    @Override
    public Hospital getHospitalDetailInfo(String hoscode) {
        Hospital byHoscode = hospitalRepository.findByHoscode(hoscode);
        if (byHoscode == null) {
            return null;
        }
        this.switchHospitalInfo(byHoscode);
        return byHoscode;
    }

    /**
     * @param hospital
     * @description: 转换医院信息
     * @author 靳雪超
     * @date: 2022/9/21 21:21
     */
    private void switchHospitalInfo(Hospital hospital) {
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), Long.parseLong(hospital.getHostype()));
        String provinceString = dictFeignClient.getValue(Long.parseLong(hospital.getProvinceCode()));
        String cityString = dictFeignClient.getValue(Long.parseLong(hospital.getCityCode()));
        String districtString = dictFeignClient.getValue(Long.parseLong(hospital.getDistrictCode()));


        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
    }
}
