package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.pojo.User;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);

        // 除了时间戳，时间戳是一串看不懂的数字
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        // 忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        // 所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        // 反序列化设置，如果json中存在这个属性，但是java对象里面没有的情况，防止报错
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    // 序列化
    public static <T> String obj2String(T obj){
        if(obj == null){
            return null;
        }
        try{
            return obj instanceof String ? (String)obj : objectMapper.writeValueAsString(obj);
        }catch (Exception ex){
            log.warn("parse object to String error",ex);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try{
            return obj instanceof String ? (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }catch (Exception ex){
            log.warn("parse Object to String error",ex);
            return null;
        }
    }

    // 反序列化
    public static <T> T string2Obj(String str,Class clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }

        try {
            return clazz.equals(String.class)? (T)str : (T) objectMapper.readValue(str, clazz);
        } catch (Exception ex){
            log.warn("parse String to Object error",ex);
            return null;
        }
    }

    // 如果需要有collection类型的参数需要反序列化，就可以用typeReference来细化，具体它里面的类型
    public static <T> T string2Obj(String str, TypeReference typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }

        try {
            return (T)(typeReference.getType().equals(String.class)? str : objectMapper.readValue(str, typeReference));
        } catch (Exception ex){
            log.warn("parse String to Object error",ex);
            return null;
        }
    }

    // 这个class是代表collection class,后面的点是可变长度
    public static <T> T string2Obj(String str, Class<?>collectionClass, Class<?>elementClass){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClass);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception ex){
            log.warn("parse String to Object error",ex);
            return null;
        }
    }

    public static void main(String[] args) {
        User user1 = new User();
        user1.setId(2);
        user1.setAnswer("hiih");
        user1.setCreateTime(new Date());

        String s1 = JsonUtil.obj2String(user1);
        String prettyS1 = JsonUtil.obj2StringPretty(user1);

        log.info(s1);
        log.info(prettyS1);

        User user2 = JsonUtil.string2Obj(s1,User.class);

        List<User>userList = Lists.newArrayList();
        userList.add(user1);
        userList.add(user2);

        // 一般工作是不会用pretty，因为会占用字节，换行什么的
        String s3 = JsonUtil.obj2StringPretty(userList);
        log.info(s3);

        // 第一个反序列化方法
        // 这里本来放的应该是user的，但是反序列化变成了linkedHashMap
        // 然后如果getId()是会失败的，因为linkedHashmap没有getId()

        // 第二个方法就会成功
        List<User>userList1 = JsonUtil.string2Obj(s3, new TypeReference<List<User>>(){});

        List<User>userList2 = JsonUtil.string2Obj(s3,List.class,User.class);
        System.out.println("jj");
    }
}
