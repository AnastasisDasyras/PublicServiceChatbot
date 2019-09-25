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
			String final_message = "{\"fulfillmentText\": \"Απάντηση\",\n" + 
				"    \"fulfillmentMessages\": [\n"; 
		
			for(int i=0;i<endpoint_response.length();i++) {
				JSONObject jsonObject2 = endpoint_response.getJSONObject(i);
				String value1 = jsonObject2.getJSONObject("PS_name").getString("value");
				System.out.println(value1);
				final_message = final_message + "      {\n" + 
						"        \"text\": {\n" + 
						"          \"text\": [\n" + 
						"            \""+value1+"\"\n" + 
						"          ]\n" + 
						"        }\n" + 
						"      },\n";
			}

		final_message = final_message + "    ]},";
			
		System.out.println(final_message);
		
		response = final_message;

		}
		else if(intent.equals("LE - Divorce")){
			String le_uri = "le0006";
			String divorse = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Divorce");
			JSONArray endpoint_response = getPSFromLE(le_uri);

			response = "{\"fulfillmentText\": \"Οι σχετικές με το διαζύγιο Παρεχόμενες Υπηρεσίες είναι: "+divorse+"\""+"}";
			System.out.println(divorse);
		}
		else if(intent.equals("LE - Lost Wallet")) {
			String le_uri = "le0002";
			String wallet = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Wallet");

			JSONArray endpoint_response = getPSFromLE(le_uri);

			response = "{\"fulfillmentText\": \"Οι σχετικές με την απώλεια πορτοφολιου σας Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";

			System.out.println(wallet);
		}
		else if(intent.equals("LE - School Life")) {
			String le_uri = "le0004";
			String schoolLife = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("SchoolLife");

			JSONArray endpoint_response = getPSFromLE(le_uri);

			response = "{\"fulfillmentText\": \"Οι σχετικές με την Σχολική Ζωή σας Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";

			System.out.println(schoolLife);
		}
		else if(intent.equals("LE - Travel")) {
			String le_uri = "le0005";
			String travel = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Travel");

			JSONArray endpoint_response = getPSFromLE(le_uri);

			response = "{\"fulfillmentText\": \"Οι σχετικές με Ταξίδι Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";

			System.out.println(travel);
		}
		else if(intent.equals("LE - Wedding")){
			String le_uri = "le0003";
			String wedding = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Wedding");

			JSONArray endpoint_response = getPSFromLE(le_uri);

			response = "{\"fulfillmentText\": \"Οι σχετικές με τον Γάμο Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";

			System.out.println(wedding);
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
			String text = getInputsFromPS("ps0004");
			//response = "{\"fulfillmentText\": \"Θελετέ τα σχετικά χαρτιά, το κόστος ή και τα δύο;\""+"}";
			response2 = "{\"fulfillmentText\": \"Τα δικαιολογητικά της ps0004 που θα χρειαστούν είναι: "+text+"\""+"}";
		}
		else if(!cost.isEmpty() && documents.isEmpty()){
			//method get cost
			String text = getCostFromPS("ps0004");
			//response = "{\"fulfillmentText\": \"Θελετέ τα σχετικά χαρτιά, το κόστος ή και τα δύο;\""+"}";
			//response = "{\"fulfillmentText\": \"Το κόστος είναι: "+text+"\""+"}";
			response2 = "{\"fulfillmentText\": \"Απάντηση\",\n" + 
					"    \"fulfillmentMessages\": [\n" + 
					"      {\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κόστος1\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"      {\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κοστοσ2\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"		{\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κόστος3\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"      {\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κοστοσ4\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"		{\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κόστος5\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"      {\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κοστοσ6\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"		{\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κόστος7\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"      {\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κοστοσ8\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"		{\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κόστος9\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"      {\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κοστοσ10\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"		{\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κόστος11\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      },\n" + 
					"      {\n" + 
					"        \"text\": {\n" + 
					"          \"text\": [\n" + 
					"            \"κοστοσ12\"\n" + 
					"          ]\n" + 
					"        }\n" + 
					"      }\n" + 
					"    ]},";

		}
		else {
			//method get cost and papers
		}


		//response = "{\"fulfillmentText\": \"Πληροφορίες σχετικά με "+intent+"\""+"}";
		byte[] enc = response.getBytes("UTF-8");

		//System.out.println(enc.toString());
		//Get service cost
		//ServiceResponse sr = new ServiceResponse(service);
		//String cost = sr.getCost();
		//System.out.println("cost: "+cost);
		return enc;

	}

	public static String getCostFromPS(String PS_URI){
		String s2 = "prefix cv: <http://data.europa.eu/m8g/>\n" +
				"select distinct ?PS_cost\n" +
				"where{\n" +
				"<http://data.dai.uom.gr:8890/PublicServices/id/ps/"+PS_URI+"> cv:hasCost ?PS_cost .\n" +
				"}\n" +
				"";



		Query query = QueryFactory.create(s2); //s2 = the query above
		QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://data.dai.uom.gr:8890/sparql", query );
		ResultSet results = qExe.execSelect();
		ResultSetFormatter.out(System.out, results, query);
		String text = ResultSetFormatter.asText(results);
		System.out.println(text);

		return text;


	}

	public static String getInputsFromPS(String PS_URI){
		String s2 = "prefix cpsv: <http://purl.org/vocab/cpsv#>\n" +
				"select distinct ?PS_input\n" +
				"where{\n" +
				"<http://data.dai.uom.gr:8890/PublicServices/id/ps/"+PS_URI+"> cpsv:hasInput ?PS_input .\n" +
				"}\n" +
				"";



		Query query = QueryFactory.create(s2); //s2 = the query above
		QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://data.dai.uom.gr:8890/sparql", query );
		ResultSet results = qExe.execSelect();
		String text = ResultSetFormatter.asText(results);
		//System.out.println(text);

		return text;


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
	
	


}
