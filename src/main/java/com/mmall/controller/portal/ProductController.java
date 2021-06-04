package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse<ProductDetailVo> getDetail(Integer productId){
        // 前台返回产品需要判断是不是在线状态
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<PageInfo> getList(@RequestParam(value = "categoryId",required = false) Integer categoryId,
                                             @RequestParam(value = "keyword",required = false)  String keyword,
                                             @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                             @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                             @RequestParam(value = "orderBy",defaultValue = "")String orderBy)
    {
        // 前台返回产品需要判断是不是在线状态
        return iProductService.getProductByKeywordAndCategory(categoryId,keyword,pageNum,pageSize,orderBy);
    }
}
