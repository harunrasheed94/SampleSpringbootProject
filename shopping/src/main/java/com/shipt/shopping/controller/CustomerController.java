package com.shipt.shopping.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.shipt.shopping.service.CustomerService;

@RestController
public class CustomerController {
	private CustomerService customerServiceObj;

	@Autowired
	public CustomerController(CustomerService theCustomerService){
		customerServiceObj = theCustomerService;
	}

	//For getting all the customers and their details
	@RequestMapping(value = "/customer", method= RequestMethod.GET)
	public String getAllCustomers() throws JSONException {
		JSONObject retObj = customerServiceObj.getAllCustomerJSONObjects();
		return retObj.toString();
	}

	//For getting a detail of a customer by giving his id
	@RequestMapping(value = "/customer/{customerId}", method = RequestMethod.GET)
	public String getACustomerDetail(@PathVariable long customerId) throws JSONException {
		JSONObject retObj = customerServiceObj.getCustomerJSONObj(customerId);
		return retObj.toString();
	}
	
	//For getting all the orders of a customer sorted by date in descending order
	@RequestMapping(value = "/customer/{customerId}/order", method = RequestMethod.GET)
	public String getAllOrdersForCustomer(@PathVariable long customerId) throws Exception {
		JSONObject retObj = customerServiceObj.getAllOrdersJSONforaCustomer(customerId);
		return retObj.toString();
	}

	//For adding a new customer
	@RequestMapping(value = "/customer", method = RequestMethod.POST)
	public String addNewCustomer(@RequestBody String requestBody) throws JSONException{
		JSONObject retObj = customerServiceObj.addNewCustomer(new JSONObject(requestBody));
		return retObj.toString();
	}
	
	//For adding a new order for a customer
	@RequestMapping(value = "/customer/{customerId}/order", method = RequestMethod.POST)
	public String addNewOrder(@RequestBody String requestBody, @PathVariable long customerId) throws Exception{
		JSONObject retObj = customerServiceObj.addNewOrder(new JSONObject(requestBody), customerId);
		return retObj.toString();
	}

}
