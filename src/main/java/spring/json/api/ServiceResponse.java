package spring.json.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServiceResponse {

	private String cost;
	private String documents;
	private String name;
	
	public ServiceResponse(String name) {
		this.name = name;
		this.cost = null;
		this.documents = null;
	}

	public String getCost() {
		
		cost = null;

		try {

			URL url = new URL("http://data.dai.uom.gr:8890/sparql?default-graph-uri=&query=prefix+dct%3A%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E+%0D%0Aprefix+cv%3A+%3Chttp%3A%2F%2Fdata.europa.eu%2Fm8g%2F%3E%0D%0A%0D%0Aselect+distinct+%3FPSname++%3Fvalue++%0D%0Awhere+%7B%3Fx+dct%3Atitle+%3FPSname.%0D%0A%3Fx+cv%3AhasCost+%3Fcost.%0D%0A%3Fcost+cv%3Avalue+%3Fvalue.%7D%0D%0AORDER+BY+%28%3FPSname%29&should-sponge=&format=text%2Fhtml&timeout=0&debug=on");
			//URL url = new URL("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=PREFIX+dbo%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2F%3E%0D%0APREFIX+dbp%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2F%3E%0D%0APREFIX+foaf%3A+%3Chttp%3A%2F%2Fxmlns.com%2Ffoaf%2F0.1%2F%3E%0D%0A%0D%0ASELECT+%3Fname+%3Fbandname+where+%7B%0D%0A+++%3Fperson+foaf%3Aname+%3Fname+.%0D%0A+++%3Fband+dbo%3AbandMember+%3Fperson+.%0D%0A+++%3Fband+dbo%3Agenre+dbp%3APunk_rock+.%0D%0A+++%3Fband+foaf%3Aname+%3Fbandname+.%0D%0A%7D&format=text%2Fhtml&CXML_redir_for_subjs=121&CXML_redir_for_hrefs=&timeout=30000&debug=on&run=+Run+Query+");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//conn.setDoOutput(true); // Triggers POST.
			//conn.setRequestProperty("Accept-Charset", charset);
			//conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}


		return cost;
	}




}
