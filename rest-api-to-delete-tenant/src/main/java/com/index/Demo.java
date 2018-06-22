package com.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Demo {
//curl -X GET "localhost:9200/tenantsdetdwiwrt_data_20180606_1000_archive.av4/idoc/U3bTm8kmhnNI2hQDPDjYJbsLX1w="
	public static void main(String[] args) {
		
		 String username="myusername";
		    String password="mypassword";
		    String url="http://192.168.125.109:9200/tenantsdetdwiwrt_data_20180606_1000_archive.av4/idoc/U3bTm8kmhnNI2hQDPDjYJbsLX1w=";
		       String[] command = {"wGet", "X" ,"GET",url};
		        ProcessBuilder process = new ProcessBuilder(command); 
		        Process p;
		        try
		        {
		            p = process.start();
		             BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
		                StringBuilder builder = new StringBuilder();
		                String line = null;
		                while ( (line = reader.readLine()) != null) {
		                        builder.append(line);
		                        builder.append(System.getProperty("line.separator"));
		                }
		                String result = builder.toString();
		                System.out.print(result);

		        }
		        catch (IOException e)
		        {   System.out.print("error");
		            e.printStackTrace();
		        }
System.out.println("saskjd");
	}

}
