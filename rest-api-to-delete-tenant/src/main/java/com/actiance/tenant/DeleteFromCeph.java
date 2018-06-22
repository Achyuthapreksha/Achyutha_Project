package com.actiance.tenant;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;

public class DeleteFromCeph {
	 static int initialContainerCount=0;
	 static int deletedContainerCount=0;
	public static void setValues(int initial,int delete) {
		initialContainerCount=initial;
		deletedContainerCount=delete;
	}
	public static int getDeletedContainerCount() {
		return deletedContainerCount;
	}
	public static int getInitialContainerCount() {
		return initialContainerCount;
	}
	
	public static   void deleteContainer(Map<String, Long> data) throws IOException, Exception {
        Properties properties = new Properties();
        properties.put(PROPERTY_ENDPOINT,"http://192.168.125.121:7480/auth/1.0");

        BlobStoreContext context = ContextBuilder.newBuilder("swift")
                .credentials("sdet:swift","lwOO4OnBu72arTjOjMiO6N1wOkfFNZ4PwNRaVzGh")
                .overrides(properties)
                .buildView(BlobStoreContext.class);

        BlobStore blobStore = context.getBlobStore();
        System.out.println("Total  containers present--------------------------"+blobStore.list().size());
        initialContainerCount=blobStore.list().size();
        for(Entry<String,Long> d:data.entrySet()) {
			try {
				if(blobStore.containerExists(d.getKey())) {
					deletedContainerCount++;
					blobStore.deleteContainer(d.getKey());
				}
			}
			catch(Exception e) {
				
			}
		}
        System.out.println("ceph"+deletedContainerCount);
        DeleteFromCeph.setValues(initialContainerCount, deletedContainerCount);
        context.close();
    }
}


