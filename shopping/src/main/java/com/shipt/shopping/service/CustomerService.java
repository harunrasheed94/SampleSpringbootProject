package com.shipt.shopping.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.shipt.shopping.model.Customer;

public interface CustomerService  {
	public JSONObject getAllCustomerJSONObjects() throws JSONException;
	public JSONObject getCustomerJSONObj(long customerId) throws JSONException;
	public JSONObject getAllOrdersJSONforaCustomer(long customerId) throws Exception;
	public JSONObject addNewCustomer(JSONObject customerJObj) throws JSONException;
	public JSONObject addNewOrder(JSONObject orderJObj, long customerId) throws Exception;
	
}
