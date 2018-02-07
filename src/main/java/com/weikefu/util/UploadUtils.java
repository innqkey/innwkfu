package com.weikefu.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.weikefu.constant.ContextConstant;


/**文件上传的工具类
 * Created by loneless on 2017/7/15.
 */
public class UploadUtils {
	private  static  final Logger logger = LoggerFactory.getLogger(UploadUtils.class);
    /**
     * 上传单个图片，参数一是springmvc接收的file
     * ,string 是文件保存地址
     * @param file
     * @param pathname
     * @param userId 这个是上传的客服的商户的id
    
     * @param filetype 上传的类型
     * @return
     */
    public static String uploadimage(MultipartFile file, String pathname, String shopId){
        if (!file.isEmpty() && StringUtils.isNotBlank(pathname)) {
            //判断文件的类型和文件的大小
            BufferedOutputStream out = null;
            try {
				pathname= pathname + shopId + "/";
            	mkDir(pathname);
                int k = file.getContentType().indexOf("/") + 1;
                String suffix = file.getContentType().substring(k);
                String[] filetype={"bmp", "jpg","jpeg", "png", "gif", "webp"};
                boolean imageType=false;
                for (int i = 0; i < filetype.length; i++) {
                    if (filetype[i].equals(suffix)) {
                        imageType=true;
                    }
                }
                if (!imageType){
                	return ContextConstant.IMAGE_TYPE;
                }
                //限制文件的大小
                if (file.getSize() > 10485760){
                	return ContextConstant.SIZE_EXCEEDS;
                }
                
                String image = System.nanoTime() + "." + suffix;
                String url = pathname + image;
                logger.info("上传upload地址==="+url);
                out = new BufferedOutputStream(new FileOutputStream(new File(url)));
                out.write(file.getBytes());
                out.flush();
            	return shopId + "/" + image;
            } catch (IOException e) {
                e.printStackTrace();
                return ContextConstant.UPLOAD_FAILURE;
            }finally{
                try {
                	if(out!=null){
                		out.close();
                	}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        
        } else {
            return ContextConstant.IMAGE_NULL;
        }
    }

	private static void mkDir(String pathname) {
		File parentFile;
		parentFile = new File(pathname);
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
	}

    /** 上传多个文件
     * 一个文件判断错误，全部不保存
     */
    public static List<String> uploadImages(List<MultipartFile> files, String pathName) {
        MultipartFile file = null;
        List<String> urls=new ArrayList<String>();
        BufferedOutputStream out = null;
        //先进行判断
        for (int i =0; i< files.size(); i++) {
            file = files.get(i);
            if (!file.isEmpty()) {
                //判断文件的类型和文件的大小
                try {
	                    int k = file.getContentType().indexOf("/") + 1;
	                    String suffix = file.getContentType().substring(k);
	                    String[] filetype={"bmp", "jpg","jpeg", "png", "gif", "webp"};
	                    boolean imageType=false;
	                    for (int j = 0; j < filetype.length; j++) {
	                        if (filetype[j].equals(suffix)) {
	                            imageType=true;
	                        }
	                    }
	                    if(!imageType){
	                    	urls.add(ContextConstant.IMAGE_TYPE);
	                    	return urls;
	                    }
	                    //限制文件的大小
	                    if(file.getSize()>10485760){
	                    	urls.add(ContextConstant.SIZE_EXCEEDS);
	                    	return urls;
	                    } 
                    } catch (Exception e) {
                        e.printStackTrace();
                        urls.add(ContextConstant.UPLOAD_FAILURE);
                        return urls;
                    }
            }else {
				urls.add(ContextConstant.IMAGE_NULL);
				return urls;
			}
        }
        
        
        for (int i = 0; i < files.size(); i++) {
            
            //保存所有的图片
            try {
				int k = file.getContentType().indexOf("/") + 1;
				String suffix = file.getContentType().substring(k);
				String url=pathName+System.nanoTime()+"."+suffix;
				out = new BufferedOutputStream(new FileOutputStream(new File(url)));
				out.write(file.getBytes());
				out.flush();
				urls.add(url);
			} catch (IOException e) {
				urls.add(ContextConstant.UPLOAD_FAILURE);
				e.printStackTrace();
			}finally{
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
        return urls;
        
        
    }
    /**
     * 用来上传文件的，比上传图片多增加一步对相同名称的文件进行判断
     * @param file
     * @param pathname
     * @param userId
     * @return
     */
	public static String uploaddoc(MultipartFile file, String pathname,
			int userId) {
		 if (!file.isEmpty() && StringUtils.isNotBlank(pathname)) {
	            //判断文件的类型和文件的大小
	            BufferedOutputStream out = null;
	            pathname=pathname+userId+"/";
	            try {
	            	if(!new File(pathname).exists()){
	            		new File(pathname).mkdir();
	            	}
	            	String docname=file.getOriginalFilename();
	                String suffix =docname.substring(docname.lastIndexOf(".") + 1);
	                String[] filetype={"doc","txt", "docx","jpg", "pdf", "xls", "xlsx","zip","rar"};
	                boolean imageType=false;
	                for (int i = 0; i < filetype.length; i++) {
	                    if (filetype[i].equals(suffix)) {
	                        imageType=true;
	                    }
	                }
	                if(!imageType){
	                	return ContextConstant.IMAGE_TYPE;
	                }
	                //限制文件的大小
	                if(file.getSize()>10485760){
	                	return ContextConstant.SIZE_EXCEEDS;
	                }
	                //拼接文件保存路径
	                String url=pathname+docname;
	                String prefix = docname.substring(0, docname.lastIndexOf(".")-1);
	                int i=0;
	                while(true){
	                	if(new File(url).exists()){
	                		i++;
	                		docname=prefix+i+"."+suffix;
	                		url=pathname+docname;
	                	}else {
							break;
						}
	                }
	                out = new BufferedOutputStream(new FileOutputStream(new File(url)));
	                out.write(file.getBytes());
	                out.flush();
	              
	                return docname;
	            } catch (IOException e) {
	                e.printStackTrace();
	                return ContextConstant.UPLOAD_FAILURE;
	            }finally{
	                try {
	                	if(out!=null){
	                		out.close();
	                	}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	            }
	        
	        } else {
	            return ContextConstant.IMAGE_NULL;
	        }
	}
	
	
	/**
	 * ps: 这是一个备用的方法
     * @desc ：微信上传素材的请求方法
     *  
     * @param requestUrl  微信上传临时素材的接口url
     * @param file    要上传的文件MulipartFile，是用来spring的接受图片的，
     * 可以换成File等
     * @return String  上传成功后，微信服务器返回的消息
     */
    public static String weixinUpload(String requestUrl, MultipartFile file) {  
        StringBuffer buffer = new StringBuffer();  
        OutputStream outputStream = null;
        DataInputStream in = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try{
            //1.建立连接
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();  //打开链接
            
            //1.1输入输出设置
            httpUrlConn.setDoInput(true);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setUseCaches(false); // post方式不能使用缓存
            //1.2设置请求头信息
            httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConn.setRequestProperty("Charset", "UTF-8");
            //1.3设置边界
            String BOUNDARY = "----------" + System.currentTimeMillis();
            httpUrlConn.setRequestProperty("Content-Type","multipart/form-data; boundary="+ BOUNDARY);

            // 请求正文信息
            // 第一部分：
            //2.将文件头输出到微信服务器
            StringBuilder sb = new StringBuilder();
            sb.append("--"); // 必须多两道线
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"" + file.getSize()
                    + "\";filename=\""+ file.getOriginalFilename() + "\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");
            byte[] head = sb.toString().getBytes("utf-8");
            // 获得输出流
            outputStream = new DataOutputStream(httpUrlConn.getOutputStream());
            // 将表头写入输出流中：输出表头
            outputStream.write(head);

            //3.将文件正文部分输出到微信服务器
            // 把文件以流文件的方式 写入到微信服务器中
            in = new DataInputStream(file.getInputStream());
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                outputStream.write(bufferOut, 0, bytes);
            }
            //4.将结尾部分输出到微信服务器
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
            outputStream.write(foot);
            outputStream.flush();

            
            //5.将微信服务器返回的输入流转换成字符串  
            inputStream = httpUrlConn.getInputStream();  
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            bufferedReader = new BufferedReader(inputStreamReader);  
            
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
            
            // 释放资源  
            inputStream = null;  
            httpUrlConn.disconnect();  
            return buffer.toString();
        } catch (IOException e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        } finally {
        	 try {
        		 if (outputStream != null) {
        			 outputStream.close();;
        		 }
        		 if (in != null ) {
        			 in.close();
        		 }
        		 if (inputStream != null) {
        			 inputStream.close();;
        		 }
        		 if (inputStreamReader != null) {
        			 inputStreamReader.close();;
        		 }
        		 if (bufferedReader != null) {
        			 bufferedReader.close();;
        		 }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return null;
    }
	
	
	
}
