package com.weikefu.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weikefu.dao.mangodb.UserInfoDao;
import com.weikefu.po.UserInfo;
import com.weikefu.service.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService {
	@Autowired
	private UserInfoDao userInfoDao;
	
	/**
	 * 保存用户的信息，先查询，如果存在就更新，如果不存在的话，那么就添加。
	 * 
	 */
	@Override
	public String saveUser(UserInfo userInfo) {
		UserInfo info = null;
		//下面这么做的原因是应为防止没有openid出现的异常
		if (StringUtils.isNotBlank(userInfo.getOpenid()) ) {
			info = userInfoDao.findByOpenidAndWeiuseridAndJoinway(userInfo.getOpenid(),userInfo.getWeiuserid(),userInfo.getJoinway());
		}
		
		if (StringUtils.isBlank(userInfo.getOpenid()) && StringUtils.isNotBlank(userInfo.getWeiuserid())) {
			info = userInfoDao.findByWeiuseridAndJoinway(userInfo.getWeiuserid(),userInfo.getJoinway());
		}
		//这个是更新操作
		if (info != null) {
			userInfo.setUserId(info.getUserId());
			UserInfo save = userInfoDao.save(userInfo);
			return save.getUserId();
		}
		
		//插入操作
		UserInfo save = userInfoDao.insert(userInfo);
		return save.getUserId();
		
	}

	@Override
	public UserInfo findByUserId(String userId) {
		
		return userInfoDao.findByUserId(userId);
	}

	@Override
	public UserInfo findByOpenIdAndJoinWay(String string, String joinwaySmallroutine) {
		UserInfo userInfo = userInfoDao.findByOpenidAndJoinway(string,joinwaySmallroutine);
		return userInfo;
	}
	
}
