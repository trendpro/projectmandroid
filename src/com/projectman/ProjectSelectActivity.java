package com.projectman;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ProjectSelectActivity extends  ListActivity {
	
	/**
	 * UI Variables
	 */
	private Button proceedButton;
	private Button createNewShopButtom;
	
	/**
	 * Instance Variables
	 */
    private static String projectListUrl = "http://10.0.2.2:3000/projects.json";
    private JSONParser jsonParser = new JSONParser();
    
    private String SelectedProjectID = "987765543332";
    private ArrayList<ProjectDataType> m_orders = null;
    private ListItemAdapter m_adapter;
    
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
		
				
				Intent startProjectIntent = new Intent(getApplicationContext(), ProjectActivity.class);
				startProjectIntent.putExtra("project_id", SelectedProjectID);
				startActivity(startProjectIntent);
				
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
				Intent startProjectIntent = new Intent(getApplicationContext(), ProjectActivity.class);
				startProjectIntent.putExtra("project_id", SelectedProjectID);
				startActivity(startProjectIntent);
				finish();
				
			}
		});
		
		mAuthTask = new ProjectLoadingTask();
		mAuthTask.execute((Void) null);
		
		m_orders = new ArrayList<ProjectDataType>();
		this.m_adapter = new ListItemAdapter(this, R.layout.row, m_orders);
        setListAdapter(this.m_adapter);
        
        System.out.println("Printing...m_orders");
        System.out.println(m_orders);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
        
            	ProjectDataType item = m_orders.get(position);
            	
            	SelectedProjectID = item.getProjectId();
            	
            	Log.d("Selected Project ID", SelectedProjectID);
            }
          });//end of method
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
				m_orders = getProjectListFromJson(json);
				
				if(m_orders != null && m_orders.size() > 0){
	                m_adapter.notifyDataSetChanged();
	                for(int i=0;i<m_orders.size();i++)
	                m_adapter.add(m_orders.get(i));
	            }
	           // m_ProgressDialog.dismiss();
	            m_adapter.notifyDataSetChanged();
				
				//projectsArr = getProjectListFromJson(json);
				for(int i = 0; i < m_orders.size(); i++){
					System.out.println("Name: " + m_orders.get(i).getProjectName()+" Id: " + m_orders.get(i).getProjectId());
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
	public ArrayList<ProjectDataType> getProjectListFromJson(JSONArray json){
		ArrayList<ProjectDataType> arr = new ArrayList<ProjectDataType>();
		
		//code for retrieving project Names and ids
		int n = json.length();
		JSONObject jobj;
		
		for(int i = 0; i < n; i++){
			try {
				jobj = json.getJSONObject(i);
				//ProjectDataType dat = new ProjectDataType(jobj.getString("name"), jobj.getString("id"));
				arr.add(new ProjectDataType(jobj.getString("name"), jobj.getString("id"),
						jobj.getString("description")));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return arr;
	}
	
	//inner class
	private class ListItemAdapter extends ArrayAdapter<ProjectDataType> {

        private ArrayList<ProjectDataType> items;

        public ListItemAdapter(Context context, int textViewResourceId, ArrayList<ProjectDataType> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row, null);
                }
                ProjectDataType o = items.get(position);
                if (o != null) {
                        TextView tt = (TextView) v.findViewById(R.id.item_title);
                        TextView bt = (TextView) v.findViewById(R.id.item_type);
                        //ImageView icon=(ImageView)v.findViewById(R.id.list_item_icon);
                        
                        if (tt != null) {
                              tt.setText(o.getProjectName());                           
                           }
                        if (bt != null) {
                            bt.setText(o.getProjectDescription());                           
                         }
                        
                        
                }
                return v;
        }//end of getView()
	}//end of inner class
}
