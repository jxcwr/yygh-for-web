package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author jxc
 * @since 2022-09-16
 */
public interface DictService extends IService<Dict> {

    List<Dict> getChildListById(Long pid);

    void exportData(HttpServletResponse response);

    boolean importData(MultipartFile file);

    String getName(String parentDictCode, Long value);

    String getValue(Long value);
}
