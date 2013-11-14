package com.projectman;

public class ShopDataType {
	
	/**
	 * Class variables;
	 */
	private String shopName;
	private String shopLat;
	private String shopLong;
	private String shopId;
	
	public ShopDataType(String nm,String lat, String lon, String id){
		this.shopName = nm;
		this.shopLat = lat;
		this.shopLong = lon;
		this.shopId = id;
		
	}
	
	public ShopDataType(){
		this.shopName = null;
		this.shopLat = null;
		this.shopLong = null;
		this.shopId = null;
		
	}
	
	/**
	 * Getters.
	 */
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
	
	/**
	 * Setters.
	 */
	public void setShopName(String nm){
		this.shopName = nm;
	}
	
	public void setShopLat(String lat){
		this.shopLat = lat;
	}
	
	public void setShopLong(String lon){
		this.shopLong = lon;
	}
	
	public void setShopId(String id){
		this.shopId = id;
	}
}
