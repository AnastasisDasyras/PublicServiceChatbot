package spring.json.api;


import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping(value = "/service", method = RequestMethod.POST)
public class ServiceController {


	@PostMapping("/serve")
	public byte[] serve(HttpServletRequest request) throws IOException {
		

		request.setCharacterEncoding("UTF8");
		
		
		String reqObject = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		System.out.println("request json object = "+reqObject);


		//Get the service
		JSONObject obj = new JSONObject(reqObject);
		//obj = obj.getJSONObject("queryResult").getJSONObject("parameters");
		String response = "null";
		String Life_event_house = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("House");
		
		if (!(Life_event_house.isEmpty() || Life_event_house== null)) {
			//String Life_event_house = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("House");
			response = "{\"fulfillmentText\": \"I will send you information about "+Life_event_house+"\""+"}";
		}
		else{
			response = "{\"fulfillmentText\": \"I did not find \""+"}";
		}
		
		//System.out.println(Life_event_house);
		
		byte[] enc = response.getBytes("UTF-8");
		
		
		//Get service cost
		//ServiceResponse sr = new ServiceResponse(service);
		//String cost = sr.getCost();
		//System.out.println("cost: "+cost);
		return enc;
		
	}
}
