package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    // user login
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse, HttpServletRequest request){
        //service->mybatis->dao
        ServiceResponse<User>response = iUserService.login(username,password);
        // 把结果放进session里面
        if(response.isSuccess()){
            // 设置了一个新的接口，接口里面定义了一个常量
            //4A1BA09DE7C7DF67EB453E81CC325DBA
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            CookieUtil.readLoginToken(request);
            CookieUtil.delLoginToken(request,httpServletResponse);
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

            // 进行redisPool的改造
//            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String>checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User>getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return ServiceResponse.createBySuccess(user);
        }
        return ServiceResponse.createByErrorMsg("用户未登录，无法获取当前信息");
    }

    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String>forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String>forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String>forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String>resetPassword(String passwordOld,String passwordNew,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorMsg("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User>updateInformation(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServiceResponse.createByErrorMsg("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
       ServiceResponse<User>response =  iUserService.updateInformation(user);
       if(response.isSuccess()){
           session.setAttribute(Const.CURRENT_USER,response.getData());
       }
       return response;
    }

    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User>getInformation(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录status = 10");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
