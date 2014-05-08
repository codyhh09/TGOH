package edu.ycp.cs.cs496.TGOH.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.ycp.cs.cs496.TGOH.JSON.JSON;
import edu.ycp.cs.cs496.TGOH.temp.User;



public class PutPassword {
	public boolean putPassword(User user, String password) throws URISyntaxException, JsonGenerationException, JsonMappingException, IOException {
		return makePutRequest(user, password);
	}

	private boolean makePutRequest(User user, String password) throws URISyntaxException, JsonGenerationException, JsonMappingException, IOException {
		// Create HTTP client
 		HttpClient client = new DefaultHttpClient();
		
		// Construct URI
		URI uri;
		uri = URIUtils.createURI("http", "10.0.2.2", 8081, "/user/" + user.getUserName() + "/" + password, null, null);

		// Construct request
		HttpPut request = new HttpPut(uri);
		
		if(user != null){
			// Create JSON object from Item
			User cloneUser = user;
			cloneUser.setPassword(user.getPassword());
		
			StringWriter sw = new StringWriter();
			JSON.getObjectMapper().writeValue(sw, cloneUser);
			
			// Add JSON object to request
			StringEntity reqEntity = new StringEntity(sw.toString());
			reqEntity.setContentType("application/json");
			request.setEntity(reqEntity);
		}
		// Execute request
		HttpResponse response = client.execute(request);
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			return true;
		else
			return false;
	}
}
