package com.weikefu.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weikefu.constant.ContextConstant;
import com.weikefu.po.ImagePo;
import com.weikefu.po.UserInfo;
import com.weikefu.service.FileService;
import com.weikefu.service.UserInfoService;
import com.weikefu.util.ResUtils;
import com.weikefu.util.UploadUtils;
import com.weikefu.vo.PageTemp;

/**
 * 文件的controller，用于所有的
 * 文件的上传和下载使用
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/file")
public class FileController {
	
	
	@Value("${image.upload-path}")
	private String path;
	
	@Value("${image.server}")
	private String imageServer;
	
	@Value("${weixin.uploadImage}")
	private String weixinUrl;
	@Value("${http.wsc.url}")
	public String serverUrl;
	
	@Autowired
	private FileService fileService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private RestTemplate restTemplate;
	
	private final Logger logger = LoggerFactory.getLogger(FileController.class);
	/**
	 * 用于图片的上传
	 * @return
	 */
	@RequestMapping(value = "/uploadImage",method=RequestMethod.POST)
	public String uploadImage(@RequestParam("file") MultipartFile  file,String shopId,String custId,String userId,String joinway){
		if (StringUtils.isBlank(shopId) || (StringUtils.isBlank(custId) && StringUtils.isBlank(userId))) {
			return ResUtils.errRes("404", "参数不能为空");
		}
		
		HashMap<String, String> map = new HashMap<String,String>();
		//先上传微信的服务器，然后在存储本地
		if (StringUtils.isNotBlank(joinway) && ContextConstant.JOINWAY_SMALLROUTINE.equals(joinway)) {
			UserInfo userInfo = userInfoService.findByUserId(userId);
			if (userInfo != null) {
				String accesstoken = getAccessToken(shopId, userInfo);
				//解析微信发来的消息
				weixinUrl = new StringBuilder().append(weixinUrl).append("?access_token=").append(accesstoken).append("&type=image").toString();
				String weixinUpload = UploadUtils.weixinUpload(weixinUrl, file);
				JSONObject parseObject = JSON.parseObject(weixinUpload);
				String mediaId = parseObject.getString("media_id");
				//为空的话那么access_token就是为空的
				if (StringUtils.isNotBlank(mediaId)) {
					map.put("mediaId", mediaId);
				}else {
					return ResUtils.execRes();
				}
				
			}else {
				return ResUtils.execRes();
			}
		}
		
		//存储本地
		String	 custIdAndImageName = UploadUtils.uploadimage(file, path, shopId);
		String url = imageServer + "/file/displayImage/" + custIdAndImageName;
		if (!custIdAndImageName.contains(".")) {
			return ResUtils.errRes(custIdAndImageName, "异常");
		}
		ImagePo imagePo = new ImagePo();
		if (StringUtils.isNotBlank(custId)) {
			imagePo.setCustid(custId);
		}else {
			imagePo.setUserid(userId);
		}
		
		imagePo.setShopid(shopId);
		imagePo.setUrl(url);
		imagePo.setUserid(userId);
		imagePo.setCreatetime(new Date());
		imagePo.setWeixin_Media_id(map.get("mediaId"));
		imagePo.setTitle(file.getOriginalFilename());
		//成功之后就保存本地的服务器
		fileService.saveImage(imagePo);
		map.put("url", url);
		
		return ResUtils.okRes(map);
	}
	/**
	 * 获取access_token的内容
	 * @param shopId
	 * @param userInfo
	 * @return
	 */
	private String getAccessToken(String shopId, UserInfo userInfo) {
		String accesstoken="";                
		//如果数据库中已经在该access_token，并且没有过期的话，那么直接从数据库中直接获取就ok
		if (userInfo.getExpireTime() != null && userInfo.getExpireTime() > new Date().getTime()/1000 
				&& StringUtils.isNotBlank(userInfo.getAccess_token())) {
				accesstoken = userInfo.getAccess_token();
		}else {
			String url = new StringBuilder().append(serverUrl).append(ContextConstant.GET_ACCESS_TOKEN).append("?shopId=").append(shopId).toString();
			String response = restTemplate.getForObject(url, String.class);
			if (StringUtils.isNotBlank(response)) {
				JSONObject parseObject = JSON.parseObject(response);
				JSONObject jsonObject = parseObject.getJSONObject("data");
				if (jsonObject != null && StringUtils.isNotBlank(jsonObject.getString("token"))
						&& jsonObject.getLong("expireTime") != null) {
					accesstoken = jsonObject.getString("token");
					userInfo.setAccess_token(jsonObject.getString("token"));
					userInfo.setExpireTime(jsonObject.getLong("expireTime"));
				}
			}
		}
		return accesstoken;
	}
	
	/**
	 *  专门用于图片的上传内容
	 * @param request
	 * @param dir
	 * @param imageName
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/displayImage/{dir}/{imageName}")
	public String displayImage(HttpServletRequest request,@PathVariable("dir")String dir
			,@PathVariable("imageName")String imageName,HttpServletResponse response) {
		if (StringUtils.isBlank(dir) || StringUtils.isBlank(imageName)) {
			return ResUtils.exceCode;
		}
		
		String requestURI = request.getRequestURI();
		String suffix = requestURI.substring(requestURI.lastIndexOf(".")+1);
		
		OutputStream outputStream = null;
		FileInputStream fileInputStream = null;
		try {
			StringBuilder url = new StringBuilder();
			url.append(path).append(dir).append("/").append(imageName).append(".").append(suffix);
			// 度图片
			fileInputStream = new FileInputStream(url.toString());
			response.setContentType("image/png");
			outputStream = response.getOutputStream();
			int available = fileInputStream.available();
			byte[] data = new byte[available];
			fileInputStream.read(data);
			outputStream.write(data);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.exceCode;
		} finally {
			try {
				if (fileInputStream != null){
					fileInputStream.close();
				}
				if (outputStream != null){
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return ResUtils.exceCode;
			}
		}
		return ResUtils.okRes();
	}
	/**
	 * 获取对应的对应的图片
	 * @param request
	 * @param shopId
	 * @param custId
	 * @param pageTemp
	 * @return
	 */
	@RequestMapping("/getImages")
	public String getImages(HttpServletRequest request,String shopId,String custId,PageTemp pageTemp){
		if (StringUtils.isBlank(shopId) || StringUtils.isBlank(custId)) {
			return ResUtils.errRes("404", "参数丢失");
		}
		Page<ImagePo> result= fileService.findImageByShopIdAndCustId(shopId,custId,pageTemp);
		return ResUtils.okRes(result);
	}
	/**
	 * 用来读取微信的声音
	 * @param request
	 * @return
	 */
	@RequestMapping("/weixinVoice")
	public String readWeixinVoice(HttpServletRequest request){
		return ResUtils.okRes();
	}
	
	/**
	 * 如果是小程序，但是mediaId为空的时候，
	 *这个时候将对应的图片的消息放上传到微信的小程序，最后传送到前台中去
	 * @param imageUrl
	 * @param shopId
	 * @return
	 */
	@RequestMapping("/imageToWeiXin")
	public String copyImageToWeixin(String imageUrl,String shopId,String userId) {
		//先判断该路径在服务器中是否存在，存在后上传图片
		try {
			ImagePo imagePo = fileService.findByUrlAndShopid(imageUrl,shopId);
			if (imagePo != null && StringUtils.isNotBlank(imagePo.getUrl())) {
				String url = imagePo.getUrl();
				int lastIndexOf = url.lastIndexOf("displayImage");
				if (lastIndexOf > 0) {
					url = url.substring(lastIndexOf + 13, url.length());
					UserInfo userInfo = userInfoService.findByUserId(userId);
					if (userInfo != null) {
						String accesstoken = getAccessToken(shopId, userInfo);
						//解析微信发来的消息
						weixinUrl = new StringBuilder().append(weixinUrl).append("?access_token=").append(accesstoken).append("&type=image").toString();
						String weixinUpload = UploadUtils.weixinUpload(weixinUrl, new MockMultipartFile(imagePo.getTitle(),imagePo.getTitle(),null,new FileInputStream(path + url)));
						JSONObject parseObject = JSON.parseObject(weixinUpload);
						if (parseObject != null && StringUtils.isNotBlank(parseObject.getString("media_id"))) {
							String media_id = parseObject.getString("media_id");
							imagePo.setWeixin_Media_id(media_id);
							fileService.saveImage(imagePo);
							return ResUtils.okRes(media_id);
						}else {
							//上传失败
							logger.error("the action of uploading image failed ,error info:" + weixinUpload );
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResUtils.execRes();
	}
	
}
