package com.htmsd.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import com.htmsd.slayer.NoSuchCurrencyException;
import com.htmsd.slayer.model.Currency;
import com.htmsd.slayer.service.CurrencyLocalServiceUtil;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.util.Validator;

public class UpdateXRates extends BaseMessageListener {
	
	private final static Log _log = LogFactoryUtil.getLog(UpdateXRates.class.getName());
	
	@Override
	protected void doReceive(Message arg) throws Exception {
		update();
	}

	private void update() {
						
		String url = "http://api.fixer.io/latest?base=INR";
	
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		
		try {
			client.executeMethod(method);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream inputStream = null;
		try {
			inputStream = method.getResponseBodyAsStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String jsonResponse = null;
		try {
			jsonResponse = IOUtils.toString(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONFactoryUtil.createJSONObject(jsonResponse);
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
		Iterator<String> itr = jsonObject.getJSONObject("rates").keys();
						
		while (itr.hasNext()) {
			
			String currencyCode = itr.next();
		
			double xrate = jsonObject.getJSONObject("rates").getDouble(currencyCode);
			_log.info("Updating currency "+currencyCode+": "+xrate);						
			Currency currency = null;
			try {
				currency = CurrencyLocalServiceUtil.getCurrency(currencyCode);
			} catch (NoSuchCurrencyException e) {
				_log.error("No Such Curency");
			} catch (SystemException e) {
				_log.error(e);
			}
			
			if (Validator.isNull(currency)) {
				long currencyId = 0;
				try {
					currencyId = CounterLocalServiceUtil.increment();
				} catch (SystemException e) {
					_log.error(e);
				}
				currency = CurrencyLocalServiceUtil.createCurrency(currencyId);
				currency.setCurrencyCode(currencyCode);	
				try {
					currency = CurrencyLocalServiceUtil.addCurrency(currency);
				} catch (SystemException e) {
					_log.error(e);
				}
			}
			
			if (xrate == currency.getConversion()) continue;
			
			currency.setConversion(1/xrate);
			
			try {
				CurrencyLocalServiceUtil.updateCurrency(currency);
			} catch (SystemException e) {
				_log.error(e);
			}
		}
	}
}