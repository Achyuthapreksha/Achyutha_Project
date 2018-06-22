package com.es.util;

import java.net.InetAddress;
import java.util.Optional;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ESManager {
	 public Optional<TransportClient> getClient(String host, int port) {
	        /*try {
	            Settings.Builder setting =Settings.builder().put("client.transport.sniff", false);
	            TransportClient client = TransportClient.builder().settings(setting).build()
	                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
	            return Optional.of(client);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return Optional.empty();
	        }*/
		 try {
	            Settings.Builder setting =Settings.builder().put("cluster.name", "vagrant-es-cluster");
	            TransportClient client = TransportClient.builder().settings(setting).build()
	                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
	            return Optional.of(client);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return Optional.empty();
	        }
	    }
}
