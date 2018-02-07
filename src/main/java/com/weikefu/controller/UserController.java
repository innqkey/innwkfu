package com.weikefu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weikefu.cache.RedisUserShopTalkCache;
import com.weikefu.constant.ContextConstant;
import com.weikefu.po.Message;
import com.weikefu.po.UserInfo;
import com.weikefu.service.AllotCustService;
import com.weikefu.service.UserInfoService;
import com.weikefu.util.ConvertUtils;
import com.weikefu.util.JacksonUtils;
import com.weikefu.util.MessageUtils;
import com.weikefu.util.ResUtils;
import com.weikefu.vo.smallroutine.WeiXinMessage;
import com.weikefu.vo.smallroutine.WeiXinUserInfo;



/**
* 用于接受    用户在进入小程序“客服会话按钮”产生的数据包
* @author 作者
* @version 创建时间：2018年1月25日 上午11:33:27
* 
*/
@RequestMapping("/user")
@RestController
public class UserController {

	@Value("${image.upload-path}")
	private String path;
	
	@Value("${image.server}")
	private String imageServer;
	
	@Autowired
	private  UserInfoService userInfoService;
	@Autowired
	private RedisUserShopTalkCache shopUserTalk;
	
	@Autowired
	private AllotCustService allotSer;
	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	
	
	/**
	 * 当小程序的用户进入客服界面的时候，会发送数据流
	 * 这里不需要用户的数据，只要用户的openid,对应商店的shopId，和加入的方式
	 * @param request
	 */
	@RequestMapping(value = "/userJoin",method=RequestMethod.POST)
	public String userJoin(HttpServletRequest request,@RequestBody @Valid  WeiXinUserInfo weiXinUserInfo,BindingResult result) {
		//用来校验参数是否为空
		if (result.hasErrors()) {
			return ResUtils.execRes();
		}
		try {
			String shopId = weiXinUserInfo.getShopId();
			Map<String, String> userMap = weiXinUserInfo.getUser();
			UserInfo userInfo = new UserInfo();
			ConvertUtils.convertDtoAndVo(userMap, userInfo);
			userInfo.setJoinway(ContextConstant.JOINWAY_SMALLROUTINE);
			userInfo.setWeiuserid(userMap.get("id"));
			userInfo.setOpenid(userMap.get("xcx_openid"));
			userInfo.setAccess_token(weiXinUserInfo.getToken());
			userInfo.setExpireTime(weiXinUserInfo.getExpireTime());
			
			logger.info(new StringBuilder().append("用户加入  ：shopId是").append(shopId).append("--用户信息是: ").append(userInfo.getOpenid()).toString());
			String userId = userInfoService.saveUser(userInfo);
			allotSer.userJoin(userId, shopId, null);
			
			return ResUtils.okRes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResUtils.execRes();
	}
	
	/***
	 * 用来接受微信小程序的接口，有商城的接口提供对应的转发，
	 * 所有的参考数据来自小程序的官方文档
	 * https://mp.weixin.qq.com/debug/wxadoc/dev/api/custommsg/receive.html#进入会话事件
	 * @return
	 */
	@RequestMapping("/receiveMessage")
	public String receive(@RequestBody @Valid WeiXinMessage weiXinMessage,BindingResult result, HttpServletRequest request) {
		try {   
				//用来校验参数是否为空
				if (result.hasErrors()) {
					return ResUtils.execRes();
				}
				logger.info("接受用户发送的消息：" + JacksonUtils.toJson(weiXinMessage));
				Map<String, String> messageWeixin = weiXinMessage.getMessage();
				Message message = new Message();
				message.setShopid(Integer.valueOf(weiXinMessage.getShopId()));
				String msgType = messageWeixin.get("MsgType");
				if (StringUtils.isNotBlank(msgType) && StringUtils.isNotBlank(messageWeixin.get("FromUserName"))
						&& 	StringUtils.isNotBlank(messageWeixin.get("ToUserName"))) {
					UserInfo userInfo = userInfoService.findByOpenIdAndJoinWay(messageWeixin.get("FromUserName"),ContextConstant.JOINWAY_SMALLROUTINE);
					//当没有改用户的时
					if (userInfo != null) {
						//如果没有被分配的话，重新配后在发送消息
						List<String> custIdList = shopUserTalk.getShopTalkList(userInfo.getUserId(), weiXinMessage.getShopId());
						if (custIdList == null || custIdList.size() < 1) {
							allotSer.userJoin(userInfo.getUserId(), weiXinMessage.getShopId(), null);
						}
						sendMessage(messageWeixin, message, msgType, userInfo);
						return ResUtils.okRes();
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.execRes();
		}
		return ResUtils.execRes();

	}

	private void sendMessage(Map<String, String> messageWeixin, Message message, String msgType, UserInfo userInfo) {
		message.setHeadurl(userInfo.getHeadimgurl());
		message.setSendway(ContextConstant.SEND_USERWAY);
		message.setUserid(userInfo.getUserId());
		message.setUsername(message.getUsername());
		message.setMsgtype(msgType);
		message.setCreatetime(new Date());
		if (ContextConstant.MES_IMAGE.equals(msgType)) {
			//和有咱一样，从服务器中奖图片下载后，放入图片服务器
			String picUrl = messageWeixin.get("PicUrl");
			//直接写死为小程序
			picUrl = downLoadImage(picUrl,path,ContextConstant.SMALLROUTINUE_SAVE_NAME);
			picUrl = imageServer + "/file/displayImage/" + picUrl;
			message.setMessage(picUrl);
			message.setMsgtype(msgType);
		}else if (ContextConstant.MES_CARD.equals(msgType)) {
			message.setMsgtype(msgType);
			String downLoadImage = downLoadImage(messageWeixin.get("ThumbUrl"), path, ContextConstant.SMALLROUTINUE_SAVE_NAME);
			downLoadImage = imageServer + "/file/displayImage/" + downLoadImage;
			message.setProduct_imgurl(downLoadImage);
			message.setProduct_name(messageWeixin.get("Title"));
			String PagePath = messageWeixin.get("PagePath");
			if (StringUtils.isNotBlank(PagePath)) {
				JSONObject parseObject = JSON.parseObject(PagePath);
				if (parseObject != null) {
					
					message.setMessage(parseObject.getString("url"));
					String price = parseObject.getString("price");
					price = price.substring(0, price.indexOf("&"));
					message.setProduct_price(price);
				}
			}
			//否则就是text格式
		}else {
			//小程序的原始的Id
			message.setMessage(messageWeixin.get("Content"));
			message.setMsgtype(ContextConstant.MES_TEXT);
		}
		
		MessageUtils.userSendMesage(null, message);
	}

	/***
	 * 根据微信提供的路径从服务器中奖图片下载到本地服务器中去。
	 * @param picUrl
	 * @param savePath 保存的路径
	 * @param shopId 这里写死就是小程序
	 * @return
	 */
	private String downLoadImage(String picUrl,String savePath,String shopId) {
		InputStream is = null;
		OutputStream os = null;
		try {
			if (StringUtils.isNotBlank(picUrl)) {
				URL url = new URL(picUrl);
				URLConnection connection = url.openConnection();
				//设置请求超时的时间5秒钟
				connection.setConnectTimeout(5 * 1000);
				is= connection.getInputStream();
				byte[] bs = new byte[1024];
				File file = new File(savePath + shopId);
				if (!file.exists()) {
					file.mkdirs();
				}
				String filename =System.nanoTime() + ".jpg";
				os = new FileOutputStream(file.getPath()+"/"+filename);    
				// 开始读取 
				int len ;
				while ((len = is.read(bs)) != -1) {    
					os.write(bs, 0, len);    
				}
				
				return  shopId + "/" + filename;
			}
			
		} catch (Exception e) {
			//抛出异常是了调用的可以抓取到，最后返回失败的消息
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		
		return null ;
	}
	
	
}
