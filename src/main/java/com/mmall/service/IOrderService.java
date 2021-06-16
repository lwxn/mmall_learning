package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {
    ServiceResponse pay(Long orderNo, Integer userId, String path);
    ServiceResponse aliCallBack(Map<String,String> params);
    ServiceResponse queryOrderPayStatus(Integer userId,Long orderNo);
    ServiceResponse createOrder(Integer userId,Integer shippingId);
    ServiceResponse<String> cancel(Integer userId,Long orderNo);
    ServiceResponse<OrderProductVo> getOrderCartProduct(Integer userId);
    ServiceResponse<OrderVo>getOrderDetail(Integer userId, Long orderNo);
    ServiceResponse<PageInfo>getOrderList(Integer userId, int pageNum, int pageSize);

    ServiceResponse <PageInfo> manageList(Integer pageNum,Integer pageSize);
    ServiceResponse<OrderVo> manageDetail(Long orderNo);
    ServiceResponse<PageInfo> manageSearch(Long orderNo,Integer pageNum,Integer pageSize);
    ServiceResponse<String>manageSendGoods(Long orderNo);
}
