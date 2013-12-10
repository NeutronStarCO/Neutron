package com.neutronstar.neutron;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.neutronstar.neutron.NeutronContract.ITEM;
import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronContract.TAG;

public class NewRecordActivity extends Activity {
	
	private NeutronDbHelper ndb;
	TextView title;
	TextView itemTitle;
	TextView itemValue;
	TextView dateTitle;
	TextView dateValue;
	int item;
	int userid;
	long rowid;
	String datetime;
	private boolean modify = false;
	Bundle bl;
	Intent intent;

	Calendar c = Calendar.getInstance();
	SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat timeFormat = new SimpleDateFormat(" HH:mm:ss.SSS");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_record);
		ndb = NeutronDbHelper.GetInstance(this);
		
		title		= (TextView)findViewById(R.id.new_record_title);
		itemTitle	= (TextView)findViewById(R.id.new_record_itemTitle);
		itemValue	= (TextView)findViewById(R.id.new_record_itemValue);
		dateTitle	= (TextView)findViewById(R.id.new_record_dateTitle);
		dateValue	= (TextView)findViewById(R.id.new_record_dateValue);
		
		intent=this.getIntent();
		//获取Intent中的Bundle数据
		bl=intent.getExtras();
		item = bl.getInt("item");
		userid = bl.getInt("userid");
		rowid = bl.getInt("rowid");
		String titleList[] = {"量量体重","体重","千克","测量日期","年-月-日"};
		
		dateValue.setOnFocusChangeListener(new OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus == true) {  
                    new DatePickerDialog(NewRecordActivity.this, new DatePickerDialog.OnDateSetListener() {  
                        @Override  
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
                        	c.set(Calendar.YEAR, year);
                        	c.set(Calendar.MONTH, monthOfYear);
                        	c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        	datetime = dateFormat.format(c.getTime());
//                        	c = Calendar.getInstance();
                        	new TimePickerDialog(NewRecordActivity.this, new TimePickerDialog.OnTimeSetListener() {  
                                @Override  
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {  
                                	c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                	c.set(Calendar.MINUTE, minute); 
                                	datetime += timeFormat.format(c.getTime());
                                	dateValue.setText(datetime.substring(0, 16));  
                                }  
                            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(); 
                        }  
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();  
                 
		        }
		    }
		});
		
		switch(item)
		{
		case ITEM.weight:
			titleList = getResources().getStringArray(R.array.new_record_weight);	
			break;
		case ITEM.height:
			titleList = getResources().getStringArray(R.array.new_record_height);	
			break;
		case ITEM.B_RBC:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.B_WBC:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.B_HGB:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.B_HCT:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.B_PLT:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.B_PCT:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.U_SG:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.U_PH:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.BP_X:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.BP_Y:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.heart_rate:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		// Group testing	
		case ITEM.g_urineTest:
			titleList = getResources().getStringArray(R.array.new_record_rbc);	
			break;
		case ITEM.g_bloodPressure:
			titleList = getResources().getStringArray(R.array.new_record_g_blood_pressure);
			break;
		case ITEM.g_bloodTest:
			titleList = getResources().getStringArray(R.array.new_record_g_blood_test);
			break;
		}
		if(	   (item == ITEM.weight)
			|| (item == ITEM.height)
			|| (item == ITEM.B_WBC)
			|| (item == ITEM.B_RBC)
			|| (item == ITEM.B_HGB)
			|| (item == ITEM.B_HCT)
			|| (item == ITEM.B_PLT)
			|| (item == ITEM.B_PCT)
			|| (item == ITEM.U_SG)
			|| (item == ITEM.U_PH)
			|| (item == ITEM.BP_X)
			|| (item == ITEM.BP_Y)
			|| (item == ITEM.heart_rate)
			)
		{
			itemValue.setText(new DecimalFormat("#.##").format(bl.getDouble("value")));
			c = Calendar.getInstance();
			datetime = datetimeFormat.format(c.getTime());
			dateValue.setText(datetime.substring(0, 16));
		}
		else if((item == ITEM.g_bloodPressure)
			||  (item == ITEM.g_bloodTest)
			||  (item == ITEM.g_urineTest)
			)
		{
			itemValue.setInputType(InputType.TYPE_CLASS_TEXT);
			modify = bl.getBoolean("modify");
			if(modify)
			{
				itemValue.setText(bl.getString("testingInstitution"));
				dateValue.setText(bl.getString("datetime"));
				rowid = bl.getInt("rowid");
			}
		}

		title.setText(titleList[0]);
		itemTitle.setText(titleList[1]);
		itemValue.setHint(titleList[2]);
		dateTitle.setText(titleList[3]);
		dateValue.setHint(titleList[4]);
		
	}
	
	public void save(View v){
		SQLiteDatabase db = ndb.getWritableDatabase();
		ContentValues cv = new ContentValues();
		String timestamp = datetimeFormat.format(Calendar.getInstance().getTime());
		if(	   (item == ITEM.weight)
			|| (item == ITEM.height)
			|| (item == ITEM.B_WBC)
			|| (item == ITEM.B_RBC)
			|| (item == ITEM.B_HGB)
			|| (item == ITEM.B_HCT)
			|| (item == ITEM.B_PLT)
			|| (item == ITEM.B_PCT)
			|| (item == ITEM.U_SG)
			|| (item == ITEM.U_PH)
			|| (item == ITEM.BP_X)
			|| (item == ITEM.BP_Y)
			|| (item == ITEM.heart_rate)
		)
		{
			bl.putString("value", itemValue.getText().toString());
			bl.putInt("item", item);
			intent.putExtras(bl);
			cv.put(NeutronRecord.COLUMN_NAME_USERID, userid);
			cv.put(NeutronRecord.COLUMN_NAME_ITEM, item);
			cv.put(NeutronRecord.COLUMN_NAME_VALUE, Double.parseDouble(itemValue.getText().toString()));
			cv.put(NeutronRecord.COLUMN_NAME_DATETIME, dateValue.getText().toString()+":00.000");
			cv.put(NeutronRecord.COLUMN_NAME_TIMESTAMP, timestamp);
			cv.put(NeutronRecord.COLUMN_NAME_TAG, TAG.normal);
			db.insert(NeutronRecord.TABLE_NAME, null, cv);
			NewRecordActivity.this.setResult(RESULT_OK, intent);
			NewRecordActivity.this.finish();
		}
		else if((item == ITEM.g_bloodPressure)
			||  (item == ITEM.g_bloodTest)
			||  (item == ITEM.g_urineTest)
			)
		{
			if(modify)
			{
				cv.put(NeutronGroupTesting.COLUMN_NAME_DATETIME, dateValue.getText().toString());
				cv.put(NeutronGroupTesting.COLUMN_NAME_TESTINGINST, itemValue.getText().toString());
				String whereClause = "rowid=?";
				String[] whereArgs = {String.valueOf(rowid)};  
				db.update(NeutronGroupTesting.TABLE_NAME, cv, whereClause, whereArgs);
			}else
			{
				cv.put(NeutronGroupTesting.COLUMN_NAME_USERID, userid);
				cv.put(NeutronGroupTesting.COLUMN_NAME_GROUP, item);
				cv.put(NeutronGroupTesting.COLUMN_NAME_DATETIME, dateValue.getText().toString()+":00.000");
				cv.put(NeutronGroupTesting.COLUMN_NAME_TESTINGINST, itemValue.getText().toString());
				cv.put(NeutronGroupTesting.COLUMN_NAME_TAG, TAG.normal);
				rowid = db.insert(NeutronGroupTesting.TABLE_NAME, null, cv);
			}			
			bl.putLong("rowid", rowid);
			bl.putString("value", itemValue.getText().toString());
			bl.putString("datetime", dateValue.getText().toString());
			intent.putExtras(bl);
			NewRecordActivity.this.setResult(RESULT_OK, intent);
			NewRecordActivity.this.finish();
		}
	}
	
	public void cancel_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

}
