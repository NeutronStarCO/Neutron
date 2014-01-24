package com.neutronstar.neutron;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.neutronstar.neutron.NeutronContract.GENDER;
import com.neutronstar.neutron.NeutronContract.ITEM;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.model.BMIModel;
import com.neutronstar.neutron.model.RMRModel;
import com.neutronstar.neutron.model.User;

public class MainTabTodayFragment extends Fragment {
	
	public static MainTabTodayFragment instance = null;
	private View view;
	private NeutronDbHelper ndb;
	private Intent intent;
	private Bundle bl;
	private User user;
	private BMIModel bmiModel;
	private RMRModel rmrModel;
	private WebView wv;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private TextView man_tab_today_metabolismAcceleration;
	private TextView man_tab_today_metabolismRate;
	private TextView man_tab_today_metabolismTotal;
	private TextView man_tab_today_metabolismRMRRateValue;	
	private TextView tvTitle;
	private PopupWindow pwTitle;
	private LinearLayout llTitle;
	private ListView lvTitle;
	private String familyTitle[];
	private Integer familyId[];
	private double currentTotalCost = 0;
	double currentAcceleration = 0;
	private Timer updateTimer;
	
	private double lowAcc;
	private final double FILTERING_VALUE = 0.8;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		view = inflater.inflate(R.layout.main_tab_today, container, false);
		ndb = NeutronDbHelper.GetInstance(getActivity());
		intent = getActivity().getIntent();
		bl = intent.getExtras();
		Log.d("-----", "" + bl.getInt("userid"));
		// 获取家庭成员列表
		familyId = new Integer[1];
		familyTitle = new String[1];
		List<Integer> listId = new ArrayList<Integer>();  
		List<String> listName = new ArrayList<String>(); 
		SQLiteDatabase db = ndb.getReadableDatabase();
		String[] projection = {
			    NeutronUser.COLUMN_NAME_ID,
			    NeutronUser.COLUMN_NAME_NAME
			    };
		String selection = "" + NeutronUser.COLUMN_NAME_TAG + "=" + TAG.normal;
		Cursor cur = db.query(
				NeutronUser.TABLE_NAME,  // The table to query
			    projection,                // The columns to return
			    selection,                 // The columns for the WHERE clause selection
			    null,                      // The values for the WHERE clause selectionArgs
			    null,                      // don't group the rows
			    null,                      // don't filter by row groups
			    null                  		// The sort order
			    );
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					listId.add(cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_ID)));
					listName.add(cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_NAME)));
				} while (cur.moveToNext());
			}
			familyId = listId.toArray(new Integer[1]);
			familyTitle = listName.toArray(new String[1]);
		}
		
		// 初始化界面内容
		initMainTabToday(bl.getInt("userid"));	
		return view;
	}

	private void initMainTabToday(int id) {
		// init WebView
		user = new User(getActivity(), id);
		bmiModel = new BMIModel(getActivity(), user);
		rmrModel = new RMRModel(getActivity(), user);
		
		((ImageView) view.findViewById(R.id.man_tab_today_avatar)).setImageBitmap(user.getAvatar());
		wv = (WebView) view.findViewById(R.id.wv);
		wv.getSettings().setJavaScriptEnabled(true); // 设置WebView支持javascript
		wv.getSettings().setUseWideViewPort(true); // 设置是当前html界面自适应屏幕
		wv.getSettings().setSupportZoom(true); // 设置支持缩放
		wv.getSettings().setBuiltInZoomControls(true);// 显示缩放控件
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.requestFocus();
		wv.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");
		wv.loadUrl("file:///android_asset/html/test_chart.html"); // 加载assert目录下的文件
		// wv.loadUrl("javascript:refreshToday('" + getRemoteData() + "')");
		// 获取sensor服务，选择加速度感应器
		sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		tvTitle = (TextView) view.findViewById(R.id.man_tab_today_title);
		man_tab_today_metabolismAcceleration = (TextView) view.findViewById(R.id.man_tab_today_metabolismAcceleration);
		man_tab_today_metabolismRate = (TextView) view.findViewById(R.id.man_tab_today_metabolismRate);
		man_tab_today_metabolismTotal = (TextView) view.findViewById(R.id.man_tab_today_metabolismTotal);
		currentTotalCost = rmrModel.getCurrentTotalCost();
		man_tab_today_metabolismTotal.setText(new DecimalFormat("#.##").format(currentTotalCost));
		man_tab_today_metabolismRMRRateValue = (TextView) view.findViewById(R.id.man_tab_today_metabolismRMRRateValue);
		man_tab_today_metabolismRMRRateValue.setText(new DecimalFormat("#.##")
		.format(rmrModel.getRMRPerHour()));
		// 姓名
		TextView man_tab_today_name = (TextView) view.findViewById(R.id.man_tab_today_name);
		man_tab_today_name.setText(user.getName());
		// 性别
		ImageView man_tab_today_gender = (ImageView) view.findViewById(R.id.man_tab_today_gender);
		if(user.getGender() == GENDER.male)
			man_tab_today_gender.setImageResource(R.drawable.sex_male);
		else if(user.getGender() == GENDER.female)
			man_tab_today_gender.setImageResource(R.drawable.sex_female);
		else
			man_tab_today_gender.setImageResource(R.drawable.sex_unknown);
		// 体重
		TextView man_tab_today_weight = (TextView) view.findViewById(R.id.man_tab_today_weight);
		if(bmiModel.isWeightRecorded())
			man_tab_today_weight.setText(new DecimalFormat("#.#").format(bmiModel.getWeight()));
		else
			man_tab_today_weight.setText(getResources().getString(R.string.unknown));
		// 身高
		TextView man_tab_today_height = (TextView) view.findViewById(R.id.man_tab_today_height);
		if(bmiModel.isHeightRecorded())
			man_tab_today_height.setText(new DecimalFormat("#.#").format(bmiModel.getHeight()));
		else
			man_tab_today_height.setText(getResources().getString(R.string.unknown));
		// BMI
		TextView man_tab_today_bmi = (TextView) view.findViewById(R.id.man_tab_today_bmi);
		if(bmiModel.isHeightRecorded()&&bmiModel.isWeightRecorded())
			man_tab_today_bmi.setText(new DecimalFormat("#.#").format(bmiModel.getBMI()));
		else
			man_tab_today_bmi.setText(getResources().getString(R.string.unknown));	
		// 体型
		TextView man_tab_today_bmiMeasure = (TextView) view.findViewById(R.id.man_tab_today_bmiMeasure);
		if(bmiModel.isHeightRecorded()&&bmiModel.isWeightRecorded())
			man_tab_today_bmiMeasure.setText(getResources().getStringArray(R.array.bmi_measure)[bmiModel.getMeasureLevel()]);
		else
			man_tab_today_bmiMeasure.setText(getResources().getString(R.string.unknown));	

		updateTimer = new Timer("gForceUpdate");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sensorManager.registerListener(sensorEventListener,
						accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
			}
		}, 0, 5000);
		
		view.findViewById(R.id.man_tab_today_layout_weight)
		.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
//				if(!bmiModel.isWeightRecorded())
				{
					Intent intent = new Intent(getActivity(), NewRecordActivity.class);
					Bundle bl = new Bundle();
					bl.putInt("userid", user.getID());
					bl.putInt("item", ITEM.weight);
					bl.putInt("rowid", -1);
					bl.putDouble("value", bmiModel.getWeight());
					//将Bundle放入Intent传入下一个Activity
					intent.putExtras(bl);
					startActivityForResult(intent,ITEM.weight);
				}	
			}
		});
		
		view.findViewById(R.id.man_tab_today_layout_height)
		.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
//				if(!bmiModel.isHeightRecorded())
				{
					Intent intent = new Intent(getActivity(), NewRecordActivity.class);
					Bundle bl = new Bundle();
					bl.putInt("userid", user.getID());
					bl.putInt("item", ITEM.height);
					bl.putDouble("value", bmiModel.getHeight());
					bl.putInt("rowid", -1);
					intent.putExtras(bl);
					startActivityForResult(intent,ITEM.height);
				}		
			}
		});
		
		
		view.findViewById(R.id.man_tab_today_layout_bloodTest)
		.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), PEHistoryActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("userid", user.getID());
				bl.putInt("item", ITEM.g_bloodTest);
				intent.putExtras(bl);
				startActivity(intent);	
			}
		});
		
		view.findViewById(R.id.man_tab_today_layout_urineTest)
		.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), PEHistoryActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("userid", user.getID());
				bl.putInt("item", ITEM.g_urineTest);
				intent.putExtras(bl);
				startActivity(intent);	
			}
		});
		
		view.findViewById(R.id.man_tab_today_layout_bloodPressure)
		.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), PEHistoryActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("userid", user.getID());
				bl.putInt("item", ITEM.g_bloodPressure);
				intent.putExtras(bl);
				startActivity(intent);	
			}
		});
		
		view.findViewById(R.id.man_tab_today_title)
		.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.up_32), null);
				int y = tvTitle.getBottom() * 3 / 2;
				int x = getActivity().getWindowManager().getDefaultDisplay().getWidth() / 4;
				showPopupWindow(x, y);
			}
		});
	}
	
	private final SensorEventListener sensorEventListener = new SensorEventListener() {
		// 系统设置的重力加速度标准值，设备在水平静止的情况下就承受这个压力，所以默认Y轴方向的加速度值为STANDARD_GRAVITY
		double calibration = SensorManager.STANDARD_GRAVITY;

		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		public void onSensorChanged(SensorEvent event) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			
			// 计算三个方向的加速度
			double a = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
					+ Math.pow(z, 2)));
			double acc = Math.abs((float) (a - calibration));
			lowAcc = lowAcc * FILTERING_VALUE + acc * ( 1.0f - FILTERING_VALUE );

			// 消去原有的重力引起的压力
			currentAcceleration = Math.abs(acc - lowAcc);
			sensorManager.unregisterListener(sensorEventListener);
			if(null !=getActivity())
				refreshAccelerometer();
		}
	};
	
	private void refreshAccelerometer() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				String currentG = currentAcceleration
						/ SensorManager.STANDARD_GRAVITY + "Gs";
				man_tab_today_metabolismAcceleration.setText(new DecimalFormat(
						"#.###").format(currentAcceleration));

				man_tab_today_metabolismRate
						.setText(new DecimalFormat("#.#").format(rmrModel
								.getRMRPerHour()
								+ rmrModel
										.getExerciseCostRate(currentAcceleration)));
				currentTotalCost += (rmrModel.getRMRPerHour() + rmrModel
						.getExerciseCostRate(currentAcceleration)) * 5 / 3600;
				man_tab_today_metabolismTotal.setText(new DecimalFormat("#.##")
						.format(currentTotalCost));
			}
		});
	}
		
	public void onPause() {
		sensorManager.unregisterListener(sensorEventListener);
		updateTimer.cancel();
		super.onPause();
	}
	
	public void onResume() {
		super.onResume();
		updateTimer = new Timer("gForceUpdate");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sensorManager.registerListener(sensorEventListener,
						accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
			}
		}, 0, 5000);
	}
	
	public void showPopupWindow(int x, int y) {
		llTitle = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
				R.layout.tab_today_dialog, null);
		lvTitle = (ListView) llTitle.findViewById(R.id.tab_today_dialog);
		lvTitle.setAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.simple_text_item, R.id.simple_text_item, familyTitle));
		pwTitle = new PopupWindow(getActivity());
//		pwTitle.setBackgroundDrawable(new BitmapDrawable());
		pwTitle.setWidth(getActivity().getWindowManager().getDefaultDisplay().getWidth() / 2);
		pwTitle.setHeight(getActivity().getWindowManager().getDefaultDisplay().getHeight() / 2);
		pwTitle.setOutsideTouchable(true);
		pwTitle.setFocusable(true);
		pwTitle.setContentView(llTitle);
		pwTitle.showAtLocation(getActivity().findViewById(R.id.main_tab_today), Gravity.LEFT | Gravity.TOP, x, y);

		lvTitle.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tvTitle.setText(familyTitle[arg2]);
				initMainTabToday(familyId[arg2]);
				pwTitle.dismiss();
				pwTitle = null;
				tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_24), null);
			}
		});
	}
	
	public class WebAppInterface {
		Context mContext;
		/** Instantiate the interface and set the context */
		WebAppInterface(Context c) {
			mContext = c;
		}

		public String getHourData(String toast) {
			return getLocalData();
		}
	}
	
	private String getLocalData() { 
		try {
			double sumHour[] = rmrModel.getCurrentHourCosts(getActivity());
			JSONArray jadata = new JSONArray();
			for (int i = 0; i < 24; i++) {
				jadata.put(sumHour[i]);
			}
			return jadata.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode)
		{
		case Activity.RESULT_OK:
			//获取Bundle的数据
			Bundle bl= data.getExtras();
			String value=bl.getString("value");Log.d("res",value);
			switch(requestCode){
			case ITEM.weight:
				TextView textView_weight = (TextView) getActivity().findViewById(R.id.man_tab_today_weight);
				textView_weight.setText(value);Log.d("weight",value);
				break;
			case ITEM.height:
				TextView textView_height = (TextView) getActivity().findViewById(R.id.man_tab_today_height);
				textView_height.setText(value);
				break;
			}
			break;
		default:
			break;
		}
	}
}
