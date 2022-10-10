package com.atguigu.yygh.orders.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.util.JwtHelper;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.orders.service.OrderInfoService;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author jxc
 * @since 2022-10-05
 */
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    //String scheduleId, Long patientId
    @PostMapping("/auth/submitOrder/{scheduleId}/{patientId}")
    public R getOrderId(@PathVariable("scheduleId") String scheduleId,
                        @PathVariable("patientId") Long patientId) {
        Long orderId = orderInfoService.saveOrder(scheduleId, patientId);
        return R.ok().data("orderId", orderId);
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 订单列表（条件查询带分页）
     * @author 靳雪超
     * @date: 2022/10/7 14:25
     */
    @ApiOperation("订单列表（条件查询带分页）")
    @GetMapping("/auth/{pageNum}/{limit}")
    public R pageOrders(@PathVariable Integer pageNum,
                        @PathVariable Integer limit,
                        OrderQueryVo orderQueryVo,
                        @RequestHeader String token) {
        Long userId = JwtHelper.getUserId(token);
        IPage<OrderInfo> pageModel = orderInfoService.selectPageByConditions(userId, pageNum, limit, orderQueryVo);
        return R.ok().data("pageModel", pageModel);
    }

    /**
     * @param orderId
     * @return com.atguigu.yygh.common.result.R
     * @description: 根据订单id 查询订单详情
     * @author 靳雪超
     * @date: 2022/10/10 14:41
     */
    @ApiOperation("根据订单id 查询订单详情")
    @GetMapping("/auth/getOrders/{orderId}")
    public R getOrderDetail(@PathVariable Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOrderDetail(orderId);
        return R.ok().data("orderInfo", orderInfo);
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 订单状态的下拉列表
     * @author 靳雪超
     * @date: 2022/10/10 14:44
     */
    @ApiOperation("订单状态的下拉列表")
    @GetMapping("/auth/getStatusList")
    public R getOrderStatusList() {
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();
        return R.ok().data("statusList", statusList);
    }
}

