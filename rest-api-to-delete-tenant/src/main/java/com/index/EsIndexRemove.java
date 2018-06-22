package com.index;

import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;

import com.es.util.ESManager;

public class EsIndexRemove {
	static int initialIndexCount=0;
	static int deletedIndexCount=0;
	public static  int getInitialIndexCount() {
		return initialIndexCount;
	}
	public static int getDeletedIndexCount() {
		return deletedIndexCount;
	}
	public static void setDeletedIndexCount(int initial, int deleted) {
		initialIndexCount=initial;
		 deletedIndexCount=deleted;
	}
	public static void deleteIndex(Map<String, Long> data) {
		
		
		
		ESManager esManager = new ESManager();
		Client client = esManager.getClient("192.168.125.110", 9300).get();

		String[] indexList = client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().concreteAllIndices();
		initialIndexCount=indexList.length;
		
		
		System.out.println("Deleting index------");
		for(Entry<String, Long> d:data.entrySet()) {
			try {

				DeleteIndexResponse deleteResponse = client.admin().indices().delete(new DeleteIndexRequest(d.getKey()+"_*")).actionGet();
				System.out.println("ES response:"+deleteResponse.isAcknowledged());
				System.out.println(d.getKey());
				deletedIndexCount++;
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
		System.out.println("es"+deletedIndexCount);
		EsIndexRemove.setDeletedIndexCount(initialIndexCount, deletedIndexCount);
		client.close();
	} 
	
	
}





