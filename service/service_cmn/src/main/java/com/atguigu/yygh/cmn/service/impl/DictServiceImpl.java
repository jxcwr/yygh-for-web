package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.config.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author jxc
 * @since 2022-09-16
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> getChildListById(Long pid) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id", pid);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        for (Dict dict : dicts) {
            dict.setHasChildren(isHaveChild(dict.getId()));
        }
        return dicts;
    }

    @Override
    public void exportData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for(Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();
                BeanUtils.copyProperties(dict,dictVo);
                dictVoList.add(dictVo);
            }

            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public boolean importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getName(String parentDictCode, Long value) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("dict_code", parentDictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);

        QueryWrapper<Dict> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("parent_id", dict.getId()).eq("value", value);
        return baseMapper.selectOne(queryWrapper1).getName();
    }

    @Override
    public String getValue(Long value) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("value", value);

        return baseMapper.selectOne(queryWrapper).getName();
    }

    private boolean isHaveChild(Long pid){
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id", pid);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
