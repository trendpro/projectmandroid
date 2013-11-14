package com.projectman;

public class ShopDataType {
	
	/**
	 * Class variables;
	 */
	String shopName;
	String shopLat;
	String shopLong;
	String shopId;
	
	public ShopDataType(String nm,String lat, String lon, String id){
		this.shopName = nm;
		this.shopLat = lat;
		this.shopLong = lon;
		this.shopId = id;
		
	}
	
	public String getShopName(){
		return this.shopName;
	}
	
	public String getShopLat(){
		return this.shopLat;
	}
	
	public String getShopLong(){
		return this.shopLong;
	}
	
	public String getShopId(){
		return this.shopId;
	}
}
