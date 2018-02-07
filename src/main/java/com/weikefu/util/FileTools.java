package com.weikefu.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 用于文件的操作
 * @author Administrator
 *
 */
public class FileTools {
	
	/**
	 * 将发送的消息转换为本地的文件，返回对应的存放地址
	 * @param is
	 * @param path
	 * @param userid
	 * @return
	 */
	public static String writeAudioToFile(InputStream is, String path,
			Long userid) {
		 BufferedInputStream bis = new BufferedInputStream(is);
		 BufferedOutputStream bos;
		 String userName = "";
		try {
			userName = "" + System.nanoTime();
			String url = path + userid +"/" + userName + ".amr";
			bos = new BufferedOutputStream(new FileOutputStream(new File(path + userid +"/" + System.nanoTime() + ".amr")));
			int x = 0;
			
			while ((x = bis.read()) != -1) {
				bos.write(x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 
       
		return userName;
	}

}
