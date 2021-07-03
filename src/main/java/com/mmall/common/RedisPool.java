package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool; // jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));// jedis连接池最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));// 最多有多少个实例是空闲的
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));// 最小的有多少个实例是空闲的
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));// 在从pool拿一个实例和redis server连接时是否要测试一下，没有问题就会返回true,否则就是false
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.return.borrow","true"));// 在return一个实例时是否要测试一下，没有问题就会返回true,否则就是false，true说明这个实例是可用的

    private static String ip = PropertiesUtil.getProperty("redis.ip");
    private static Integer port = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));// jedis连接池最大连接数

    private static void initPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);

        jedisPoolConfig.setBlockWhenExhausted(true); // 连接耗尽时是否阻塞,false抛出异常, true的话阻塞到超时，超时会抛出异常

        pool = new JedisPool(jedisPoolConfig,ip,port,1000*2); // 一般是500ms，1s已经很慢了
    }

    static {
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(final Jedis jedis){
            pool.returnResource(jedis);
    }

    public static void returnBrokenResource(final Jedis jedis){
            pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("lwxn","hhh");
        returnResource(jedis);

        pool.destroy(); // 销毁连接池的所有连接，因为会重复使用连接池的代码，所以不要用这个销毁
        System.out.println("This is end");
    }
}
