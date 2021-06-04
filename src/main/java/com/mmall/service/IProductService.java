package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
    ServiceResponse saveOrUpdateProduct(Product product);
    ServiceResponse setSaleStatus(Integer productId,Integer status);
    ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServiceResponse getProductList(Integer pageNum,Integer pageSize);
    ServiceResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize);
    ServiceResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServiceResponse<PageInfo> getProductByKeywordAndCategory(Integer categoryId,String keyword,Integer pageNum,Integer pageSize,String orderBy);
}
