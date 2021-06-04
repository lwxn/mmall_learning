package com.mmall.service.impl;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    public ServiceResponse add(Integer userId,Integer productId,Integer count){

    }
}
