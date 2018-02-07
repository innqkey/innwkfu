package com.weikefu.util;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.EncoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 用来解析微信发来的语音
 * @author Administrator
 *
 */
public class VoiceWeiXinUtils {
	
	private  static   Logger logger = LoggerFactory.getLogger(VoiceWeiXinUtils.class);
	/** 
	 * 从微信下载语音，并存储到硬盘 
	 * @Title: handleVoice  
	 * @Description: TODO(从微信下载语音，并存储到硬盘)  
	 * @author  pll 
	 * @param @param userid 用户ID 
	 * @param @param mediaId 从微信下载所需语音ID  
	 * @path 是声音文件保存的路径
	 * @return void 返回类型  
	 * 
	 * @throws 
	 */  
	public static void handleVoice(Long userid,String mediaId,String path){  
	    //accesstoken  
	    String accesstoken="accesstoken";  
	    InputStream is = null;  
	    
	    String url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+ accesstoken + "&media_id=" + mediaId;  
	    try {  
	        URL urlGet = new URL(url);  
	        HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();  
	        http.setRequestMethod("GET"); // 必须是get方式请求  
	        http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");  
	        http.setDoOutput(true);  
	        http.setDoInput(true);  
	  
	        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒  
	        System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒  
	        http.connect();  
	        // 获取文件转化为byte流  
	        is = http.getInputStream();  
	        //存储到硬盘，原本音频格式为amr  
	        String userName = FileTools.writeAudioToFile(is,path,userid);  
	        //arm Convert Mp3  
	        changeToMp3(path + userid + "/" + userName + ".amr",path + userid + "/" + userName + ".mp3");  
	          
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	  
	}  
	  
	  
	/** 
	 * 把amr格式的语音转换成MP3 
	 * @Title: changeToMp3  
	 * @Description: TODO(把amr格式的语音转换成MP3)  
	 * @author  pll 
	 * @param @param sourcePath amr格式文件路径 
	 * @param @param targetPath 存放mp3格式文件路径  
	 * @return void 返回类型  
	 * @throws 
	 */  
	public static void changeToMp3(String sourcePath, String targetPath) {    
	    File source = new File(sourcePath);    
	    File target = new File(targetPath);    
	    AudioAttributes audio = new AudioAttributes();    
	    Encoder encoder = new Encoder();    
	  
	    audio.setCodec("libmp3lame");    
	    EncodingAttributes attrs = new EncodingAttributes();    
	    attrs.setFormat("mp3");    
	    attrs.setAudioAttributes(audio);    
	    try {    
	        encoder.encode(source, target, attrs);
	        //成功后删除文件
	        
//	        File Array[] = source.listFiles();
//	        for (File f : Array) {
//	            if (f.isFile()) {// 如果是文件
//	                    f.delete();
//	                    logger.debug("remove 文件--" + sourcePath);
//	                    return;
//	            }
//	        }
	    } catch (IllegalArgumentException e) {    
	        e.printStackTrace();    
	    } catch (InputFormatException e) {    
	        e.printStackTrace();    
	    } catch (Exception e) {    
	        e.printStackTrace();    
	    }    
	}    
}
