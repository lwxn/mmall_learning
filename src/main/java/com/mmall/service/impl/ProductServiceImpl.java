package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    public ServiceResponse saveOrUpdateProduct(Product product){
        if(product != null){
            if(StringUtils.isNoneBlank(product.getSubImages())){
                String[] subImageList = product.getSubImages().split(",");
                if(subImageList.length > 0){
                    product.setMainImage(subImageList[0]);
                }

                if(product.getId() != null){
                    int rowCount = productMapper.updateByPrimaryKeySelective(product);
                    if(rowCount > 0){
                        return ServiceResponse.createBySuccess("更新产品成功");
                    }
                    else{
                        return ServiceResponse.createByErrorMsg("更新产品失败");
                    }
                }else{
                    int rowCount = productMapper.insert(product);
                    if(rowCount > 0){
                        return ServiceResponse.createBySuccess("增加产品成功");
                    }
                    else{
                        return ServiceResponse.createByErrorMsg("增加产品失败");
                    }
                }
            }
         }
        return ServiceResponse.createByErrorMsg("保存或更新产品参数为空，失败");
    }

    public ServiceResponse setSaleStatus(Integer productId,Integer status){
        if(productId == null || status == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return ServiceResponse.createBySuccess("修改产品状态成功");
        }else{
            return ServiceResponse.createByErrorMsg("修改产品状态失败");
        }
    }

    public ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServiceResponse.createByErrorMsg("产品不存在");
        }
        // VO对象--value object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServiceResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();

        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        // imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        // parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        // createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        // updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    // 使用到了mybatis-page-helper
    public ServiceResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        // startpage-start
        PageHelper.startPage(pageNum,pageSize);
        List<Product>productList = productMapper.selectList();

        List<ProductListVo>productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        // 填充SQL逻辑 这里是因为productList是List,而productListVoList则是ArrayList，所以是不一样的
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubTitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setStatus(product.getStatus());
        productListVo.setPrice(product.getPrice());

        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        return productListVo;
    }

    public ServiceResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNoneBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }

        List<Product>productList = productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product p : productList){
            ProductListVo t = assembleProductListVo(p);
            productListVoList.add(t);
        }
        PageInfo PageResult = new PageInfo(productList);
        PageResult.setList(productListVoList);
        return ServiceResponse.createBySuccess(PageResult);
    }

    public ServiceResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServiceResponse.createByErrorMsg("产品不存在");
        }
        if(product.getStatus() != Const.ProductSaleEnum.ON_SALE.getCode()){
            return ServiceResponse.createByErrorMsg("产品已经下架");
        }
        // VO对象--value object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServiceResponse.createBySuccess(productDetailVo);
    }

    public ServiceResponse<PageInfo> getProductByKeywordAndCategory(Integer categoryId,String keyword,Integer pageNum,Integer pageSize,String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 如果该产品是一个很大的父产品的话，就需要查询它的子节点
        List<Integer>categoryIdList = new ArrayList<Integer>();

        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo>productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServiceResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }

        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();

        }

        PageHelper.startPage(pageNum,pageSize);
        // 排序处理
        if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            String[] orderByArray = orderBy.split("_");
            PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]); // format: price desc
        }

        // 因为这里category不是空，已经new了

        List<Product>productList = productMapper.selectByKeyWordAndCategoryId(
                StringUtils.isBlank(keyword)? null : keyword,
                categoryIdList.size() == 0 ? null : categoryIdList);
        List<ProductListVo>productListVoList = Lists.newArrayList();
        for(Product item : productList){
            productListVoList.add(assembleProductListVo(item));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServiceResponse.createBySuccess(pageInfo);

    }
}
