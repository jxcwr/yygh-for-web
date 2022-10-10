package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月20日 14:30
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final HospitalSetMapper hospitalSetMapper;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository, HospitalSetMapper hospitalSetMapper) {
        this.departmentRepository = departmentRepository;
        this.hospitalSetMapper = hospitalSetMapper;
    }


    @Override
    public void save(Map<String, Object> resultMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Department.class);

        String depCode = department.getDepcode();
        String hosCode = department.getHoscode();

        Department resultDepartment = departmentRepository.findByHoscodeAndDepcode(hosCode, depCode);
        if (resultDepartment == null) {

            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        } else {

            department.setId(resultDepartment.getId());
            department.setUpdateTime(new Date());
            department.setCreateTime(resultDepartment.getCreateTime());
            department.setIsDeleted(resultDepartment.getIsDeleted());
            departmentRepository.save(department);
        }

    }

    @Override
    public String getSignKey(String hosCode) {
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hosCode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(queryWrapper);
        if (hospitalSet != null) {
            return hospitalSet.getSignKey();
        } else {
            throw new YyghException(20001, "没有此医院的signKey...");
        }
    }

    @Override
    public Page<Department> page(Integer pageNum, Integer pageSize, String hosCode) {

        Department department = new Department();
        //分页的条件
        Example<Department> example = Example.of(department);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public boolean deleteDepartment(String hoscode, String depcode) {

        if (hoscode == null || depcode == null) {
            return false;
        }
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        departmentRepository.deleteById(department.getId());
        return true;
    }

    @Override
    public List<DepartmentVo> getDepartmentList(String hoscode) {

        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        List<Department> all = departmentRepository.findAll(example);

        //分类
        //map的key就是当前所属大科室的编号
        //map的value就是大科室下的各个小科室
        Map<String, List<Department>> collect = all.stream().collect(Collectors.groupingBy(Department::getBigcode));

        List<DepartmentVo> finialList = new ArrayList<>();

        for (Map.Entry<String, List<Department>> entry : collect.entrySet()) {

            DepartmentVo departmentVo = new DepartmentVo();
            //大科室的编号
            String bigCode = entry.getKey();

            //当前大科室下的各个小科室
            List<Department> childDepartment = entry.getValue();

            List<DepartmentVo> childDepartmentList = new ArrayList<>();
            //遍历每个小科室，拿到科室编号和科室名字
            for (Department child : childDepartment) {

                DepartmentVo childDep = new DepartmentVo();
                childDep.setDepcode(child.getDepcode());
                childDep.setDepname(child.getDepname());

                childDepartmentList.add(childDep);
            }


            //把大科室添加到finialList中去
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(childDepartment.get(0).getBigname());
            departmentVo.setChildren(childDepartmentList);
            finialList.add(departmentVo);

        }

        return finialList;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department byHoscodeAndDepcode = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        return byHoscodeAndDepcode.getDepname();
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
    }

}
