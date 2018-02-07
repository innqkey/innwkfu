package com.weikefu.constant;

import org.apache.commons.lang.StringUtils;


public class DictConConstant {

	public enum Paytype{
        //通过括号赋值,而且必须带有一个参构造器和一个属性跟方法，否则编译出错
        //赋值必须都赋值或都不赋值，不能一部分赋值一部分不赋值；如果不赋值则不能写构造器，赋值编译也出错
		//支付方式：0未支付；1微信支付；2支付宝支付；3储值余额支付；4货到付款/到店付款；5找人代付；6领取赠品；7优惠兑换；8银行卡支付；9会员卡支付；10小程序支付
		paytype0(0,"未支付"), paytype1(1,"微信支付"), paytype2(2,"支付宝支付"), paytype3(3,"储值余额支付"), 
		paytype4(4,"货到付款/到店付款"), paytype5(5,"找人代付"), paytype6(6,"领取赠品"), paytype7(7,"优惠兑换"), 
		paytype8(8,"银行卡支付"), paytype9(9,"会员卡支付"),paytype10(10,"小程序支付");
        
		private int i;
        private String val;

        //构造器默认也只能是private, 从而保证构造函数只能在内部使用
        Paytype(int i,String val) {
        	this.i = i;
            this.val = val;
        }
        
        public String getVal() {
            return val;
        }
        public int getI() {
            return i;
        }
    }
	
	public static String getDicName(String type,Integer i){
		if(StringUtils.isBlank(type)||null==i){
			return "";
		}
		type = type.toUpperCase();
		if(DictConConstant.Paytype.class.getName().toUpperCase().contains(type)){
			for(Paytype itmeType : Paytype.values()){
				if(itmeType.i==i){
					return itmeType.val;
				}
	        }
		}
		return null;
	}
	
}
