package com.projectman;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
public class JSONParser {
 
    static InputStream is = null;
    static JSONArray jObj = null;
    static JSONObject Obj = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
    
    /**
     * Sends a HTTP POST Request and returns a JSONArray.
     * 
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public JSONArray getJSONFromUrl(String url, List<NameValuePair> params) throws Exception {
 
        	//Making HTTP request
    	
            //defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
 
      
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) 
        {
        	throw new RuntimeException(e);
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONArray(json);
        } catch (JSONException e) 
        {
        	throw new RuntimeException(e);
        }
 
        return jObj;
 
    }
    
    /**
     * Returns a JSONArray from Url using HTTP GET.
     * @param url
     * @return
     * @throws Exception
     */
    public JSONArray getJSONFromUrlUsingGET(String url) throws Exception {
	    	 
	    	//Making HTTP request
		
	        //defaultHttpClient
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        //HttpPost httpPost = new HttpPost(url);
	        HttpGet httpGet = new HttpGet(url);
	        //httpPost.setEntity(new UrlEncodedFormEntity(params));
	
	        HttpResponse httpResponse = httpClient.execute(httpGet);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        is = httpEntity.getContent();
	
	  
	
	    try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                is, "iso-8859-1"), 8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "n");
	        }
	        is.close();
	        json = sb.toString();
	    } catch (Exception e) 
	    {
	    	throw new RuntimeException(e);
	    }
	
	    // try parse the string to a JSON object
	    try {
	        //jObj = new JSONObject(json);
	    	jObj = new JSONArray(json);
	    } catch (JSONException e) 
	    {
	    	throw new RuntimeException(e);
	    }
	
	    return jObj;
	
	}
    
    /**
     * Returns a JSONObject from Url using HTTP POST.
     * 
     * @param url
     * @param params
     * @return a JSONObject
     * @throws Exception
     */
    public JSONObject getJSONObjectFromUrl(String url, List<NameValuePair> params) throws Exception {
	    	 
	    	//Making HTTP request
		
	        //defaultHttpClient
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new UrlEncodedFormEntity(params));
	
	        HttpResponse httpResponse = httpClient.execute(httpPost);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        is = httpEntity.getContent();
	
	  
	
	    try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                is, "iso-8859-1"), 8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "n");
	        }
	        is.close();
	        json = sb.toString();
	    } catch (Exception e) 
	    {
	    	throw new RuntimeException(e);
	    }
	
	    // try parse the string to a JSON object
	    try {
	        Obj = new JSONObject(json);
	    } catch (JSONException e) 
	    {
	    	throw new RuntimeException(e);
	    }
	
	    return Obj;
	
	}
}