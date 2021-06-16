package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServiceResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectByUserIdProductId(userId,productId);
        if(cart == null){
            // 说明该产品不在购物车里面，需要增加
            Cart c = new Cart();
            c.setUserId(userId);
            c.setProductId(productId);
            c.setChecked(Const.Cart.CHECKED);
            c.setQuantity(count);

            int rowCount = cartMapper.insert(c);
            if(rowCount == 0){
                return ServiceResponse.createByErrorMsg("添加购物车商品失败");
            }
        }else{
            // 说明已经买了，还想再买多一点
            count += cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);

    }

    public ServiceResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectByUserIdProductId(userId,productId);
        if(cart != null){
           cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);

        return this.list(userId);
    }

    public ServiceResponse<CartVo>delete(Integer userId,String productIds){
        List<String>productIdList = Splitter.on(",").splitToList(productIds);
        cartMapper.deleteByUserIdAndProductId(userId,productIdList);
        return this.list(userId);
    }

    public ServiceResponse<CartVo>list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVo);
    }

    public ServiceResponse<CartVo>selectOrUnSelectAll(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedAllProduct(userId,productId,checked);
        return this.list(userId);
    }

    public ServiceResponse<Integer>getCartProductCount(Integer userId) {
        if(userId == null){
            return ServiceResponse.createBySuccess(0);
        }
        return ServiceResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart c : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(c.getId());
                cartProductVo.setUserId(c.getUserId());
                cartProductVo.setProductId(c.getProductId());
                cartProductVo.setQuantity(c.getQuantity());

                Product product = productMapper.selectByPrimaryKey(c.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    int productLimitNum = 0;
                    if(cartProductVo.getProductStock() >= c.getQuantity()){
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        cartProductVo.setQuantity(c.getQuantity());
                    }else{
                        productLimitNum = cartProductVo.getProductStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        cartProductVo.setQuantity(productLimitNum);

                        // 更新购物车数据库
                        Cart tmp = new Cart();
                        tmp.setId(c.getId());
                        tmp.setQuantity(productLimitNum);
                        cartMapper.updateByPrimaryKeySelective(tmp);
                    }

                    // 计算该购物车对应该产品的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(
                            cartProductVo.getProductPrice().doubleValue(),
                            cartProductVo.getQuantity()
                    ));
                    cartProductVo.setProductChecked(c.getChecked());
                }

                // 计算总价
                if(c.getChecked() == Const.Cart.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private Boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
