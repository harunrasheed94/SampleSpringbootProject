package com.shipt.shopping.model;

import java.util.Map;

public class Order {
	Long orderId = null, customerId = null;
	Integer orderStatus = null;
	Long orderDate = null;
	Map<Long,Double> productsQuantity = null;
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Map<Long, Double> getProductsQuantity() {
		return productsQuantity;
	}
	public void setProductsQuantity(Map<Long, Double> productsQuantity) {
		this.productsQuantity = productsQuantity;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Long getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Long orderDate) {
		this.orderDate = orderDate;
	}
}
