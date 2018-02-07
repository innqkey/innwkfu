package com.weikefu.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumUtils {

	private  static  final Logger logger = LoggerFactory.getLogger(NumUtils.class);
	private static Integer num = null;
	private static String dateStr = null;
	
	static{
		clearNum(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		dateStr = sdf.format(new Date());
		
		String itemNum = StringUtils.isNotBlank(FileUtils.getApplicationPro("item.num"))?FileUtils.getApplicationPro("item.num"):"0";
		num = Integer.valueOf(itemNum);
		logger.info("初始值dateStr=="+dateStr+";==num=="+num);
	}
	public static String getNum(){
		synchronized(NumUtils.class){
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
			String curStr = sdf.format(new Date()); 
			if(!dateStr.equals(curStr)){
				dateStr = curStr;
				clearNum(0);
			}
			num++;
			String numStr = "";
			if(num<10){
				numStr = dateStr+"-0"+num;
			}else{
				numStr = dateStr+"-"+num;
			}
			logger.info("获取项目编码值=="+numStr);
			return numStr;
		}
	}
	
	public static void clearNum(Integer i){
		synchronized(NumUtils.class){
			num = i;
		}
	}
}
