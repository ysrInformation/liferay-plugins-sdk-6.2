package com.htmsd.checkout;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class CheckoutPortlet
 */
public class CheckoutPortlet extends MVCPortlet {
	
	public void step1(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
	
		System.out.println("Checkout :- Step 1");
		actionResponse.setRenderParameter("order_step", "step2"); 
	}
	
	public void step2(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
	
		System.out.println("Checkout :- Step 2");
		actionResponse.setRenderParameter("order_step", "step3");
	}
	
	public void step3(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		System.out.println("Checkout :- Step 3");
		actionResponse.setRenderParameter("order_step", "step4");
	}
	
	public void step4(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		System.out.println("Checkout :- Step 4");
		actionResponse.setRenderParameter("order_step", "step5");
	}
	
	public void step5(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		System.out.println("Checkout :- Step 5");
	}
 

}
