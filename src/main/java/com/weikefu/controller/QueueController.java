package com.weikefu.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.weikefu.cache.RedisQueueCache;
import com.weikefu.service.DialogService;
import com.weikefu.util.ResUtils;
import com.weikefu.vo.UserInfoVo;

@RestController
@RequestMapping("/queueController")
public class QueueController {

	@Autowired
	private RedisQueueCache queueCache; //排队队列

	@Autowired
	private DialogService dialogServie;
	
	/**
	 * 获取队列排队等待人数
	 * @param request
	 * @param shopId
	 * @return
	 */
	@RequestMapping("/getQueueSize")
	public String getQueueSize(HttpServletRequest request, String shopId){
		try {
			if(StringUtils.isEmpty(shopId)){
				return ResUtils.errRes("102", "请求参数错误");
			}
			int size = queueCache.getQuequSize(shopId);
			return ResUtils.okRes(size);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 根据分页信息获取对应的等待用户信息
	 * @param request
	 * @param shopId
	 * @param page
	 * @return
	 */
	@RequestMapping("/getQuequUserList")
	public String getQuequUserList(HttpServletRequest request, String shopId, String custId){
		try {
			if(StringUtils.isEmpty(shopId)){
				return ResUtils.errRes("102", "请求参数错误");
			}
			List<UserInfoVo> queueUserList = dialogServie.queueUserInfoVoList(shopId, custId);
			return ResUtils.okRes(queueUserList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}
}
