package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.vo.CartVo;

public interface ICartService {
    ServiceResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServiceResponse<CartVo> update(Integer userId,Integer productId,Integer count);
    ServiceResponse<CartVo>delete(Integer userId,String productIds);
    ServiceResponse<CartVo>list(Integer userId);
    ServiceResponse<CartVo>selectOrUnSelectAll(Integer userId,Integer productId,Integer checked);
    ServiceResponse<Integer>getCartProductCount(Integer userId);
}
