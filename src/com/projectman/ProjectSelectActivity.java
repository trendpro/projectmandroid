package com.projectman;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ProjectSelectActivity extends Activity {
	
	//Instance Variables
	private Button proceedButton;
	private Button createNewShopButtom;
    private static String projectListUrl = "http://10.0.2.2:3000/projects.json";
    private JSONParser jsonParser = new JSONParser();
    private ProjectDataType[] projectsArr;
    
    /**
	 * Keep track of the project loading task to ensure we can cancel it if requested.
	 */
	private ProjectLoadingTask mAuthTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_select);
		// Show the Up button in the action bar.
		setupActionBar();
		
		proceedButton = (Button)findViewById(R.id.proceed_to_project_button);
		createNewShopButtom = (Button)findViewById(R.id.create_new_shop_button);
		
		/**
		 * Logic for retrieving project details from server.
		 */
		proceedButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//get project list from network
		
				
				Intent startProjectSelectioIntent = new Intent(getApplicationContext(), ProjectActivity.class);
				startActivity(startProjectSelectioIntent);
				
				//Do not call this method from an activity
				finish();
				
			}
		});
		
		/**
		 * Logic for creating a new place.
		 */
		createNewShopButtom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startProjectSelectioIntent = new Intent(getApplicationContext(), ProjectActivity.class);
				startActivity(startProjectSelectioIntent);
				finish();
				
			}
		});
		
		mAuthTask = new ProjectLoadingTask();
		mAuthTask.execute((Void) null);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Retrieves List of projects from Server over Network.
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONArray getProjectList() throws Exception{
	    // Building Parameters
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    
	    params.add(new BasicNameValuePair("action", "show"));
	    params.add(new BasicNameValuePair("controller", "projects"));
	    JSONArray json = jsonParser.getJSONFromUrlUsingGET(projectListUrl);
	    
	    return json;
	}
	
	/**
	 * Represents an asynchronous project List loading task used to fetch
	 * list of projects from Server.
	 */
	public class ProjectLoadingTask extends AsyncTask<Void, Void, Boolean> {
		JSONArray json;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				//Thread.sleep(2000);
			   json = getProjectList();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			// TODO: register the new account here.
			
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			//showProgress(false);

			if (success) {
				//do something
				projectsArr = getProjectListFromJson(json);
				for(int i = 0; i < projectsArr.length; i++){
					System.out.println("Name: " + projectsArr[i].getProjectName()+" Id: " + projectsArr[i].getProjectId());
				}
				
				Log.d("JSON object from fetching project list from the net", json.toString());
			} else {
				Log.d("Error fetching project list", "Error in JSON");
				
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			//showProgress(false);
		}
	}
	
	/**
	 * Gets projects IDs and Names from JSON and populates a ProjectDataType[]
	 * 
	 * @param json
	 * @return
	 */
	public ProjectDataType[] getProjectListFromJson(JSONArray json){
		ProjectDataType[] arr = new ProjectDataType[json.length()];
		
		//code for retrieving project Names and ids
		int n = json.length();
		JSONObject jobj;
		
		for(int i = 0; i < n; i++){
			try {
				jobj = json.getJSONObject(i);
				ProjectDataType dat = new ProjectDataType(jobj.getString("name"), jobj.getString("id"));
				arr[i] = dat;
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return arr;
	}

}
