package com.atguigu.yygh.cmn.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月19日 11:19
 */

public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    //一行一行读取
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        //调用方法添加数据库
        Dict dict = new Dict();
        //dictEeVo源对象
        //dict赋值对象
        BeanUtils.copyProperties(dictEeVo,dict);

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>();
        queryWrapper.eq("id", dict.getId());
        Integer count = dictMapper.selectCount(queryWrapper);

        if(count>0){
            dictMapper.updateById(dict);
        }else{
            dictMapper.insert(dict);
        }
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
