package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneNumberActivity extends Activity {
	public static final int TAG_LOGIN = 1;
	public static final int TAG_SIGN_IN = 2;
	private final int requestCode = 1;
	private int tag;
	private TextView tvPhoneNumber;
	private Button tvIDDbutton;
	private TextView tvIDD;
	private CheckBox cbTerms;
	private TextView tvOthers;
	private String IDDCountry[];
	private String IDD[];
	private PopupWindow pwIDD;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_number);
		Intent intent = this.getIntent();
		Bundle bl = intent.getExtras();
		tag = bl.getInt("tag");
		tvPhoneNumber = (TextView) findViewById(R.id.phone_number_number);
		tvPhoneNumber.requestFocus();
		tvIDDbutton = (Button) findViewById(R.id.phone_number_IDDbutton);
		tvIDD = (TextView) findViewById(R.id.phone_number_IDD);
		tvOthers = (TextView) findViewById(R.id.phone_number_others);
		cbTerms = (CheckBox) findViewById(R.id.phone_number_terms);
		IDDCountry = getResources().getStringArray(R.array.idd_country);
		IDD = getResources().getStringArray(R.array.idd);
		
		switch(tag)
		{
		case TAG_LOGIN:
			tvOthers.setVisibility(View.GONE);
			break;
		case TAG_SIGN_IN:
			break;
		}	
	}
	
	public void back(View view)
	{
		this.finish();
	}

	public void next(View view)
	{
		if(!cbTerms.isChecked())
		{
			Toast toast = Toast.makeText(this, "您还没有同意用户协议", Toast.LENGTH_LONG );
			toast.show();
		}
//		else if(tvPhoneNumber.getText().length() <= 8)
//		{
//			Toast toast = Toast.makeText(this, "你的手机号码少于8位", Toast.LENGTH_LONG );
//			toast.show();
//		}
		else
		{
			Intent intent = new Intent(PhoneNumberActivity.this, ConfirmationDialogActivity.class);
			Bundle bl = new Bundle();
			bl.putInt("tag", ConfirmationDialogActivity.TAG_PHONE_NUMBER);
			bl.putString("IDD", tvIDD.getText().toString());
			bl.putString("phonenumber", tvPhoneNumber.getText().toString());
			intent.putExtras(bl);
			startActivityForResult(intent, requestCode);
		}
	}
	
	public void chooseIDD(View view)
	{
		int x = getWindowManager().getDefaultDisplay().getWidth() / 4;
		int y = getWindowManager().getDefaultDisplay().getHeight() / 5;
		showPopupWindow(x, y);
	}
	
	public void showPopupWindow(int x, int y) 
	{
		
		LinearLayout llIDD = (LinearLayout) LayoutInflater.from(PhoneNumberActivity.this).inflate(
				R.layout.tab_today_dialog, null);
		ListView lvIDD = (ListView) llIDD.findViewById(R.id.tab_today_dialog);
		lvIDD.setAdapter(new ArrayAdapter<String>(PhoneNumberActivity.this,
				R.layout.simple_text_item, R.id.simple_text_item, IDDCountry));
		
		pwIDD = new PopupWindow(PhoneNumberActivity.this);
		pwIDD.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		pwIDD.setHeight(getWindowManager().getDefaultDisplay().getHeight() / 2);
		pwIDD.setOutsideTouchable(true);
		pwIDD.setFocusable(true);
		pwIDD.setContentView(llIDD);

		pwIDD.showAtLocation(findViewById(R.id.phone_number_linearlayout), Gravity.LEFT | Gravity.TOP, x, y);
		
		lvIDD.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tvIDDbutton.setText(IDDCountry[arg2]);
				tvIDD.setText(IDD[arg2]);
				pwIDD.dismiss();
				pwIDD = null;
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		switch(resultCode){
		case RESULT_OK:
			Bundle bl = data.getExtras();
			boolean confirmation = bl.getBoolean("confirmation");
			if(confirmation)
			{
				Log.d("return", String.valueOf(confirmation));
				Intent intent = new Intent();
				Bundle bundle  = new Bundle();
				switch(tag)
				{
				case TAG_LOGIN:
					intent = new Intent(PhoneNumberActivity.this, VarificationCodeActivity.class);
					bundle.putInt("tag", VarificationCodeActivity.TAG_LOGIN);
					bundle.putString("IDD", tvIDD.getText().toString());
					bundle.putString("phonenumber", tvPhoneNumber.getText().toString());
					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
					break;
				case TAG_SIGN_IN:
					intent = new Intent(PhoneNumberActivity.this, VarificationCodeActivity.class);
					bundle.putInt("tag", VarificationCodeActivity.TAG_SIGN_IN);
					bundle.putString("IDD", tvIDD.getText().toString());
					bundle.putString("phonenumber", tvPhoneNumber.getText().toString());
					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
					break;
				}
			}
			else
			{
				// Nothing to do!
			}
			break;
		case RESULT_FIRST_USER:
			this.setResult(RESULT_FIRST_USER, new Intent());
			this.finish();
			break;
		}
	}
}
