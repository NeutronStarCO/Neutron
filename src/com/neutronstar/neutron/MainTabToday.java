package com.neutronstar.neutron;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.neutronstar.neutron.NeutronContract.ITEM;
import com.neutronstar.neutron.NeutronContract.USER;
import com.neutronstar.neutron.model.BMIModel;
import com.neutronstar.neutron.model.RMRModel;
import com.neutronstar.neutron.model.User;

public class MainTabToday extends Activity implements OnTabActivityResultListener{
	
	public static MainTabToday instance = null;
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
	private String title[] = { "��", "����", "�ε���", "��÷¡", "��ʲ" };

	private double currentTotalCost = 0;
	float currentAcceleration = 0;
	private Timer updateTimer;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_today);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		instance = this;
		initMainTabToday();
		
		
	}
	
	private void initMainTabToday() {
		// init WebView
		user = new User(this, USER.me);
		bmiModel = new BMIModel(this, user);
		rmrModel = new RMRModel(this, user);
		
		((ImageView) findViewById(R.id.man_tab_today_avatar)).setImageBitmap(user.getAvatar());
		wv = (WebView) findViewById(R.id.wv);
		wv.getSettings().setJavaScriptEnabled(true); // ����WebView֧��javascript
		wv.getSettings().setUseWideViewPort(true); // �����ǵ�ǰhtml��������Ӧ��Ļ
		wv.getSettings().setSupportZoom(true); // ����֧������
		wv.getSettings().setBuiltInZoomControls(true);// ��ʾ���ſؼ�
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.requestFocus();
		wv.addJavascriptInterface(new WebAppInterface(this), "Android");
		wv.loadUrl("file:///android_asset/html/test_chart.html"); // ����assertĿ¼�µ��ļ�
		// wv.loadUrl("javascript:refreshToday('" + getRemoteData() + "')");
		// init Numbers
		// ��ȡsensor����ѡ����ٶȸ�Ӧ��
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		tvTitle = (TextView) findViewById(R.id.man_tab_today_title);
		man_tab_today_metabolismAcceleration = (TextView) findViewById(R.id.man_tab_today_metabolismAcceleration);
		man_tab_today_metabolismRate = (TextView) findViewById(R.id.man_tab_today_metabolismRate);
		man_tab_today_metabolismTotal = (TextView) findViewById(R.id.man_tab_today_metabolismTotal);
		currentTotalCost = rmrModel.getCurrentTotalCost();
		man_tab_today_metabolismTotal.setText(new DecimalFormat("#.##")
				.format(currentTotalCost));
		man_tab_today_metabolismRMRRateValue = (TextView) findViewById(R.id.man_tab_today_metabolismRMRRateValue);
		man_tab_today_metabolismRMRRateValue.setText(new DecimalFormat("#.##")
		.format(rmrModel.getRMRPerHour()));
		// ����
		TextView man_tab_today_name = (TextView) findViewById(R.id.man_tab_today_name);
		man_tab_today_name.setText(user.getName());
		// �Ա�
		ImageView man_tab_today_gender = (ImageView) findViewById(R.id.man_tab_today_gender);
		if(user.getGender().equals("male"))
			man_tab_today_gender.setImageResource(R.drawable.sex_male);
		else if(user.getGender().equals("female"))
			man_tab_today_gender.setImageResource(R.drawable.sex_female);
		else
			man_tab_today_gender.setImageResource(R.drawable.sex_unknown);
		// ����
		TextView man_tab_today_weight = (TextView) findViewById(R.id.man_tab_today_weight);
		if(bmiModel.isWeightRecorded())
			man_tab_today_weight.setText(new DecimalFormat("#.#").format(bmiModel.getWeight()));
		else
			man_tab_today_weight.setText(getResources().getString(R.string.unknown));
		
		// ���
		TextView man_tab_today_height = (TextView) findViewById(R.id.man_tab_today_height);
		if(bmiModel.isHeightRecorded())
			man_tab_today_height.setText(new DecimalFormat("#.#").format(bmiModel.getHeight()));
		else
			man_tab_today_height.setText(getResources().getString(R.string.unknown));
		
		// BMI
		TextView man_tab_today_bmi = (TextView) findViewById(R.id.man_tab_today_bmi);
		if(bmiModel.isHeightRecorded()&&bmiModel.isWeightRecorded())
			man_tab_today_bmi.setText(new DecimalFormat("#.#").format(bmiModel.getBMI()));
		else
			man_tab_today_bmi.setText(getResources().getString(R.string.unknown));	
		
		// ����
		TextView man_tab_today_bmiMeasure = (TextView) findViewById(R.id.man_tab_today_bmiMeasure);
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
	}

	public void btn_weight_add(View v) { // �������
//		if(!bmiModel.isWeightRecorded())
		{
			Intent intent = new Intent(MainTabToday.this, NewRecordActivity.class);
			Bundle bl = new Bundle();
			bl.putInt("userid", user.getID());
			bl.putInt("item", ITEM.weight);
			bl.putInt("rowid", -1);
			bl.putDouble("value", bmiModel.getWeight());
			//��Bundle����Intent������һ��Activity
			intent.putExtras(bl);
			getParent().startActivityForResult(intent,ITEM.weight);
		}	
	}
	
	public void btn_height_add(View v) { // �������
//		if(!bmiModel.isHeightRecorded())
		{
			Intent intent = new Intent(MainTabToday.this, NewRecordActivity.class);
			Bundle bl = new Bundle();
			bl.putInt("userid", user.getID());
			bl.putInt("item", ITEM.height);
			bl.putDouble("value", bmiModel.getHeight());
			bl.putInt("rowid", -1);
			intent.putExtras(bl);
			getParent().startActivityForResult(intent,ITEM.height);
		}
		
	}
	
	public void btn_bloodTestHistory(View v) { // �������
		Intent intent = new Intent(MainTabToday.this, PEHistoryActivity.class);
		Bundle bl = new Bundle();
		bl.putInt("userid", user.getID());
		bl.putInt("item", ITEM.g_bloodTest);
		intent.putExtras(bl);
		startActivity(intent);			
	}
	
	
	public void btn_urineTestHistory(View v) { // �������
		Intent intent = new Intent(MainTabToday.this, PEHistoryActivity.class);
		Bundle bl = new Bundle();
		bl.putInt("userid", user.getID());
		bl.putInt("item", ITEM.g_urineTest);
		intent.putExtras(bl);
		startActivity(intent);			
	}
	
	public void btn_bloodPressureHistory(View v) { // �������
		Intent intent = new Intent(MainTabToday.this, PEHistoryActivity.class);
		Bundle bl = new Bundle();
		bl.putInt("userid", user.getID());
		bl.putInt("item", ITEM.g_bloodPressure);
		intent.putExtras(bl);
		startActivity(intent);			
	}
	
	private final SensorEventListener sensorEventListener = new SensorEventListener() {
		// ϵͳ���õ��������ٶȱ�׼ֵ���豸��ˮƽ��ֹ������¾ͳ������ѹ��������Ĭ��Y�᷽��ļ��ٶ�ֵΪSTANDARD_GRAVITY
		double calibration = SensorManager.STANDARD_GRAVITY;

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];

			// ������������ļ��ٶ�
			double a = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
					+ Math.pow(z, 2)));

			// ��ȥԭ�е����������ѹ��
			currentAcceleration = Math.abs((float) (a - calibration));
			Log.i("sensor", "\n currentAcceleration " + currentAcceleration);
			sensorManager.unregisterListener(sensorEventListener);
			refreshAccelerometer();
		}
	};
	
	private void refreshAccelerometer() {
		/*
		 * �Ƽ���һ��ˢ��UI�ķ��� Activity.runOnUiThread��Runnable�� ���µ��߳��и���UI
		 * Runnable��һ���ӿڣ���Ҫ��ʵ��run�����������TimerTask����ʵ��������ӿ�ͬ����Ҫʵ��run����
		 */
		runOnUiThread(new Runnable() {
			public void run() {
				String currentG = currentAcceleration
						/ SensorManager.STANDARD_GRAVITY + "Gs";
				// wv.loadUrl("javascript:setContentInfo('" + getRemoteData()
				// + "')");
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

				// Log.i("TEXT", "\n currentG " + currentG);
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
	
	public void onDisplayChange(View v)
	{
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.up_32), null);
		int y = tvTitle.getBottom() * 3 / 2;
		int x = getWindowManager().getDefaultDisplay().getWidth() / 4;
		showPopupWindow(x, y);
	}
	
	public void showPopupWindow(int x, int y) {
		llTitle = (LinearLayout) LayoutInflater.from(MainTabToday.this).inflate(
				R.layout.tab_today_dialog, null);
		lvTitle = (ListView) llTitle.findViewById(R.id.tab_today_dialog);
		lvTitle.setAdapter(new ArrayAdapter<String>(MainTabToday.this,
				R.layout.simple_text_item, R.id.simple_text_item, title));
		pwTitle = new PopupWindow(MainTabToday.this);
//		pwTitle.setBackgroundDrawable(new BitmapDrawable());
		pwTitle.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		pwTitle.setHeight(getWindowManager().getDefaultDisplay().getHeight() / 2);
		pwTitle.setOutsideTouchable(true);
		pwTitle.setFocusable(true);
		pwTitle.setContentView(llTitle);
		// showAsDropDown��������view��Ϊ���������Ҫ������Ļparent
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		pwTitle.showAtLocation(findViewById(R.id.main_tab_today), Gravity.LEFT
				| Gravity.TOP, x, y);//��Ҫָ��Gravity��Ĭ�������center.

		lvTitle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tvTitle.setText(title[arg2]);
				pwTitle.dismiss();
				pwTitle = null;
				tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_24), null);
			}
		});
	}

	
//	public boolean onKeyDown(int keyCode, KeyEvent event) {  
//        if (keyCode == KeyEvent.KEYCODE_BACK ) {  
//        	Log.d("keyMain", ""+ KeyEvent.KEYCODE_BACK);
//        	moveTaskToBack(true);  
//            return true;
//        }  
//        return super.onKeyDown(keyCode, event);  
//    }  
	
	private String getRemoteData() {
		try {
			double sumHour[] = rmrModel.getCurrentHourCosts(this);
			// double sumHour[] = rmrModel.test(this);
			// JSONObject object1 = new JSONObject();
			// object1.put("name", "��л��");
			// object1.put("color", "#1f7e92");
			JSONArray jadata = new JSONArray();
			for (int i = 0; i < 24; i++) {
				jadata.put(sumHour[i]);
			}
			// object1.put("value", jadata);
			// JSONArray jsonArray = new JSONArray();
			// jsonArray.put(object1);
			Log.i("json", "\n json " + jadata.toString());
			System.out.println(jadata.toString());
			return jadata.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public class WebAppInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		WebAppInterface(Context c) {
			mContext = c;
		}

		public String getHourData(String toast) {
//			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
			return getRemoteData();
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode)
		{
		case RESULT_OK:
			//��ȡBundle������
			Bundle bl= data.getExtras();
			String value=bl.getString("value");Log.d("res",value);
			switch(requestCode){
			case ITEM.weight:
				TextView textView_weight = (TextView) findViewById(R.id.man_tab_today_weight);
				textView_weight.setText(value);Log.d("weight",value);
				break;
			case ITEM.height:
				TextView textView_height = (TextView) findViewById(R.id.man_tab_today_height);
				textView_height.setText(value);
				break;
			}
			break;
		default:
			break;
		}
	}
	

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("listener","listener");onActivityResult(requestCode, resultCode, data);		
	}

}
