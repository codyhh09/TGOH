package edu.ycp.cs.cs496.TGOH.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;

public class RemovingAUserFromCourse {
	public boolean deleteRegristration(int userid, int courseid) throws ClientProtocolException, URISyntaxException, IOException {
		return makeDeleteRequest(userid, courseid);
	}
	
	private boolean makeDeleteRequest(int userid, int courseid) throws URISyntaxException, ClientProtocolException, IOException {
		// Create HTTP client
 		HttpClient client = new DefaultHttpClient();
		
		// Construct URI
		URI uri;
		uri = URIUtils.createURI("http", "10.0.2.2", 8081, "/regforcourses/" + userid +"/"+courseid, null, null);

		// Construct request
		HttpDelete request = new HttpDelete(uri);
		
		// Execute request
		HttpResponse response = client.execute(request);
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			return true;
		else
			return false;
	}
}
