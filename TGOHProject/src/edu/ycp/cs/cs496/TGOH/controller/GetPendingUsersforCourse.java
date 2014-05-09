package edu.ycp.cs.cs496.TGOH.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.ycp.cs.cs496.TGOH.JSON.JSON;
import edu.ycp.cs.cs496.TGOH.temp.User;

public class GetPendingUsersforCourse {
	public User[] getUser(int courseId) throws ClientProtocolException, URISyntaxException, IOException {
		return makeGetRequest(courseId);
	}
	
	private User[] makeGetRequest(int courseId) throws URISyntaxException, ClientProtocolException, IOException{
		// Create HTTP client
 		HttpClient client = new DefaultHttpClient();
		// Construct URI
		URI uri = URIUtils.createURI("http", "10.0.2.2", 8081, "/usersforcourse/" + courseId , null, null);

		// Construct request
		HttpGet request = new HttpGet(uri);
		
		// Execute request
		HttpResponse response = client.execute(request);
		// Parse response
		System.out.print(courseId);
		
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			// Add JSON object to request
			HttpEntity entity = response.getEntity();
			// Parse JSON
			return JSON.getObjectMapper().readValue(entity.getContent(), User[].class);
		} 
		
		// Return null if invalid response
		return null;
	}
}
