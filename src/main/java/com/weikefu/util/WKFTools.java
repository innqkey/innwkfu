package com.weikefu.util;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.jasypt.util.text.BasicTextEncryptor;

import com.common.WKFDataContext;

/**
 * 功能描述：
 * @Package: com.weikefu.util 
 * @author: Administrator   
 * @date: 2018年2月24日 下午1:59:31
 */
public class WKFTools {
    /**
     * 进行系统的加密，
     * @param str
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static String decryption(String str) throws NoSuchAlgorithmException{
    	BasicTextEncryptor  textEncryptor = new BasicTextEncryptor ();
    	//设置加密的密钥
    	textEncryptor.setPassword(WKFDataContext.getSystemSecrityPassword());
    	//对密码进行加密
    	return textEncryptor.decrypt(str);
    }
    
    /**
     * 获取随机的UUID
     * @return
     */
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "") ;
	}
}
