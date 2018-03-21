package com.weikefu.controller;


import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weikefu.constant.ContextConstant;
import com.weikefu.po.ShopCustPo;
import com.weikefu.service.ShopCustPoStatusService;
import com.weikefu.util.Base64Utils;
import com.weikefu.util.ConvertUtils;
import com.weikefu.util.CookieUtils;
import com.weikefu.util.FileUtils;
import com.weikefu.util.MD5Utils;
import com.weikefu.util.ResUtils;
import com.weikefu.vo.CustomerBaseInfo;

/***
 * 用于客服身份的验证，验证成功
 * 后跳转到客服的登录。
 * 需要知道客服的id，custname，headurl
 * @author Administrator
 *
 */
@RequestMapping("/authority")
@RestController
public class LoginController {
	
	
	@Value("{sign.switch}")
	private String signSwitch;
	@Autowired
	private ShopCustPoStatusService shopCustService;
	@Autowired
	private RestTemplate template;
	
	private String httpWscUrl = FileUtils.getApplicationPro("http.wsc.url");

	
	/**
	 * 登录验证请求，token
	 * php验证完成后，发送消息给这个端口
	 * @param request
	 * @param custName
	 * @param custId
	 * @param headUrl
	 * @return 1
	 */
	@RequestMapping(value = "/login")
	public String login(HttpServletRequest request,HttpServletResponse response,String shopId,String custId,String sign){			
		try {
			if (StringUtils.isBlank(shopId) || StringUtils.isBlank(custId)|| StringUtils.isBlank(sign)) {
				return ResUtils.errRes("405", "参数丢失");
			}
			//判断是否开启令牌
			if ("1".equals(signSwitch)) {
				String token = new StringBuilder().append(shopId).append(custId).append(ContextConstant.TOKEN).toString();
				token =MD5Utils.md5Encode(token);
				//验证sign中的对应是否是发过来的信息
				if (!sign.equals(token)) {
					return ResUtils.execRes("令牌错误");
				}
			}
			HashMap<String, String> hashMap = new HashMap<>();
			hashMap.put("shopId", shopId);
			//调用接口获取信息
			JSONObject jsonObject = template.getForObject(httpWscUrl + ContextConstant.GET_CHAT_DATA +"?shopId={shopId}", JSONObject.class,hashMap);
			JSONObject data = jsonObject.getJSONObject("data");
			if (data != null) {
				String shopName = "汇好微商城客服";
				String logo = "https://hsshop.huisou.cn/home/image/huisouyun_120.png";
				JSONObject shopJson = data.getJSONObject("shopData");
				if(null!=shopJson){
					shopName = shopJson.getString("shop_name");
					logo = shopJson.getString("logo");
				}
				JSONArray jsonArray = data.getJSONArray("manager");
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i <jsonArray.size(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						String tempId = object.getString("id");
						//如果
						if (tempId != null && custId.equals(tempId)) {
							ShopCustPo shopCustPo = new ShopCustPo();
							shopCustPo.setCustname(object.getString("name"));
							shopCustPo.setCustid(object.getInteger("id"));
							shopCustPo.setShopid(Integer.valueOf(shopId));
							shopCustPo.setHeadurl(object.getString("head_pic")); 
							shopCustPo.setIsheader(object.getString("is_header"));
							
							String result = shopCustService.loginShopCustPo(shopCustPo);
							//登录失败
							if (!result.equals(ContextConstant.LOGIN_OK)) {
								return ResUtils.errRes(ContextConstant.LOGIN_EXCEPTION, "登录失败");
							}
							//登录成功后将对应的内容添加到cookie中,请求转发
							String kefu_token = (shopId + "-" + custId + ContextConstant.TOKEN_SALT);
							kefu_token = MD5Utils.md5Encode(MD5Utils.md5Encode(kefu_token));
							ShopCustPo custPo = shopCustService.selectShopCustPo(shopId,custId);
							CustomerBaseInfo customerBaseInfo = (CustomerBaseInfo) ConvertUtils.convertDtoAndVo(custPo, CustomerBaseInfo.class);
							customerBaseInfo.setCrm_token(kefu_token);
							customerBaseInfo.setShopId(String.valueOf(custPo.getShopid()));
							customerBaseInfo.setCustId(custId);
							customerBaseInfo.setShopname(shopName);
							customerBaseInfo.setLogo(logo);
							String encode = new StringBuilder().append(shopId).append("-").append(custId).append("-82-36-19").toString();
							encode= Base64Utils.encode(encode);
							encode=URLEncoder.encode(encode, "utf-8");
							CookieUtils.writeCookie(response, ContextConstant.COOKIE_TOKEN,encode, 7*24*3600);
							//成功后使用浏览器
							return ResUtils.okRes(customerBaseInfo);
							}
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.execRes( "登录异常");
		}
		return ResUtils.execRes("登录失败，用户不存在！");
	}
	/**
	 * 需要放在php去操作，删除cookie中的token，不然没法删除cookie
	 * @param request
	 * @param response
	 * @param shopId
	 * @param custId
	 * @return
	 */
	@RequestMapping("/loginOut")
	public String loginOut(HttpServletRequest request,HttpServletResponse response,String shopId,String custId){
		//只是修改对应的用户的状态
		try {
			if (StringUtils.isBlank(shopId) || StringUtils.isBlank(custId)) {
				return ResUtils.execRes( "参数异常");
			}
			//为0的时候表示服务的人置为空
			shopCustService.changeCustStatus(shopId,custId,ContextConstant.CustomerStatus.OFFLINE.toString(),0);
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.execRes();
		}	
		return ResUtils.okRes();
	}
	
	
	
}
