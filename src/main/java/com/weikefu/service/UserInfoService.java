package com.weikefu.service;

import com.weikefu.po.UserInfo;

public interface UserInfoService {

	String saveUser(UserInfo userInfo);

	UserInfo findByUserId(String userId);

	UserInfo findByOpenIdAndJoinWay(String string, String joinwaySmallroutine);

}
