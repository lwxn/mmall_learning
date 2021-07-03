package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.security.interfaces.RSAPublicKey;

@Slf4j
public class RedisPoolUtil {
    public static String set(String key,String value){
        Jedis jedis = null;
        String result = null;

        // 拿到jedis的一个链接
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key,value); // 这个可能会出现异常
        } catch (Exception e) {
            // e.getMessage只能返回一行信息
            log.error("set key:{} value:{} error:{}",key,value,e);
            RedisPool.returnBrokenResource(jedis); // 因为已经出现了异常，所以返回broken的resource
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    // 重新设置过期信息，如果已经超时了就可以重新设置，返回1，如果没有超时就不能更改，返回0，单位是s
    public static Long expire(String key,int exTime){
        Jedis jedis = null;
        Long result = null;

        // 拿到jedis的一个链接
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key,exTime); // 这个可能会出现异常
        } catch (Exception e) {
            // e.getMessage只能返回一行信息
            log.error("expire key:{} value:{} error:{}",key,e);
            RedisPool.returnBrokenResource(jedis); // 因为已经出现了异常，所以返回broken的resource
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    // 设置过期时间,单位是s 比如说放用户的登录信息
    public static String setEx(String key,String value,int exTime){
        Jedis jedis = null;
        String result = null;

        // 拿到jedis的一个链接
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key,exTime,value); // 这个可能会出现异常
        } catch (Exception e) {
            // e.getMessage只能返回一行信息
            log.error("setex key:{} value:{} error:{}",key,value,e);
            RedisPool.returnBrokenResource(jedis); // 因为已经出现了异常，所以返回broken的resource
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        Jedis jedis = null;
        String result = null;

        // 拿到jedis的一个链接
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key); // 这个可能会出现异常
        } catch (Exception e) {
            // e.getMessage只能返回一行信息
            log.error("get key:{} error:{}",key,e);
            RedisPool.returnBrokenResource(jedis); // 因为已经出现了异常，所以返回broken的resource
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        Jedis jedis = null;
        Long result = null;

        // 拿到jedis的一个链接
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key); // 这个可能会出现异常
        } catch (Exception e) {
            // e.getMessage只能返回一行信息
            log.error("del key:{} error:{}",key,e);
            RedisPool.returnBrokenResource(jedis); // 因为已经出现了异常，所以返回broken的resource
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();

        RedisPoolUtil.set("keyset","oioi");
        String value = RedisPoolUtil.get("keyset");

        RedisPoolUtil.setEx("keyex","valuex",10*60);

        RedisPoolUtil.expire("keyset",60*20);

        RedisPoolUtil.del("keyset");

        System.out.println("end");
    }
}
