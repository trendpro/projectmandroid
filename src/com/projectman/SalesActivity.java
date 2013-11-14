package com.projectman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SalesActivity extends Activity {
	
	/**
	 * UI Variables
	 */
	EditText projectListEdit;
	EditText shopNameEdit;
	EditText salesValueEdit;
	EditText salesCoordLat;
	EditText salesCoordLong;
	Button submitBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales);
		
		submitBtn = (Button)findViewById(R.id.sales_submit_button);
		
		submitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
			}
		});
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
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
