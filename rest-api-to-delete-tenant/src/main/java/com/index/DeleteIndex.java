package com.index;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class DeleteIndex {
	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {
		
		DeleteIndex http = new DeleteIndex();
		http.sendGet();

		
		
		
/*
		InetAddress host = InetAddress.getByName("192.168.125.109");
		int port=9200;
		Settings settings = Settings.builder()
		    .put("cluster.name", "elasticsearch").
		    put("xpack.security.user","elastic:XXXXX")
		            .build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.125.109"),9200));
		//.addTransportAddress(new InetSocketTransportAddress(host,port));
		        
		GetResponse response = client.prepareGet("tenantsdetdwiwrt_data_20180606_1000_archive.av4", "idoc", "U3bTm8kmhnNI2hQDPDjYJbsLX1w=").get();
		System.out.println(response);*/
	
	}
	
	
	
	
	private void sendGet() throws Exception {
		String str="(http:\\/\\/|https:\\/\\/)?192.168.125.109:9200([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?";
		String url = "http://192.168.125.109:9200/tenantsdetdwiwrt_data_20180606_1000_archive.av4";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
	}


}
