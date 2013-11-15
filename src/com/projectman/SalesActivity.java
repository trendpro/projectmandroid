package com.projectman;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SalesActivity extends Activity {
	
	/**
	 * UI Variables
	 */
	private EditText projectListEdit;
	private EditText shopNameEdit;
	private EditText salesValueEdit;
	private EditText salesCoordLat;
	private EditText salesCoordLong;
	private Button submitBtn;
	
	/**
	 * Class Variables
	 */
	private static String shopListUrl = "http://pman-platform.herokuapp.com/shops.json";
	//private static String shopListUrl = "http://10.0.2.2:3000/shops.json";
	
    private JSONParser jsonParser = new JSONParser();
    
    private String SelectedShopID = "987765543332";
    private ArrayList<ShopDataType> m_orders = null;
    private String curLatitude = "0.0";
    private String curLongitude = "0.0";
    private ShopDataType nearestShop;
    //private static String createSaleUrl = "http://10.0.2.2:3000/transactions.json";
    private static String createSaleUrl = "http://pman-platform.herokuapp.com/transactions.json";
    
    private JSONParser jsonCreateParser = new JSONParser();
    
    /**
	 * Keep track of the shops loading task to ensure we can cancel it if requested.
	 */
	private ShopLoadingTask mAuthTask = null;
	
	/**
	 * Keep track of the sales entry creation task to ensure we can cancel it if requested.
	 */
	private SalesEntryCreationTask mSalesEntryTask = null;
	
	private LocationManager mlocManager;
	private MyLocationListener mlocListener;
	
	/**
	 * Global variable to hold the current location
	 */
    private Location mCurrentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales);
		
		salesCoordLat =  (EditText)findViewById(R.id.sales_coord_lat);
		salesCoordLong =  (EditText)findViewById(R.id.sales_coord_long);
		shopNameEdit =  (EditText)findViewById(R.id.sale_shop_name);
		projectListEdit =  (EditText)findViewById(R.id.sales_project_name_list);
		salesValueEdit = (EditText)findViewById(R.id.sales_value);
		
		salesCoordLat.setText(curLatitude);
		salesCoordLong.setText(curLongitude);
		projectListEdit.setText("My Current Project");
		
		/**
		 * Make Generated Fields Non Editable.
		 */
		salesCoordLat.setEnabled(false);
		salesCoordLong.setEnabled(false);
		shopNameEdit.setEnabled(false);
		projectListEdit.setEnabled(false);
		
		
		submitBtn = (Button)findViewById(R.id.sales_submit_button);
		
		submitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCurrentLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				
				mSalesEntryTask = new SalesEntryCreationTask();
				mSalesEntryTask.execute((Void) null);
				
			}
		});
		
		mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, mlocListener);
		
		
		mAuthTask = new ShopLoadingTask();
		mAuthTask.execute((Void) null);
		
		mCurrentLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sales, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.checkProjects:
	            //
	        	Intent startProjectSelectioIntent = new Intent(getApplicationContext(), ProjectSelectActivity.class);
				startActivity(startProjectSelectioIntent);
				finish();
	        	
	            return true;
	        case R.id.logout_from_sales:
	            //
	        	Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(logoutIntent);
				finish();
	        	
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Retrieves List of shops from Server over Network.
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONArray getShopList() throws Exception{
	    // Building Parameters
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    
	    params.add(new BasicNameValuePair("action", "show"));
	    params.add(new BasicNameValuePair("controller", "shops"));
	    JSONArray json = jsonParser.getJSONFromUrlUsingGET(shopListUrl);
	    
	    return json;
	}
	
	/**
	 * Gets shops id,name,lat and long from JSON and populates a shopDataType[]
	 * 
	 * @param json
	 * @return
	 */
	public ArrayList<ShopDataType> getShopListFromJson(JSONArray json){
		ArrayList<ShopDataType> arr = new ArrayList<ShopDataType>();
		
		//code for retrieving project Names and ids
		int n = json.length();
		JSONObject jobj;
		
		for(int i = 0; i < n; i++){
			try {
				jobj = json.getJSONObject(i);
				//ProjectDataType dat = new ProjectDataType(jobj.getString("name"), jobj.getString("id"));
				arr.add(new ShopDataType(jobj.getString("name"), jobj.getString("latitude"),
						jobj.getString("longitude"),jobj.getString("id")));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return arr;
	}
	
	public ShopDataType getNearestShop(ArrayList<ShopDataType> arr,Location mCurLoc){
		ShopDataType shop = new ShopDataType("No shop found","36.90358566","-1.20940377","id");
		if(!arr.isEmpty()){
			//Go through all shops comparing their lats and longs with 
			//current location and return the closest.
			int minIndex = 0;
			
			Location loc = new Location("network");
			
			loc.setLatitude(Double.parseDouble(arr.get(0).getShopLat()));
			loc.setLongitude(Double.parseDouble(arr.get(0).getShopLong()));
			double minDist = 0.0;
			minDist = mCurLoc.distanceTo(loc);
			
			for(int i = 0; i < arr.size(); i++){
				//System.out.println("Name: " + m_orders.get(i).getShopName()+" Id: " + m_orders.get(i).getShopId());
				loc.setLatitude(Double.parseDouble(arr.get(i).getShopLat()));
				loc.setLongitude(Double.parseDouble(arr.get(i).getShopLong()));
				
				double curDist = mCurLoc.distanceTo(loc);
				
				if(minDist > curDist){
					minDist = curDist;
					minIndex = i;
				}
			}
			shop =  arr.get(minIndex);
		}
		return shop;
	}
	
	/**
	 * Represents an asynchronous shop List loading task used to fetch
	 * list of projects from Server.
	 */
	public class ShopLoadingTask extends AsyncTask<Void, Void, Boolean> {
		JSONArray json;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				//Thread.sleep(2000);
			   json = getShopList();
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
				m_orders = getShopListFromJson(json);
				
				
				for(int i = 0; i < m_orders.size(); i++){
					System.out.println("Name: " + m_orders.get(i).getShopName()+" Id: " + m_orders.get(i).getShopId());
				}
				mCurrentLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				
				nearestShop = getNearestShop(m_orders, mCurrentLocation);
				SelectedShopID = nearestShop.getShopId();
				shopNameEdit.setText(nearestShop.getShopName());
				
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
	
	public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String Text = "My current shop location is: " + "Latitude = "
                + loc.getLatitude() + "Longitude = " + loc.getLongitude();
 
            curLatitude = "Cur Lat: "+loc.getLatitude();
            curLongitude = "Cur Long: "+loc.getLongitude();
            
            salesCoordLat.setText(curLatitude);
    		salesCoordLong.setText(curLongitude);
    		
    		nearestShop = getNearestShop(m_orders, loc);
			SelectedShopID = nearestShop.getShopId();
			shopNameEdit.setText(nearestShop.getShopName());
    		
    		
    		
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
	
	public String createSalesEntry() throws Exception{
	    // Building Parameters
	    JSONObject params = new JSONObject();
	    params.put("sales", salesValueEdit.getText().toString());
	    params.put("coord_lat", mCurrentLocation.getLatitude()+"");
	    params.put("coord_long", mCurrentLocation.getLongitude()+"");
	    params.put("shop_id", SelectedShopID);
	    
	    String json = jsonCreateParser.getJSONObjectFromUrl(createSaleUrl,params);
	    
	    return json;
	}
	
	/**
	 * Represents an asynchronous sales entry creation task used to authenticate
	 * the user.
	 */
	public class SalesEntryCreationTask extends AsyncTask<Void, Void, Boolean> {
		String json;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				//Thread.sleep(2000);
			   json = createSalesEntry();
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
				salesValueEdit.setText(null);
				try {
					Log.d("My JSON from creating a sales entry:Name ", json);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Log.d("Error creating a sales entry", "Error in JSON");
				
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			//showProgress(false);
		}
	}

}
