package com.atguigu.yygh.orders.service;


import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author jxc
 * @since 2022-10-05
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long saveOrder(String scheduleId, Long patientId);

    IPage<OrderInfo> selectPageByConditions(Long userId, Integer pageNum, Integer limit, OrderQueryVo orderQueryVo);

    OrderInfo getOrderDetail(Long orderId);
}
