package spring.json.api;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;


import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@RestController
@RequestMapping(value = "/service", method = RequestMethod.POST)
public class ServiceController {


	@PostMapping("/serve")
	public byte[] serve(HttpServletRequest request) throws IOException {


		request.setCharacterEncoding("UTF8");
		String response = "";


		String reqObject = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		//System.out.println("request json object = "+reqObject);

		//convert raw text to json object
		JSONObject obj = new JSONObject(reqObject);
		//Get the intention of user
		String intent = obj.getJSONObject("queryResult").getJSONObject("intent").getString("displayName");
		System.out.println(intent);
		//get PS from life event
		if(intent.equals("LE - Buy House")) {
			String le_uri = "le0001";
			String buyHouse = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("House");
			JSONArray endpoint_response = getPSFromLE(le_uri);
			String final_message = printRespo(endpoint_response);
			
			
			System.out.println(final_message);
		
			response = final_message;

		}
		else if(intent.equals("LE - Divorce")){
			String le_uri = "le0006";
			String divorse = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Divorce");
			JSONArray endpoint_response = getPSFromLE(le_uri);
			
			String final_message = printRespo(endpoint_response);
			System.out.println(final_message);
			
			response = final_message;
			
		}
		else if(intent.equals("LE - Lost Wallet")) {
			String le_uri = "le0002";
			String wallet = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Wallet");

			JSONArray endpoint_response = getPSFromLE(le_uri);

			String final_message = printRespo(endpoint_response);
			System.out.println(final_message);
			
			response = final_message;
		}
		else if(intent.equals("LE - School Life")) {
			String le_uri = "le0004";
			String schoolLife = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("SchoolLife");

			JSONArray endpoint_response = getPSFromLE(le_uri);

			String final_message = printRespo(endpoint_response);
			System.out.println(final_message);
			
			response = final_message;
		}
		else if(intent.equals("LE - Travel")) {
			String le_uri = "le0005";
			String travel = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Travel");

			JSONArray endpoint_response = getPSFromLE(le_uri);

			String final_message = printRespo(endpoint_response);
			System.out.println(final_message);
			
			response = final_message;
		}
		else if(intent.equals("LE - Wedding")){
			String le_uri = "le0003";
			String wedding = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Wedding");

			JSONArray endpoint_response = getPSFromLE(le_uri);

			String final_message = printRespo(endpoint_response);
			System.out.println(final_message);
			
			response = final_message;
		}
		else {
			//user send the name of a public service

		}


		//Find out what user need papers or cost
		String documents = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Documents");
		String cost = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Cost");
		String response2 = "";

		if(cost.isEmpty() && documents.isEmpty()) {
			response2 = "{\"fulfillmentText\": \"Θελετέ τα σχετικά χαρτιά, το κόστος ή και τα δύο;\""+"}";
		}
		else if(cost.isEmpty() && !documents.isEmpty()) {
			JSONArray ps_uri = obj.getJSONObject("queryResult").getJSONArray("outputContexts");
			System.out.println(ps_uri.get(0));
			JSONObject jsonObject2 = (JSONObject) ps_uri.get(0);
			JSONObject jsonObject3 = jsonObject2.getJSONObject("parameters");
			String ps_uri_string = jsonObject3.getString("PublicService");
			System.out.println(ps_uri_string);
			
			JSONArray endpoint_response = getInputsFromPS(ps_uri_string);
			
			String cost_value = "Δεν υπάρχει τιμή";
			if (!endpoint_response.isEmpty()) {
				jsonObject2 = endpoint_response.getJSONObject(0);
				cost_value = jsonObject2.getJSONObject("PS_input").getString("value");
			}
			
			
			String final_message = "{\"fulfillmentText\": \"Τα δικαιολογητικά είναι: "+cost_value+"\""+"}";
			//response = "{\"fulfillmentText\": \"Ξ�ΞµΞ»ΞµΟ„Ξ­ Ο„Ξ± ΟƒΟ‡ΞµΟ„ΞΉΞΊΞ¬ Ο‡Ξ±Ο�Ο„ΞΉΞ¬, Ο„ΞΏ ΞΊΟ�ΟƒΟ„ΞΏΟ‚ Ξ® ΞΊΞ±ΞΉ Ο„Ξ± Ξ΄Ο�ΞΏ;\""+"}";
			//response = "{\"fulfillmentText\": \"Ξ¤ΞΏ ΞΊΟ�ΟƒΟ„ΞΏΟ‚ ΞµΞ―Ξ½Ξ±ΞΉ: "+text+"\""+"}";
			
			response = final_message;
		}
		else if(!cost.isEmpty() && documents.isEmpty()){
			//method get cost
			JSONArray ps_uri = obj.getJSONObject("queryResult").getJSONArray("outputContexts");
			System.out.println(ps_uri.get(0));
			JSONObject jsonObject2 = (JSONObject) ps_uri.get(0);
			JSONObject jsonObject3 = jsonObject2.getJSONObject("parameters");
			String ps_uri_string = jsonObject3.getString("PublicService");
			System.out.println(ps_uri_string);
			//String ps_uri_string = (String) ps_uri_arr.get(0);
			//String ps_uri = obj.getString("queryResult");
			//System.out.println(ps_uri);
			//String ps_uri = "ps0157";
			JSONArray endpoint_response = getCostFromPS(ps_uri_string);
			//only one cost value so index = 0
			String cost_value = "Δεν υπάρχει τιμή";
			if (!endpoint_response.isEmpty()) {
				jsonObject2 = endpoint_response.getJSONObject(0);
				cost_value = jsonObject2.getJSONObject("Our_value").getString("value");
			}
			
			
			String final_message = "{\"fulfillmentText\": \"Το κόστος είναι: "+cost_value+"\""+"}";
			//response = "{\"fulfillmentText\": \"Ξ�ΞµΞ»ΞµΟ„Ξ­ Ο„Ξ± ΟƒΟ‡ΞµΟ„ΞΉΞΊΞ¬ Ο‡Ξ±Ο�Ο„ΞΉΞ¬, Ο„ΞΏ ΞΊΟ�ΟƒΟ„ΞΏΟ‚ Ξ® ΞΊΞ±ΞΉ Ο„Ξ± Ξ΄Ο�ΞΏ;\""+"}";
			//response = "{\"fulfillmentText\": \"Ξ¤ΞΏ ΞΊΟ�ΟƒΟ„ΞΏΟ‚ ΞµΞ―Ξ½Ξ±ΞΉ: "+text+"\""+"}";
			
			response = final_message;
			
		}
		else {
			//method get cost and papers
		}


		//response = "{\"fulfillmentText\": \"Ξ Ξ»Ξ·Ο�ΞΏΟ†ΞΏΟ�Ξ―ΞµΟ‚ ΟƒΟ‡ΞµΟ„ΞΉΞΊΞ¬ ΞΌΞµ "+intent+"\""+"}";
		byte[] enc = response.getBytes("UTF-8");

		//System.out.println(enc.toString());
		//Get service cost
		//ServiceResponse sr = new ServiceResponse(service);
		//String cost = sr.getCost();
		//System.out.println("cost: "+cost);
		return enc;

	}

	public static JSONArray getCostFromPS(String PS_URI){
		String s2 = "prefix cv: <http://data.europa.eu/m8g/>\n" +
				"select distinct ?Our_value\n" +
				"where{\n" +
				"GRAPH <http://data.dai.uom.gr:8890/CPSV-Chatbot>{\n" +
				"<http://data.dai.uom.gr:8890/PublicServices/id/ps/"+PS_URI+"> cv:hasCost ?PS_cost .\n" +
				"?PS_cost cv:value ?Our_value .\n" +
				"}}\n" +
				"";



		Query query = QueryFactory.create(s2); //s2 = the query above
		QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://data.dai.uom.gr:8890/sparql", query );
		ResultSet results = qExe.execSelect();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ResultSetFormatter.outputAsJSON(outputStream, results);

		// and turn that into a String
		String json_string = new String(outputStream.toByteArray());

		//System.out.println(json_string);
		
		JSONObject jsonObject = new JSONObject(json_string);
		JSONArray arr = jsonObject.getJSONObject("results").getJSONArray("bindings");
		System.out.println(arr);

		return arr;



	}

	public static JSONArray getInputsFromPS(String PS_URI){
		String s2 = "prefix cpsv: <http://purl.org/vocab/cpsv#>\n" +
				"select distinct ?PS_input\n" +
				"where{\n" +
				"<http://data.dai.uom.gr:8890/PublicServices/id/ps/"+PS_URI+"> cpsv:hasInput ?PS_input .\n" +
				"}\n" +
				"";



		Query query = QueryFactory.create(s2); //s2 = the query above
		QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://data.dai.uom.gr:8890/sparql", query );
		ResultSet results = qExe.execSelect();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ResultSetFormatter.outputAsJSON(outputStream, results);
		
		// and turn that into a String
		String json_string = new String(outputStream.toByteArray());

		JSONObject jsonObject = new JSONObject(json_string);
		JSONArray arr = jsonObject.getJSONObject("results").getJSONArray("bindings");
		System.out.println(arr);

		return arr;
		


	}

	public static JSONArray getPSFromLE(String LE_URI){
		String s2 = "prefix cv: <http://data.europa.eu/m8g/>\n" +
				"prefix cpsv: <http://purl.org/vocab/cpsv#>\n" +
				"prefix dct: <http://purl.org/dc/terms/>\n" +
				"select distinct ?PS_name\n" +
				"where{\n" +
				"?PS_URI a cpsv:PublicService .\n" +
				"?PS_URI cv:isGroupedBy <http://data.dai.uom.gr:8890/PublicServices/id/le/"+LE_URI+"> .\n" +
				"?PS_URI dct:title ?PS_name .\n" +
				"}\n" +
				"order by(?PS_URI)\n" +
				"";



		Query query = QueryFactory.create(s2); //s2 = the query above
		QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://data.dai.uom.gr:8890/sparql", query );
		ResultSet results = qExe.execSelect();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ResultSetFormatter.outputAsJSON(outputStream, results);
		
		// and turn that into a String
		String json_string = new String(outputStream.toByteArray());

		JSONObject jsonObject = new JSONObject(json_string);
		JSONArray arr = jsonObject.getJSONObject("results").getJSONArray("bindings");
		System.out.println(arr);

		return arr;

	}
	
	
	public static String printRespo(JSONArray endpoint_response){
		
		String temp = "{\"fulfillmentText\": \"Οι σχετικές δημόσιες υπηρεσίες είναι (Επιλέξτε γράφοντας τον τίτλο): ";
		
		for(int i=0;i<endpoint_response.length();i++) {
			JSONObject jsonObject2 = endpoint_response.getJSONObject(i);
			String value1 = jsonObject2.getJSONObject("PS_name").getString("value");
		
			System.out.println(value1);
			
			if (i<endpoint_response.length()-1){
				temp = temp +"▐ "+value1+" ▐ ";
				
				
			}
			else{
				temp = temp + value1+"\"}";
			}
						
		}
		
		
		return temp;
	}
	
	


}
