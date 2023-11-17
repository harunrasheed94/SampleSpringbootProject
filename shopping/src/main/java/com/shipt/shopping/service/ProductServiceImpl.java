package com.shipt.shopping.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shipt.shopping.constants.ShoppingConstants;
import com.shipt.shopping.dao.ProductDAO;
import com.shipt.shopping.model.Product;
import com.shipt.shopping.utils.ShoppingDateUtils;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	ProductDAO prDAO;

	//Get all the products with their ids, name and base price per LB.
	public JSONObject getAllProductJSONObjects() throws JSONException{
		JSONObject retObj = new JSONObject();
		List<Product> productObjList = getAllProductObjects();
		JSONArray allProductIDs = new JSONArray();
		JSONObject allProductJObj = new JSONObject();

		for(Product product:productObjList) {
			allProductIDs.put(product.getProductId());
			JSONObject productObj = new JSONObject();
			productObj.put("productId", product.getProductId());
			productObj.put("productName", product.getName());
			productObj.put("basePricePerLB", product.getBasePrice());
			allProductJObj.put(product.getProductId().toString(), productObj);
		}
		retObj.put("allProductIdsArr", allProductIDs);
		retObj.put("allProductJObj", allProductJObj);
		retObj.put("status", HttpStatus.OK.value());
		return retObj;
	}

	//get all existing products as a map with the product id as the key and the product object as the value
	public Map<Long,Product> getAllProductsMapwithIdsAsKey() throws Exception{
		Map<Long,Product> productObjMap = new HashMap<>();
		List<Product> productObjList = getAllProductObjects();
		for(Product product:productObjList) {
			productObjMap.put(product.getProductId(), product);
		}
		return productObjMap;
	}

	/*get all the ordered products within the date range
	 * 
	 * Before fetching the orders, request's parameters has to be validated 
	 * 1) Date range is valid
	 * 2) group by option is valid (i.e 1,2 or 3 only for day, week and month respectively). 
	 */
	public JSONObject getOrderedProducts(String startDate, String endDate, int groupBy) throws Exception {
		JSONObject retObj = new JSONObject();
		if(startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0 || ShoppingConstants.productGroupType.valueOf(groupBy) == null) {
			retObj.put("message", "Invalid paramaters");
			retObj.put("status", HttpStatus.BAD_REQUEST.value());
			return retObj;
		}

		if(!ShoppingDateUtils.checkIfDateRangeisvalid(startDate, endDate)) {
			retObj.put("message", "Start Date should be before the end Date");
			retObj.put("status", HttpStatus.OK.value());
			return retObj;
		}

		long startDateMS = ShoppingDateUtils.convertDatetoMillis(startDate);
		long endDateMS = ShoppingDateUtils.convertDatetoMillis(endDate);
		List<Map<String,Object>> rows = prDAO.getSoldProductsRow(startDateMS, endDateMS);
		if(rows.isEmpty()) {
			retObj.put("message", "No products were sold on the given date range");

		}
		else {
			if(groupBy == ShoppingConstants.productGroupType.DAY.getValue()) {
				retObj = groupProductsOrderedByDay(rows);
			}
			else if(groupBy == ShoppingConstants.productGroupType.WEEK.getValue()) {
				retObj = groupProductsOrderedByWeek(rows,startDateMS);
			}
			else {
				retObj = groupProductsOrderedByMonth(rows,startDateMS);
			}
		}
		retObj.put("status", HttpStatus.OK.value());
		return retObj;	
	}

	private List<Product> getAllProductObjects() {
		List<Product> productObjList = new ArrayList<>();
		List<Map<String,Object>> rows = prDAO.getAllProductsRows();
		for(Map<String,Object> row:rows) {
			Product product = new Product();
			product.setName((String)row.get("NAME"));
			product.setProductId((Long)row.get("PRODUCT_ID"));
			BigDecimal basePrice = (BigDecimal)row.get("BASE_PRICE");
			product.setBasePrice(basePrice.doubleValue());
			productObjList.add(product);
		}
		return productObjList;
	}

	private Map<Long,Product> getSpecificProductObjects(List<Long> productIds){
		Map<Long,Product> productMap = new HashMap<>();
		List<Map<String,Object>> rows = prDAO.getSpecificProducts(productIds);
		for(Map<String,Object> row:rows) {
			Product product = new Product();
			product.setName((String)row.get("NAME"));
			long productId = (Long)row.get("PRODUCT_ID");
			product.setProductId(productId);
			BigDecimal basePrice = (BigDecimal)row.get("BASE_PRICE");
			product.setBasePrice(basePrice.doubleValue());
			productMap.put(productId,product);
		}
		return productMap;
	}

	private JSONObject groupProductsOrderedByDay(List<Map<String,Object>> rows) throws Exception {
		JSONObject retObj = new JSONObject();
		Map<Long,Map<String,Double>> productsSoldbyDay = new HashMap<>();
		List<Long> productIdsList = new ArrayList<>();

		for(Map<String,Object> row:rows) {
			long productId = (long)row.get("PRODUCT_ID");
			if(!productsSoldbyDay.containsKey(productId)) {
				productsSoldbyDay.put(productId, new HashMap<String,Double>());
				productIdsList.add(productId);
			}
			String date = ShoppingDateUtils.convertMillistoDateString((long)row.get("ORDER_DATE"));
			Double quantity = ((BigDecimal)row.get("TOTALQUANTITYORDERED")).doubleValue();
			Map<String,Double> quantityByDay = productsSoldbyDay.get(productId);
			double current = quantityByDay.getOrDefault(date,0.0);
			quantityByDay.put(date, current + quantity);
		}
		Map<Long,Product> productMap = getSpecificProductObjects(productIdsList);
		JSONArray productIds = new JSONArray();
		JSONObject productJObj = new JSONObject();
		for(Map.Entry<Long, Map<String,Double>> product : productsSoldbyDay.entrySet()) {
			long productId = product.getKey();
			productIds.put(productId);
			Map<String,Double> quantityByDay = product.getValue();
			JSONArray dateArr = new JSONArray();

			for(Map.Entry<String, Double> entry:quantityByDay.entrySet()) {
				String date = entry.getKey();
				Double quantityOrdered = entry.getValue();
				String productName = productMap.get(productId).getName();
				JSONObject productQtyJObj = new JSONObject();
				productQtyJObj.put("date", date);
				productQtyJObj.put("quantity", quantityOrdered);
				productQtyJObj.put("productName", productName);
				dateArr.put(productQtyJObj);
			}
			productJObj.put(""+productId, dateArr);
		}
		retObj.put("allProductIds", productIds);
		retObj.put("productQuantityJObj", productJObj);
		return retObj;
	}

	private JSONObject groupProductsOrderedByWeek(List<Map<String,Object>> rows, long startDate) throws Exception {
		JSONObject retObj = new JSONObject();
		long startingWeek = ShoppingDateUtils.getWeekFromEpoch(startDate);
		Map<Long,Map<Long,Double>> productsSoldbyWeek = new HashMap<>();
		List<Long> productIdsList = new ArrayList<>();

		for(Map<String,Object> row:rows) {
			long productId = (long)row.get("PRODUCT_ID");
			if(!productsSoldbyWeek.containsKey(productId)) {
				productsSoldbyWeek.put(productId, new HashMap<Long,Double>());
				productIdsList.add(productId);
			}
			long orderedDate = (long)row.get("ORDER_DATE");
			long weekNumber = ShoppingDateUtils.getWeekFromEpoch(orderedDate) - startingWeek + 1;
			Double quantity = ((BigDecimal)row.get("TOTALQUANTITYORDERED")).doubleValue();
			Map<Long,Double> quantityByWeek = productsSoldbyWeek.get(productId);
			double current = quantityByWeek.getOrDefault(weekNumber,0.0);
			quantityByWeek.put(weekNumber, current + quantity);
		}
		Map<Long,Product> productMap = getSpecificProductObjects(productIdsList);
		JSONArray productIds = new JSONArray();
		JSONObject productJObj = new JSONObject();
		for(Map.Entry<Long, Map<Long,Double>> product : productsSoldbyWeek.entrySet()) {
			long productId = product.getKey();
			productIds.put(productId);
			Map<Long,Double> quantityByWeek = product.getValue();
			JSONArray weekArr = new JSONArray();

			for(Map.Entry<Long, Double> entry:quantityByWeek.entrySet()) {
				long week = entry.getKey();
				Double quantityOrdered = entry.getValue();
				String productName = productMap.get(productId).getName();
				JSONObject productQtyJObj = new JSONObject();
				productQtyJObj.put("weekNumber", week);
				productQtyJObj.put("quantity", quantityOrdered);
				productQtyJObj.put("productName", productName);
				weekArr.put(productQtyJObj);
			}
			productJObj.put(""+productId, weekArr);
		}
		retObj.put("allProductIds", productIds);
		retObj.put("productQuantityJObj", productJObj);
		return retObj;
	}

	private JSONObject groupProductsOrderedByMonth(List<Map<String,Object>> rows, long startDate) throws Exception{
		JSONObject retObj = new JSONObject();
		long startingMonth = ShoppingDateUtils.getMonthFromEpoch(startDate);
		Map<Long,Map<Long,Double>> productsSoldbyMonth = new HashMap<>();
		List<Long> productIdsList = new ArrayList<>();

		for(Map<String,Object> row:rows) {
			long productId = (long)row.get("PRODUCT_ID");
			if(!productsSoldbyMonth.containsKey(productId)) {
				productsSoldbyMonth.put(productId, new HashMap<Long,Double>());
				productIdsList.add(productId);
			}
			long orderedDate = (long)row.get("ORDER_DATE");
			long monthNumber = ShoppingDateUtils.getMonthFromEpoch(orderedDate) - startingMonth + 1;
			Double quantity = ((BigDecimal)row.get("TOTALQUANTITYORDERED")).doubleValue();
			Map<Long,Double> quantityByMonth = productsSoldbyMonth.get(productId);
			double current = quantityByMonth.getOrDefault(monthNumber,0.0);
			quantityByMonth.put(monthNumber, current + quantity);
		}
		Map<Long,Product> productMap = getSpecificProductObjects(productIdsList);
		JSONArray productIds = new JSONArray();
		JSONObject productJObj = new JSONObject();
		for(Map.Entry<Long, Map<Long,Double>> product : productsSoldbyMonth.entrySet()) {
			long productId = product.getKey();
			productIds.put(productId);
			Map<Long,Double> quantityByMonth = product.getValue();
			JSONArray monthArr = new JSONArray();

			for(Map.Entry<Long, Double> entry:quantityByMonth.entrySet()) {
				long month = entry.getKey();
				Double quantityOrdered = entry.getValue();
				JSONObject productQtyJObj = new JSONObject();
				String productName = productMap.get(productId).getName();
				productQtyJObj.put("monthNumber", month);
				productQtyJObj.put("quantity", quantityOrdered);
				productQtyJObj.put("productName", productName);
				monthArr.put(productQtyJObj);
			}
			productJObj.put(""+productId, monthArr);
		}
		retObj.put("allProductIds", productIds);
		retObj.put("productQuantityJObj", productJObj);
		return retObj;
	}
}
