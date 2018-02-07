package com.weikefu.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;




import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

/**
 * 用于复制相同属性名的值(反之也可以) 未经严格测试 目前仅限使用于getter setter属性的复制
 * author:  xueyuan
 * date  :  2017-07-05 11:48.
 */
public class ModelCopyUtils {

    public static void copy(Object from, Object to) throws IllegalAccessException {
    	try {
    		ConvertUtils.register(new org.apache.commons.beanutils.converters.DateConverter(null),
                    java.util.Date.class);
			BeanUtils.copyProperties(to, from);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    private static Method findMethodByName(Method[] methods, Object name) {
        for (Method method : methods) {
            if (method.getName().equals(name)){
            	return method;
            }
        }
        return null;
    }
}
