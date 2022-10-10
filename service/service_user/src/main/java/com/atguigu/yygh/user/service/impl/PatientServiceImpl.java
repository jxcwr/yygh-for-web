package com.atguigu.yygh.user.service.impl;


import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author jxc
 * @since 2022-10-04
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findAllByUserId(Long userId) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        List<Patient> patients = baseMapper.selectList(queryWrapper);
        if (patients == null) {
            return null;
        }
        patients.forEach(items -> this.switchTool(items));
        return patients;
    }

    @Override
    public Patient getAuthDetailById(Long id) {
        Patient patient = baseMapper.selectById(id);

        return this.switchTool(patient);
    }

    private Patient switchTool(Patient patient) {
        Map<String, Object> param = patient.getParam();
        //根据证件类型编码，获取证件类型具体指
        String certificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), Long.valueOf(patient.getCertificatesType()));//联系人证件
        //联系人证件类型
        // String contactsCertificatesTypeString =dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getValue(Long.valueOf(patient.getProvinceCode()));
        //市
        String cityString = dictFeignClient.getValue(Long.valueOf(patient.getCityCode()));
        //区
        String districtString = dictFeignClient.getValue(Long.valueOf(patient.getDistrictCode()));
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        // patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        param.put("provinceString", provinceString);
        param.put("cityString", cityString);
        param.put("districtString", districtString);
        param.put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }
}
