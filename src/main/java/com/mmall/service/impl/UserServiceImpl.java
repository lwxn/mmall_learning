package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServiceResponse.createByErrorMsg("用户名不存在");
        }

        // todo 密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServiceResponse.createByErrorMsg("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("登录成功",user);
    }

    public ServiceResponse<String>register(User user){
        ServiceResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        // 加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            return ServiceResponse.createByError();
        }
        return ServiceResponse.createBySuccessMsg("注册成功");
    }

    public ServiceResponse<String>checkValid(String str,String type){
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServiceResponse.createByErrorMsg("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServiceResponse.createByErrorMsg("EMAIL已存在");
                }
            }
        }else{
            return ServiceResponse.createByErrorMsg("参数错误");
        }
        return ServiceResponse.createBySuccessMsg("校验成功");
    }

    public ServiceResponse selectQuestion(String username){
        ServiceResponse serviceResponse = this.checkValid(username,Const.USERNAME);
        if(serviceResponse.isSuccess()){
            return ServiceResponse.createByErrorMsg("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(question)){
            return ServiceResponse.createBySuccess(question);
        }
        return ServiceResponse.createByErrorMsg("该用户未设置找回密码问题");
    }

    public ServiceResponse<String>checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            // 说明问题答案正确，生成token
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMsg("问题答案错误");
    }

    public ServiceResponse<String>forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServiceResponse.createByErrorMsg("Token是空值");
        }
        ServiceResponse serviceResponse = this.checkValid(username,Const.USERNAME);
        if(serviceResponse.isSuccess()){
            return ServiceResponse.createByErrorMsg("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token) ){
            return ServiceResponse.createByErrorMsg("Token无效或过期");
        }
        if(org.apache.commons.lang3.StringUtils.equals(token,forgetToken)){
            String md5Newpassword = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Newpassword);
            if(rowCount > 0){
                return ServiceResponse.createBySuccessMsg("修改密码成功");
            }
        }else {
            return ServiceResponse.createByErrorMsg("token错误，请重新获取token");
        }
        return ServiceResponse.createByErrorMsg("修改密码操作失效");
    }

    public ServiceResponse<String>resetPassword(String passwordOld,String passwordNew,User user){
        // 防止横向越权
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServiceResponse.createByErrorMsg("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0){
            return ServiceResponse.createBySuccessMsg("密码更新成功");
        }
        return ServiceResponse.createByErrorMsg("密码更新失败");


    }

    public ServiceResponse<User>updateInformation(User user){
        // username不可以被更新
        // 还需要校验新的email是否已经存在，就算存在也不能是这个用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServiceResponse.createByErrorMsg("email已经存在，请更换email");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0){
            return ServiceResponse.createBySuccessMsg("更新个人信息成功");
        }
        return ServiceResponse.createByErrorMsg("更新个人信息失败");
    }

    public ServiceResponse<User>getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServiceResponse.createByErrorMsg("找不到当前的用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }
}
