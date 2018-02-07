package com.weikefu.service;

import java.util.List;

import com.weikefu.po.ShopCustPo;
import com.weikefu.po.UserInfo;

public interface ShopCustPoStatusService {

//	public boolean ShopCustPo(String shopId, String custId);
	
	public void joinUserNum(String shopid, String custid, int num);
	
	public ShopCustPo selectShopCustPo(String shopId, String custId);
	
	public String selectMinCust(Integer shopid);

	public void updateCustOnlineTime(Integer shopId, Integer custId, String custServerStatus);
	
	List<ShopCustPo> findListByCustIdAndShopId(String custId, String shopId);

	void changeCustServiceUser(String id, String serveruserid);
	/**
	 * ͨ��shopId�������еĿͷ�
	 */
	List<ShopCustPo> findAllCust(Integer shopId);
	/**
	 * �ؼ��ֵ�����
	 * @param keyWord
	 * @param keyWord2 
	 * @return
	 */
	List<ShopCustPo> searchShopCustBykeyword(String shopId, String keyWord);

	String loginShopCustPo(ShopCustPo shopCustPo);
	/**
	 * 
	 * 当serviceId为0 的时候表示1时候，表示将对应的serviceId为空
	 * @param shopId
	 * @param custId
	 * @param status
	 * @param serviceId
	 */
	void changeCustStatus(String shopId, String custId, String status,int serviceId);

	/**
	 * 修改客服的最大接入人数
	 * @param shopId
	 * @param custId
	 * @param maxusernum
	 */
	public void updateMaxusernum(String shopId, String custId, Integer maxusernum);

	public List<ShopCustPo> findBycustserverstatus(String shopId, String custserverstatus);


	public List<UserInfo> searchUser(String shopId, String keyWord);

	public boolean isStatusByShopCustStatus(String shopId, String custId, String...custStatus);
}
