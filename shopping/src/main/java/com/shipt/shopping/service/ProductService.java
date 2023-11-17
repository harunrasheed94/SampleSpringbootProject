package com.shipt.shopping.service;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.shipt.shopping.model.Product;

public interface ProductService {
	public JSONObject getAllProductJSONObjects() throws JSONException;
	public Map<Long,Product> getAllProductsMapwithIdsAsKey() throws Exception;
	public JSONObject getOrderedProducts(String startDate, String endDate, int groupBy) throws Exception;
}
