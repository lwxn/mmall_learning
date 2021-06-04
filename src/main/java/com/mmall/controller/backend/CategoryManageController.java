package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServiceResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }

        // 检验是否是管理员.
        if(iUserService.checkAdminRole(user).isSuccess()){
            // 如果是管理员，增加处理分类的逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServiceResponse.createByErrorMsg("该用户不是管理员，没有权限操作");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServiceResponse setCategoryName(HttpSession session,int categoryId,String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }

        // 检验是否是管理员.
        if(iUserService.checkAdminRole(user).isSuccess()){
            // 如果是管理员，增加处理分类的逻辑
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServiceResponse.createByErrorMsg("该用户不是管理员，没有权限操作");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServiceResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }

        // 检验是否是管理员.
        if(iUserService.checkAdminRole(user).isSuccess()){
            //无递归地查询子节点的信息
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServiceResponse.createByErrorMsg("该用户不是管理员，没有权限操作");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServiceResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }

        // 检验是否是管理员.
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询当前结点和递归子节点的id
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServiceResponse.createByErrorMsg("该用户不是管理员，没有权限操作");
        }
    }

}
