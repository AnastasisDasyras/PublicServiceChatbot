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
			String buyHouse = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("House");
			//System.out.println(buyHouse);
			
		}
		else if(intent.equals("LE - Divorce")){
			String divorse = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Divorce");
			String endpoint_response = getPSFromLE(divorse);
			
			response = "{\"fulfillmentText\": \"Οι σχετικές με το διαζύγιο Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";
			System.out.println(divorse);
		}
		else if(intent.equals("LE - Lost Wallet")) {
			String wallet = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Wallet");
			
			String endpoint_response = getPSFromLE(wallet);
			
			response = "{\"fulfillmentText\": \"Οι σχετικές με την απώλεια πορτοφολιου σας Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";
			
			System.out.println(wallet);
		}
		else if(intent.equals("LE - School Life")) {
			String schoolLife = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("SchoolLife");
			
			String endpoint_response = getPSFromLE(schoolLife);
			
			response = "{\"fulfillmentText\": \"Οι σχετικές με την Σχολική Ζωή σας Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";
			
			System.out.println(schoolLife);
		}
		else if(intent.equals("LE - Travel")) {
			String travel = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Travel");
			
			String endpoint_response = getPSFromLE(travel);
			
			response = "{\"fulfillmentText\": \"Οι σχετικές με Ταξίδι Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";
			
			System.out.println(travel);
		}
		else if(intent.equals("LE - Wedding")){
			String wedding = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Wedding");
			
			String endpoint_response = getPSFromLE(wedding);
			
			response = "{\"fulfillmentText\": \"Οι σχετικές με τον Γάμο Παρεχόμενες Υπηρεσίες είναι: "+endpoint_response+"\""+"}";
			
			System.out.println(wedding);
		}
		else {
			//user send the name of a public service
			
		}

		
		//Find out what user need papers or cost
		String documents = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Documents");
		String cost = obj.getJSONObject("queryResult").getJSONObject("parameters").getString("Cost");
		
		if(cost.isEmpty() && documents.isEmpty()) {
			response = "{\"fulfillmentText\": \"Θελετέ τα σχετικά χαρτιά, το κόστος ή και τα δύο;\""+"}";
		}
		else if(cost.isEmpty() && !documents.isEmpty()) {
			String text = getInputsFromPS("ps0004");
			//response = "{\"fulfillmentText\": \"Θελετέ τα σχετικά χαρτιά, το κόστος ή και τα δύο;\""+"}";
			response = "{\"fulfillmentText\": \"Τα δικαιολογητικά της ps0004 που θα χρειαστούν είναι: "+text+"\""+"}";
		}
		else if(!cost.isEmpty() && documents.isEmpty()){
			//method get cost
			String text = getCostFromPS("ps0004");
			//response = "{\"fulfillmentText\": \"Θελετέ τα σχετικά χαρτιά, το κόστος ή και τα δύο;\""+"}";
			response = "{\"fulfillmentText\": \"Το κόστος είναι: "+text+"\""+"}";
			
		}
		else {
			//method get cost and papers
		}
		
		
		//response = "{\"fulfillmentText\": \"Πληροφορίες σχετικά με "+intent+"\""+"}";
		byte[] enc = response.getBytes("UTF-8");
		
		
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
        
        return results.toString();


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
        ResultSetFormatter.out(System.out, results, query);
        
        return results.toString();


    	}

	public static String getPSFromLE(String LE_URI){
        String s2 = "prefix cv: <http://data.europa.eu/m8g/>\n" +
		    "prefix cpsv: <http://purl.org/vocab/cpsv#>\n" +
                    "select distinct ?PS_URI\n" +
                    "where{\n" +
                    "?PS_URI a cpsv:PublicService .\n" +
		    "?PS_URI cv:isGroupedBy <http://data.dai.uom.gr:8890/PublicServices/id/ps/"+LE_URI+"> .\n" +
	            "}\n" +
		    "order by(?PS_URI)\n" +
	            "";
	

		
        Query query = QueryFactory.create(s2); //s2 = the query above
        QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://data.dai.uom.gr:8890/sparql", query );
        ResultSet results = qExe.execSelect();
        ResultSetFormatter.out(System.out, results, query);
        
        return results.toString();


    	}
	
	
}
