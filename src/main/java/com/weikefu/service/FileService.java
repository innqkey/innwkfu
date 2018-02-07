package com.weikefu.service;


import org.springframework.data.domain.Page;

import com.weikefu.po.ImagePo;
import com.weikefu.vo.PageTemp;

public interface FileService {

	public Page<ImagePo> findImageByShopIdAndCustId(String shopId, String custId, PageTemp pageTemp);

	public void saveImage(ImagePo imagePo);

	public ImagePo findByUrlAndShopid(String imageUrl, String shopId);

}
