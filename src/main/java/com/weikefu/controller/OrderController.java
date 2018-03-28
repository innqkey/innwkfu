package com.weikefu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weikefu.constant.ContextConstant;
import com.weikefu.constant.DictConConstant;
import com.weikefu.util.ResUtils;
import com.weikefu.vo.PageInfo;
import com.weikefu.vo.PageTemp;
import com.weikefu.vo.UserOrderVo;

/** 
* @author qinkai 
* @date 2018年1月4日
*/
@RestController
@RequestMapping(value = "/order")
public class OrderController {
	@Autowired
	private RestTemplate template;
	@Value("${http.wsc.url}")
	private String httpWscUrl;
	/**
	 * 获取单个用户订单数据
	 * @param shopId
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/getUserOrders")
	public String getUserOrders(String shopId, String userId){
		if (StringUtils.isEmpty(shopId) || StringUtils.isEmpty(userId)) {
			return ResUtils.errRes("404", "请求参数有误");
		}
		List<UserOrderVo> orderVos = new ArrayList<UserOrderVo>();
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("shopId", shopId);		
		hashMap.put("userId", userId);
		// 调用接口获取信息
		JSONObject jsonObject = template.getForObject(httpWscUrl + ContextConstant.GET_USER_ORDER_DATA + "?shopId={shopId}&userId={userId}",
				JSONObject.class, hashMap);
		JSONObject data = jsonObject.getJSONObject("data");
		String baseUrl = "https://hsshop.huisou.cn/";
		if (data != null) {
			JSONArray jsonArray = data.getJSONArray("userOrderData");
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					UserOrderVo orderVo = new UserOrderVo();
					JSONObject object = jsonArray.getJSONObject(i);
					JSONArray orderDetail = object.getJSONArray("orderDetail");
					JSONObject order = orderDetail.getJSONObject(0);
					orderVo.setOrderId(object.getString("id"));
					orderVo.setOrderNumber(object.getString("oid"));
					orderVo.setPayTime(object.getString("created_at"));
					orderVo.setOrderTime(object.getString("updated_at"));
					orderVo.setPrice(object.getString("pay_price"));				
					int payway = object.getInteger("pay_way");
					orderVo.setPayType(DictConConstant.getDicName("Paytype", payway));
					orderVo.setBuyNum(order.getInteger("num"));
					orderVo.setOrderName(order.getString("title"));
					orderVo.setOrderImg(baseUrl + order.getString("img"));
					orderVos.add(orderVo);
				}
				return ResUtils.okRes(orderVos);
			}
		}
		return ResUtils.okRes(orderVos);
	}
	
	/**
	 * 新订单消息列表
	 * @param shopId
	 * @return
	 */
	@RequestMapping(value = "/getAllUserOrders")
	public String getAllUserOrders(String shopId,PageTemp pageTemp){
		if (StringUtils.isEmpty(shopId)) {
			return ResUtils.errRes("404", "请求参数有误");
		}
		List<UserOrderVo> orderVos = new ArrayList<UserOrderVo>();
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("shopId", shopId);
		hashMap.put("page", pageTemp.getPageNum().toString());
		JSONObject jsonObject = template.getForObject(httpWscUrl + ContextConstant.GET_NEW_ORDER_NOTIFICATION + "?shopId={shopId}",
				JSONObject.class, hashMap);
		JSONObject data = jsonObject.getJSONObject("data");
		if (data != null) {
			JSONArray jsonArray = data.getJSONArray("data");
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject orderObject = jsonArray.getJSONObject(i);
					UserOrderVo userOrderVo = new UserOrderVo();
					userOrderVo.setOrderId(orderObject.getString("relate_order_id"));
					userOrderVo.setOrderNumber(orderObject.getString("oid"));
					userOrderVo.setOrderTime(orderObject.getString("created_at"));
					userOrderVo.setOrderImg(orderObject.getString("goods_img"));
					userOrderVo.setOrderName(orderObject.getString("goods_name"));
					userOrderVo.setBuyNum(orderObject.getInteger("goods_num"));
					userOrderVo.setPrice(orderObject.getString("order_pay"));
					orderVos.add(userOrderVo);
				}
				
				//不使用mybatis的分页的，换成这样的分页
				PageInfo<UserOrderVo> pageInfo = new PageInfo<UserOrderVo>(orderVos);
				pageInfo.setTotal(orderVos.size());
				pageInfo.setPages(pageTemp.getPageNum());
				if (orderVos.size() > 0) {
					ArrayList<UserOrderVo> list = new ArrayList<UserOrderVo>();
					//对list进行分页
					int currIdx = (pageTemp.getPageNum() > 1 ? (pageTemp.getPageNum() -1) * pageTemp.getPageSize() : 0);
					for (int i = 0; i < pageTemp.getPageSize() && i < orderVos.size() - currIdx; i++) {
						list.add(orderVos.get(i));
					}
					if (list.size() > 0) {
						orderVos = list;
					}
				}
				
				return ResUtils.okRes(pageInfo);
			}
		}
		return ResUtils.okRes(orderVos);
	}
	/**
	 * 新订单消息数量
	 * @param shopId
	 * @return
	 */
	@RequestMapping(value = "/getNewOrderCount")
	public String getNewOrderCount(String shopId){
		if (StringUtils.isEmpty(shopId)) {
			return ResUtils.errRes("404", "请求参数有误");
		}
		JSONObject data;
		try {
			HashMap<String, String> hashMap = new HashMap<>();
			hashMap.put("shopId", shopId);
			JSONObject jsonObject = template.getForObject(httpWscUrl + ContextConstant.GET_NEW_ORDER_COUNT + "?shopId={shopId}",
					JSONObject.class, hashMap);
			data = jsonObject.getJSONObject("data");
			return ResUtils.okRes(data.getInteger("order_count"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResUtils.execRes("异常");
	}
	/**
	 * 清空新订单消息
	 * @param shopId
	 * @return
	 */
	@RequestMapping(value = "/clearOrderNotification")
	public String clearOrderNotification(String shopId){
		if (StringUtils.isEmpty(shopId)) {
			return ResUtils.errRes("404", "请求参数有误");
		}
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("shopId", shopId);		

		JSONObject body = template.postForObject(httpWscUrl + ContextConstant.CLEAR_ORDER_NOTIFICATION + "?shopId={shopId}", null, JSONObject.class, hashMap);
		Integer code = body.getInteger("errCode");
		Integer integer = new Integer(0);
		if (integer.equals(code)){
			return ResUtils.okRes();
		} else{
			return ResUtils.errRes("404", body.getString("errMsg"));
		}
	}
}
