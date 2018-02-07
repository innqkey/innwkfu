package com.weikefu.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 相同属性的对象相互转换
 * author:  xueyuan
 * date  :  2017-07-05 12:51.
 */
public class ConvertUtils {


    /**
     * 两个实例对象 相同属性 值得复制
     * @param t
     * @param v
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> Object convertDtoAndVo(T t, V v){
        try {
            ModelCopyUtils.copy(t, v);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return v;
    }

    /**
     * 实例对象 与 对应对象 相同属性 值得复制
     * @param t
     * @param c
     * @param <T>
     * @param <V>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T, V> Object convertDtoAndVo(T t, Class<V> c) throws IllegalAccessException, InstantiationException {
        Object o = c.newInstance();
        try {
            ModelCopyUtils.copy(t, o);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return o;
    }

    /**
     * 实例对象集合 与 对应对象 相同属性 值得复制
     * @param t
     * @param c
     * @param <T>
     * @param <V>
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T, V> List<V> convertDtoAndVo(List<T> t, Class<V> c) throws InstantiationException, IllegalAccessException {
        List<V> o = new ArrayList<>();
        for (int i = 0; i < t.size(); i++) {
            o.add((V) ConvertUtils.convertDtoAndVo(t.get(i), c));
        }
//        o.addAll(t.stream().map(ConvertUtil::convertDtoAndVo).collect(Collectors.toList()));
        return o;
    }


}
