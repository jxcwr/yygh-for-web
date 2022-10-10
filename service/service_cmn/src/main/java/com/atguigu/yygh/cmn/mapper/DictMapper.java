package com.atguigu.yygh.cmn.mapper;


import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 组织架构表 Mapper 接口
 * </p>
 *
 * @author jxc
 * @since 2022-09-16
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

}
