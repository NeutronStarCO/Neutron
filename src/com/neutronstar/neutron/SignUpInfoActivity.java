package com.neutronstar.neutron;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SignUpInfoActivity extends Activity {
	
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private TextView tvName;
	private TextView tvBirthday;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_info);
		
		spinner = (Spinner) findViewById(R.id.sign_up_info_spinner);
		tvName = (TextView) findViewById(R.id.sign_up_info_name);
		tvBirthday = (TextView) findViewById(R.id.sign_up_info_birthday);
		adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item);
		adapter.add(getResources().getString(R.string.male));
		adapter.add(getResources().getString(R.string.female));
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		
		tvBirthday.setOnFocusChangeListener(new OnFocusChangeListener(){

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {  
                    new DatePickerDialog(SignUpInfoActivity.this, new DatePickerDialog.OnDateSetListener() {  
                        @Override  
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
                        {                        	
                        }  
                    }, 1961, 9, 27).show();                 
		        }
		    }
			
		});
	}
	
	public void back(View view)
	{
		this.finish();
	}
	
	public void next(View view)
	{
		if(tvName.length() == 0)
		{
			Toast toast = Toast.makeText(this, "ºÃ°É£¬ÄãÒ²½ÐÁõµÂ»ª£¬ËãÄãºÝ£¬»¶Ó­Äú£¡", Toast.LENGTH_LONG );
			toast.show();
			Intent intent = new Intent();
			intent.setClass(SignUpInfoActivity.this, MainNeutron.class);
			startActivity(intent);
			this.finish();
		}		
	}
	
	class SpinnerSelectedListener implements OnItemSelectedListener {   
		
	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	        // An item was selected. You can retrieve the selected item using
	        // parent.getItemAtPosition(pos)
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }

	}

}
