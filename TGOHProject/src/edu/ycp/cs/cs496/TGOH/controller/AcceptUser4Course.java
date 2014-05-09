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
import edu.ycp.cs.cs496.TGOH.temp.Registration;
import edu.ycp.cs.cs496.TGOH.temp.RegistrationStatus;
import edu.ycp.cs.cs496.TGOH.temp.User;

public class AcceptUser4Course {
	public boolean putUser(String user, int course) throws URISyntaxException, JsonGenerationException, JsonMappingException, IOException {
		return makePutUser(user, course);
	}

	private boolean makePutUser(String user, int course) throws URISyntaxException, JsonGenerationException, JsonMappingException, IOException {
		// Create HTTP client
 		HttpClient client = new DefaultHttpClient();
		
		// Construct URI
		URI uri;
		uri = URIUtils.createURI("http", "10.0.2.2", 8081, "/regforcourses/" + user + "/" + course, null, null);

		// Construct request
		HttpPut request = new HttpPut(uri);
		
		if(user != null && course < 0){
			// Create JSON object from Item
			GetUser get = new GetUser();
			
			Registration reg = new Registration(get.getUser(user).getId(), course);
			reg.setStatus(RegistrationStatus.APPROVED);
			
		
			StringWriter sw = new StringWriter();
			JSON.getObjectMapper().writeValue(sw, reg);
			
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
