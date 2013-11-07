package com.projectman;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ProjectActivity extends FragmentActivity {
	
	//Instance Variables
	Button submitButton;
	private static String createShopUrl = "http://10.0.2.2:3000/shops.json";
    private JSONParser jsonParser = new JSONParser();
    
    /**
	 * Keep track of the shop creation task to ensure we can cancel it if requested.
	 */
	private ShopCreationTask mAuthTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		// Show the Up button in the action bar.
		setupActionBar();
		
		submitButton = (Button)findViewById(R.id.submit_button);
		
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mAuthTask = new ShopCreationTask();
				mAuthTask.execute((Void) null);
				
				//Go to a new Activity
				Intent startProjectSelectioIntent = new Intent(getApplicationContext(), ProjectSelectActivity.class);
				startActivity(startProjectSelectioIntent);
				finish();
				
			}
		});
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
		getMenuInflater().inflate(R.menu.project, menu);
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
	
	public JSONObject createShop() throws Exception{
	    // Building Parameters
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    
	    params.add(new BasicNameValuePair("contact", "PO Box hehe"));
	    params.add(new BasicNameValuePair("latitude", "84774546545"));
	    params.add(new BasicNameValuePair("longitude", "9882763626"));
	    params.add(new BasicNameValuePair("name", "G Shop"));
	    params.add(new BasicNameValuePair("project_id", "1"));
	    params.add(new BasicNameValuePair("visit_datetime", "12.34.565.2013"));
	    
	    JSONObject json = jsonParser.getJSONObjectFromUrl(createShopUrl,params);
	    
	    return json;
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ShopCreationTask extends AsyncTask<Void, Void, Boolean> {
		JSONObject json;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				//Thread.sleep(2000);
			   json = createShop();
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
				try {
					Log.d("My JSON from creating a shop:Name ", json.getString("name"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Log.d("Error creating a shop", "Error in JSON");
				
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			//showProgress(false);
		}
	}

}
