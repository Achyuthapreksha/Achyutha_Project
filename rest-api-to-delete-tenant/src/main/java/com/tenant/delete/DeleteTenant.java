package com.tenant.delete;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.actiance.tenant.DeleteFromCeph;
import com.index.EsIndexRemove;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
public class DeleteTenant {
	static StringBuilder msg=new StringBuilder("Result-----\n ");
	static TreeSet<String> set = new TreeSet<String>();
	static List<Document>  info=new ArrayList<Document>();
	static Map<String,Long> data=new ConcurrentHashMap <String, Long>();
	static List<String> indexNames=new ArrayList<>();
	static BasicDBObject regexQuery = new BasicDBObject();
	public static String get(String hours) throws IOException, Exception {
		String mongoIp="192.168.125.116";
		Long thresholdHours=new Long(System.currentTimeMillis());
		thresholdHours-=TimeUnit.HOURS.toMillis(Long.parseLong(hours));
		System.out.println("Threshold hours"+ thresholdHours);

		MongoClient mongo = new MongoClient(mongoIp , 27017 );

		getTenantData(mongo,thresholdHours);

		//getIndexNames(data,mongo);
		try {
			System.out.println("Deleting Tenants ----");
			DeleteFromCeph.deleteContainer(data);
			EsIndexRemove.deleteIndex(data);
			System.out.println("Deleting from mongodb");
			for(Entry<String,Long> d:data.entrySet()) {
				try {
					mongo.dropDatabase(d.getKey());
					System.out.println(d.getKey());
				}catch(Exception e) {
					e.printStackTrace();
				}
			}

		}catch(Exception e) {
			System.out.println(e);
		}
		try {
			deleteTenancyInfo(mongo,data);
		}catch(Exception e) {

		}
		mongo.close();
		//removing the mismatch tenants
		try {
			deleteRemainingTenant(mongoIp);
		}catch(Exception e) {
			e.printStackTrace();
		}
		

		msg.append("Initial Containers in ceph---"+DeleteFromCeph.getInitialContainerCount()+"\n")
		.append("Deleted Containers in ceph---"+DeleteFromCeph.getDeletedContainerCount()+"\n")
		.append("Initial Index Count---"+EsIndexRemove.getInitialIndexCount()+"\n")
		.append("Deleted Index Count---"+EsIndexRemove.getDeletedIndexCount()+"\n");
		System.out.println("Ceph initial and deleted"+DeleteFromCeph.getInitialContainerCount()+"----"+DeleteFromCeph.getDeletedContainerCount());
		System.out.println("ES initial and deleted"+EsIndexRemove.getInitialIndexCount()+"------"+EsIndexRemove.getDeletedIndexCount());
		return msg.toString();

	}


	private static void getTenantData(MongoClient mongo, Long thresholdHours) {
		try {
			MongoDatabase database = mongo.getDatabase("alcatraz");
			MongoCollection<Document> collection =  database.getCollection("tenancy");
			regexQuery.put("tenantName", 
					new BasicDBObject("$regex", "/*")
					.append("$options", "i"));
			FindIterable<Document> cursor = collection.find(regexQuery);
			regexQuery.clear();
			// Getting the iterator 
			Iterator it = cursor.iterator(); 

			while (it.hasNext()) {  
				info.add((Document) it.next());
			}

			for(Document doc:info) {
				if(!doc.getString("tenantName").startsWith("ui")) {
					data.put(doc.getString("tenantName"), Long.valueOf(doc.getDate("createdDate").getTime()));
				}
			}
			info.clear();
			for(Entry<String,Long> entry:data.entrySet()) {
				if(thresholdHours.compareTo((Long) entry.getValue())<0) {
					data.remove(entry.getKey());
				}
			}
		}catch(Exception e) {
			System.out.println("Error while getting tenant info----"+e);
		}


	}
	private static void getIndexNames(Map<String, Long> data, MongoClient mongo) {
		try {
			for(Entry<String,Long> entry:data.entrySet()) {
				MongoDatabase database = mongo.getDatabase(entry.getKey());
				MongoCollection<Document> collections	= database.getCollection("index_metadata");
				regexQuery.put("indexName", 
						new BasicDBObject("$regex", "/*")
						.append("$options", "i"));
				FindIterable<Document> cursors = collections.find(regexQuery);
				Iterator its = cursors.iterator(); 
				while (its.hasNext()) {  
					info.add((Document) its.next());
				}
				for(Document doc:info) {
					indexNames.add(doc.getString("indexName"));
				}
			}
		}catch(Exception e) {
			System.out.println("error while getting indexes---"+e);
		}

	}
	private static void deleteTenancyInfo(MongoClient mongo, Map<String, Long> data) {
		for(Entry<String, Long> d:data.entrySet()) {
			try {
				MongoDatabase database = mongo.getDatabase("alcatraz");

				Bson condition = new Document("$eq",d.getKey());
				Bson filter = new Document("tenantName", condition);
				database.getCollection("tenancy").deleteMany(filter);

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deleteRemainingTenant(String mongoIp) throws IOException, Exception {
		regexQuery.clear();
		List<Document>  tenancyList=new ArrayList<Document>();
		List<String> tenants=new  ArrayList<>();
		Map<String,Long> datas=new ConcurrentHashMap <String, Long>();
		List<String> databaseList=new ArrayList<String>();
		MongoClient mongo = new MongoClient(mongoIp , 27017 );
		MongoDatabase database = mongo.getDatabase("alcatraz");
		MongoCollection<Document> collection =  database.getCollection("tenancy");
		regexQuery.put("tenantName", 
				new BasicDBObject("$regex", "/*")
				.append("$options", "i"));
		
		FindIterable<Document> cursor = collection.find(regexQuery);
		Iterator it = cursor.iterator(); 
		while (it.hasNext()) {  
			tenancyList.add((Document) it.next());
		}
		
		for(Document doc:tenancyList) {
			tenants.add(doc.get("tenantName").toString());
		}
		MongoCursor<String> dbsCursor=mongo.listDatabaseNames().iterator();
		while(dbsCursor.hasNext()) {
			databaseList.add(dbsCursor.next());
		}
		
		databaseList.removeAll(tenants);
	
		System.out.println("Remainig tenants deleting"+databaseList.size());
		for(Object doc:databaseList) {
			if(!((String) doc).startsWith("ui")&&!((String) doc).startsWith("alcatraz")&&!((String) doc).startsWith("config")) {
				datas.put((String) doc,(long) 1234);
			}
		}
		try {
			DeleteFromCeph.deleteContainer(datas);
			EsIndexRemove.deleteIndex(datas);
			for(String doc:databaseList) {
				try {
					if(!((String) doc).startsWith("ui")&&!((String) doc).startsWith("alcatraz")&&!((String) doc).startsWith("config")) {
						mongo.dropDatabase(doc);
						System.out.println(doc);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		mongo.close();
	}
}


