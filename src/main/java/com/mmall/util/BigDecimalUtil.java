package com.mmall.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalUtil {

    private BigDecimalUtil(){

    }

    public static BigDecimal add(double a,double b){
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return b1.add(b2);
    }

    public static BigDecimal sub(double a,double b){
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return b1.subtract(b2);
    }

    public static BigDecimal mul(double a,double b){
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return b1.multiply(b2);
    }

    public static BigDecimal div(double a,double b){
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP); // 四舍五入
    }
}
