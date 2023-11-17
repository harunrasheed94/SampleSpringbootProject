package com.shipt.shopping.model;

import java.util.List;

public class Product {
	Long productId = null;
	String name = "";
	Double basePrice = null;
	List<Long> categoryIds = null;
	
	public Long getProductId() {
		return productId;
	}
	public List<Long> getCategoryIds() {
		return categoryIds;
	}
	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}
}
