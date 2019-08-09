package spring.json.api;


import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping(value = "/service", method = RequestMethod.POST)
public class ServiceController {


	@PostMapping("/serve")
	public String serve(HttpEntity<String> httpEntity) {


		String reqObject = httpEntity.getBody();
		System.out.println("request json object = "+reqObject);

		//Get the service

			JSONObject obj = new JSONObject(reqObject);
			String service = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Services");
			System.out.println(service);
			String response = "{\"fulfillmentText\": \"I will send you information about "+service+"\""+"}";


		return response;
	}
}
