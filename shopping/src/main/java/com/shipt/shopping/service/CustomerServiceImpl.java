package com.shipt.shopping.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shipt.shopping.constants.ShoppingConstants;
import com.shipt.shopping.dao.CustomerDAO;
import com.shipt.shopping.model.Customer;
import com.shipt.shopping.model.Order;
import com.shipt.shopping.model.Product;
import com.shipt.shopping.utils.ShoppingDateUtils;

@Service
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	CustomerDAO cuDAO;
	ProductService productServiceObj;

	@Autowired
	public CustomerServiceImpl(ProductService theproductServiceObj) {
		productServiceObj = theproductServiceObj;
	}

	//For getting all the customers and their details such as first name, last name and id
	public JSONObject getAllCustomerJSONObjects() throws JSONException{
		JSONObject retObj = new JSONObject();
		List<Customer> allcustomerObj = getAllCustomerObjects();
		JSONArray allCustomerIDs = new JSONArray();
		JSONObject allCustomerObjects = new JSONObject();
		for(Customer c:allcustomerObj) {
			allCustomerIDs.put(c.getCustomerId());
			JSONObject customerObj = new JSONObject();
			customerObj.put("customerId", c.getCustomerId());
			customerObj.put("firstName", c.getFirstName());
			customerObj.put("lastName", c.getLastName());
			allCustomerObjects.put(c.getCustomerId().toString(),customerObj);
		}
		retObj.put("allCustomerIDsArr", allCustomerIDs);
		retObj.put("allCustomerJObj", allCustomerObjects);
		retObj.put("status", HttpStatus.OK.value());
		return retObj;
	}

	//for getting a single customer's detail
	public JSONObject getCustomerJSONObj(long customerId) throws JSONException{
		JSONObject retObj = new JSONObject();
		Customer c = getACustomerObject(customerId);
		if(c != null) {
			JSONObject customerObj = new JSONObject();
			customerObj.put("customerId", c.getCustomerId());
			customerObj.put("firstName", c.getFirstName());
			customerObj.put("lastName", c.getLastName());
			retObj.put("customerJObj", customerObj);
			retObj.put("status", HttpStatus.OK.value());
		}
		else {
			retObj.put("message", "Customer with the given Id not found");
			retObj.put("status", HttpStatus.NOT_FOUND.value());
		}
		return retObj;
	}

	//Add a new customer to the database
	public JSONObject addNewCustomer(JSONObject customerJObj) throws JSONException{
		JSONObject retObj = new JSONObject();
		if(customerJObj != null && customerJObj.getString("firstName") != null && customerJObj.getString("lastName") != null) {
			String firstName = customerJObj.getString("firstName");
			String lastName = customerJObj.getString("lastName");
			Customer customer = new Customer();
			customer.setFirstName(firstName);
			customer.setLastName(lastName);
			Long newCustomerID = cuDAO.addANewCustomer(customer);
			if(newCustomerID != null) {
				retObj.put("newCustomerId", newCustomerID);
				retObj.put("status", HttpStatus.CREATED.value());
			}
			else {
				retObj.put("message", "Couldn't add the new customer");
				retObj.put("status", HttpStatus.BAD_GATEWAY.value());
			}
		}
		else {
			retObj.put("message", "Couldn't add the new customer due to invalid parameters");
			retObj.put("status", HttpStatus.BAD_REQUEST.value());
		}

		return retObj;
	}

	/* To add a new order for a customer.
	 * For an order to be valid:
	 * 1) Check if the customerId is of an existing customer.
	 * 2) Check if at least one productId matches existing available products. 
	 * By default, quantity of each product is assigned as 1 LB but can be modified in future.
	 * */
	public JSONObject addNewOrder(JSONObject orderJObj, long customerId) throws Exception{
		JSONObject retObj = new JSONObject();
		if(orderJObj == null || orderJObj.get("productIds") == null) { //invalid parameters
			retObj.put("message", "Invalid parameters");
			retObj.put("status", HttpStatus.BAD_REQUEST.value());
			return retObj;
		}
		List<Long> validProductIds = checkIfOrderIsValid(orderJObj,customerId);
		//valid order
		if(validProductIds != null && validProductIds.size() > 0) {
			Order order = new Order();
			order.setCustomerId(customerId);
			order.setOrderStatus(ShoppingConstants.orderStatusType.READY.getValue());
			long currentDateTime = ShoppingDateUtils.getCurrentDateTimeInMillis();
			order.setOrderDate(currentDateTime);
			Map<Long,Double> productsQuantity = new HashMap<>();
			for(long productId: validProductIds) {
				productsQuantity.put(productId, 1.0);
			}
			order.setProductsQuantity(productsQuantity);
			long newOrderId = cuDAO.addAOrder(order);
			retObj.put("orderId", newOrderId);
			retObj.put("productIds", validProductIds.toString());
			retObj.put("message", HttpStatus.CREATED.value());
		}
		else if(validProductIds == null){
			retObj.put("message", "Invalid CustomerId");
			retObj.put("status", HttpStatus.NOT_FOUND.value());
		}
		else {
			retObj.put("message", "No valid products found for the productIds");
			retObj.put("status", HttpStatus.OK.value());
		}
		return retObj;
	}


	/*By default orders will be sorted by date in descending order*/
	public JSONObject getAllOrdersJSONforaCustomer(long customerId) throws Exception{
		JSONObject retObj = new JSONObject();
		Customer c = getACustomerObject(customerId);
		if(c == null) {
			retObj.put("message", "Invalid customerId");
			retObj.put("status", HttpStatus.NOT_FOUND.value());
			return retObj;
		}
		Map<Long,Order> allOrdersMap = getAllOrdersforCustomer(customerId);
		if(allOrdersMap.size() > 0) {
			JSONArray allOrderIds = new JSONArray();
			JSONObject allOrderObjects = new JSONObject();
			for(Map.Entry<Long, Order> orderEntry : allOrdersMap.entrySet()) {
				Order order = orderEntry.getValue();
				JSONObject orderJSONObject = new JSONObject();
				allOrderIds.put(order.getOrderId());
				orderJSONObject.put("orderId", order.getOrderId());
				orderJSONObject.put("orderStatus", ShoppingConstants.orderStatusType.valueOf(order.getOrderStatus()));
				orderJSONObject.put("orderDate", ShoppingDateUtils.convertMillistoDateTimeString(order.getOrderDate()));
				List<Long> productIds = new ArrayList<Long>(order.getProductsQuantity().keySet());
				orderJSONObject.put("productIds", productIds);
				allOrderObjects.put(order.getOrderId().toString(), orderJSONObject);
			}
			retObj.put("allOrderJObj", allOrderObjects);
			retObj.put("allOrderIds", allOrderIds);
		}
		else {
			retObj.put("message", "No orders available for the given customer");
		}
		retObj.put("status", HttpStatus.OK.value());
		return retObj;
	}

	private List<Customer> getAllCustomerObjects() {
		List<Customer> customerObj = new ArrayList<>();
		List<Map<String,Object>> rows = cuDAO.getAllCustomers();
		for(Map<String,Object> row:rows) {
			Customer customer = new Customer();
			customer.setCustomerId((Long)row.get("CUSTOMER_ID"));
			customer.setFirstName((String)row.get("CUSTOMER_FIRSTNAME"));
			customer.setLastName((String)row.get("CUSTOMER_LASTNAME"));
			customerObj.add(customer);
		}
		return customerObj;
	}

	private Customer getACustomerObject(long customerId) {
		List<Map<String,Object>> rows = cuDAO.getCustomerDetail(customerId);
		Customer c = null;
		if(!rows.isEmpty()) {
			Map<String,Object> row= rows.get(0);
			c = new Customer();
			c.setCustomerId((Long)row.get("CUSTOMER_ID"));
			c.setFirstName((String)row.get("CUSTOMER_FIRSTNAME"));
			c.setLastName((String)row.get("CUSTOMER_LASTNAME"));
		}
		return c;	
	}

	private Map<Long,Order> getAllOrdersforCustomer(long customerId){
		Map<Long,Order> allOrdersList = new LinkedHashMap<>();
		List<Map<String,Object>> allOrderRows = cuDAO.getAllOrdersRowsforCustomer(customerId);
		if(!allOrderRows.isEmpty()) {
			for(Map<String,Object> row:allOrderRows) {
				long orderId = (Long)row.get("ORDER_ID");
				if(!allOrdersList.containsKey(orderId)) {
					Order orderObj = new Order();
					orderObj.setOrderId(orderId);
					int orderStatus = (Integer)row.get("ORDER_STATUS");
					orderObj.setOrderStatus(orderStatus);
					long orderDate = (Long)row.get("ORDER_DATE");
					orderObj.setOrderDate(orderDate);
					orderObj.setProductsQuantity(new HashMap<Long,Double>());
					allOrdersList.put(orderId, orderObj);
				}
				Map<Long,Double> productsQuantity = allOrdersList.get(orderId).getProductsQuantity();
				long productId = (Long)row.get("PRODUCT_ID");
				double quantityOrdered = ((BigDecimal)row.get("QUANTITYORDERED")).doubleValue();
				productsQuantity.put(productId, quantityOrdered);
			}
		}
		return allOrdersList;
	}

	/*  Method to check if the customerId and the productIds are valid to make an order or not.
	 * (ProductIds not currently available or invalid productIds will be removed and only valid productIds that exist will be considered). 
	 * 
	 *  Returns all the valid productIds if there is any and if the customerId is valid
	 * */

	private List<Long> checkIfOrderIsValid(JSONObject orderJObj, long customerId) throws Exception{
		Customer c = getACustomerObject(customerId);
		if(c == null) { //invalid customerId
			return null;
		}
		List<Long> validProductIds = new ArrayList<>();
		Map<Long,Product> productsMap = productServiceObj.getAllProductsMapwithIdsAsKey();
		JSONArray productIds = orderJObj.getJSONArray("productIds");
		for(int i=0;i<productIds.length();i++) {
			long productId = productIds.getLong(i);
			if(productsMap.containsKey(productId) && !validProductIds.contains(productId)) {
				validProductIds.add(productId);
			}
		}
		return validProductIds;
	}
}
