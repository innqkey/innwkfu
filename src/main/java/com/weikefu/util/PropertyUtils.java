package com.weikefu.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PropertyUtils {
	    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);
	    private static Properties props;
	    //初始化的时候已经加载了
	    static{
	        loadProps();
	    }
	    /**
	     * 用来加载application.properties配置文件
	     */
	    synchronized static private void loadProps(){
	        logger.info("开始加载properties文件内容.......");
	        props = new Properties();
	        InputStream in = null;
	        try {
	            in = PropertyUtils.class.getClassLoader().getResourceAsStream("application.properties");
	            //in = PropertyUtil.class.getResourceAsStream("/jdbc.properties");
	            props.load(in);
	        } catch (FileNotFoundException e) {
	            logger.error("jdbc.properties文件未找到");
	        } catch (IOException e) {
	            logger.error("出现IOException");
	        } finally {
	            try {
	                if(null != in) {
	                    in.close();
	                }
	            } catch (IOException e) {
	                logger.error("jdbc.properties文件流关闭出现异常");
	            }
	        }
	        logger.info("加载properties文件内容完成...........");
	        logger.info("properties文件内容：" + props);
	    }

	    public static String getProperty(String key){
	        if(null == props) {
	            loadProps();
	        }
	        return props.getProperty(key);
	    }

	    public static String getProperty(String key, String defaultValue) {
	        if(null == props) {
	            loadProps();
	        }
	        return props.getProperty(key, defaultValue);
	    }
	}

