package com.projectman;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class ProjectActivity extends FragmentActivity implements
					GooglePlayServicesClient.ConnectionCallbacks,
			 GooglePlayServicesClient.OnConnectionFailedListener{
	
	/**
	 * UI Variables
	 */
	Button submitButton;
	private EditText mShopNameView;
	private EditText mShopContactView;
	private EditText mShopRadiusView;
	
	/**
	 * Instance Variable
	 */
	//private static String createShopUrl = "http://10.0.2.2:3000/shops.json";
	private static String createShopUrl = "http://pman-platform.herokuapp.com/shops.json";
	
    private JSONParser jsonParser = new JSONParser();
    private String shopName;
    private String shopContact;
    private String shopRadius;
    private String shopLatitude;
    private String shopLongitude;
    private String visitDatetime = "8.11.2013 7.17 PM";
    private String projectID;
    
    /**
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    /**
     *  Debugging tag for the application
     */
    public static final String APPTAG = "LocationSample";
    
    /**
     *  Stores the current instantiation of the location client in this object
     */
    //private LocationClient mLocationClient;
    
    /**
	 * Keep track of the shop creation task to ensure we can cancel it if requested.
	 */
	private ShopCreationTask mAuthTask = null;
	
	LocationManager mlocManager;
	MyLocationListener mlocListener;
	
	/**
	 * Global variable to hold the current location
	 */
    Location mCurrentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Get Project_ID from Project selection Activity.
		Bundle extras = this.getIntent().getExtras();
		projectID = extras.getString("project_id");
		
		//Populate Views from XML.
		submitButton = (Button)findViewById(R.id.submit_button);
		mShopNameView = (EditText) findViewById(R.id.shop_name_edit);
		mShopContactView = (EditText) findViewById(R.id.shop_contact_edit);
		mShopRadiusView = (EditText) findViewById(R.id.shop_radius_edit);
		
		
		/*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        //mLocationClient = new LocationClient(this, this, this);
        //mCurrentLocation = mLocationClient.getLastLocation();
		mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, mlocListener);
		
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCurrentLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				shopLatitude = mCurrentLocation.getLatitude()+"";
				shopLongitude = mCurrentLocation.getLongitude()+"";
				
				Log.d("Lat", shopLatitude);
				Log.d("Shop Longitude", shopLongitude);
				String Text = "My current shop location is: " + "Latitude = "
		                + shopLatitude + "Longitude = " + shopLongitude;
				
		        Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT)
		                    .show();
				
				shopName =  mShopNameView.getText().toString();
				shopRadius = mShopRadiusView.getText().toString();
				shopContact = mShopContactView.getText().toString();
				
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
		case R.id.go_to_sales_from_project:
            //
        	Intent startProjectSelectioIntent = new Intent(getApplicationContext(), SalesActivity.class);
			startActivity(startProjectSelectioIntent);
			finish();
        	
            return true;
		case R.id.logout_from_projects_project:
            //
        	Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(logoutIntent);
			finish();
        	
            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public String createShop() throws Exception{
	    // Building Parameters
	    JSONObject params = new JSONObject();
	    params.put("contact", shopContact);
	    params.put("latitude", shopLatitude);
	    params.put("longitude", shopLongitude);
	    params.put("name", shopName);
	    params.put("project_id", projectID);
	    params.put("visit_datetime", visitDatetime);
	    params.put("shop_radius", shopRadius);
	    
	    String json = jsonParser.getJSONObjectFromUrl(createShopUrl,params);
	    
	    return json;
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ShopCreationTask extends AsyncTask<Void, Void, Boolean> {
		String json;
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
					Log.d("My JSON from creating a shop:Name ", json);
				} catch (Exception e) {
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
	
    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            //...
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    //...
                    break;
                }
            //...
        }
     }
  
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private boolean servicesConnected() {
    	//ConnectionResult connectionResult =  new ConnectionResult(statusCode, pendingIntent)
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                /**
                 * THis is my modification to make the error go away.I should fix it later...
                 */
                //errorFragment.show(getSupportFragmentManager(),APPTAG);
                errorFragment.show(getFragmentManager(), APPTAG);
            }
            return false;
        }
    }

    /**
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// TODO Auto-generated method stub
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
        }
    }
    //...
		
	/**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
	@Override
	public void onConnected(Bundle dataBundle) {
		// TODO Auto-generated method stub
		// Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		
	}

	/**
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		// Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
		
	}
	
	/**
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        //mLocationClient.connect();
    }
    
    /**
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        //mLocationClient.disconnect();
        super.onStop();
    }
    
    /**
     * Displays an Alert message for an error or failure.
     */
    protected void displayAlert( String title, String message ) {
        AlertDialog.Builder confirm = new AlertDialog.Builder( this );
        confirm.setTitle( title);
        confirm.setMessage( message );
        confirm.setNegativeButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int which ) {
            }
        } );
        confirm.show().show();                
    }
    
    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            
            /**
             * THis is my modification to make the error go away.I should fix it later...
             */
            //errorFragment.show(getSupportFragmentManager(), APPTAG);
            errorFragment.show(getFragmentManager(), APPTAG);
        }
    }
    
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String Text = "My current shop location is: " + "Latitude = "
                + loc.getLatitude() + "Longitude = " + loc.getLongitude();
 
            shopLatitude = loc.getLatitude()+"";
            shopLongitude = loc.getLongitude()+"";
            Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Disable",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Enable",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

   

}
