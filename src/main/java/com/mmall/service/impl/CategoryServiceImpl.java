package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService {
//    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServiceResponse addCategory(String categoryName,int parentId){
        if(StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMsg("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServiceResponse.createBySuccessMsg("添加品类成功");
        }
        return ServiceResponse.createByErrorMsg("添加品类失败");
    }

    public ServiceResponse updateCategoryName(int categoryId,String categoryName){
        if(StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMsg("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServiceResponse.createBySuccessMsg("更新品类名字成功");
        }
        return ServiceResponse.createByErrorMsg("更新品类名字失败");
    }

    public ServiceResponse<List<Category>>getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            log.info("未找到当前分类的子分类");
        }
        return ServiceResponse.createBySuccess(categoryList);
    }

    public ServiceResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category>categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet,categoryId);

        List<Integer>categoryIdList = Lists.newArrayList();
        for(Category c: categorySet){
            categoryIdList.add(c.getId());
        }
        return ServiceResponse.createBySuccess(categoryIdList);
    }

    // 递归算法，算出子节点
    private Set<Category>findChildrenCategory(Set<Category>set,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            set.add(category);
        }

        // 如果categoryList找不到对象，那么mybatis是不会返回一个空对象的
        List<Category>categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category children : categoryList){
            findChildrenCategory(set,children.getId());
        }
        return set;
    }

}
