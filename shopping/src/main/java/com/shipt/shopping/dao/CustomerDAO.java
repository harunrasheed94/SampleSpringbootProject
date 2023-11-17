package com.shipt.shopping.dao;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.shipt.shopping.model.Customer;
import com.shipt.shopping.model.Order;

public interface CustomerDAO {
	public List<Map<String,Object>> getAllCustomers();
	public List<Map<String, Object>> getCustomerDetail(long customerID);
	public List<Map<String, Object>> getAllOrdersRowsforCustomer(long customerID);
	public Long addANewCustomer(Customer customer);
	public Long addAOrder(Order order);
}
