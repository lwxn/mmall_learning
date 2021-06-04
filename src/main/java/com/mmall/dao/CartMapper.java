package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.springframework.web.bind.annotation.RequestParam;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdProductId(@RequestParam(value = "userId") Integer userId, @RequestParam(value = "productId") Integer productId);
}