package com.neutronstar.neutron;

import java.util.ArrayList;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MainNeutron extends Activity  {

	public static MainNeutron instance = null;

	private ViewPager mTabPager;
	private ImageView mTabImg;// ����ͼƬ
	private LinearLayout layout1, layout2, layout3, layout4;
	private ImageView mTab1, mTab2, mTab3, mTab4;
	private int zero = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int one;// ����ˮƽ����λ��
	private int two;
	private int three;
	private LinearLayout mClose;
	private LinearLayout mCloseBtn;
	private View layout;
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;
	private Intent serviceIntent;
//	private View view_tab_today;
	private LocalActivityManager manager = null;
	

	// private Button mRightBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_neutron);
		// ����activityʱ���Զ����������
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		instance = this;
		manager = new LocalActivityManager(this , true);
        manager.dispatchCreate(savedInstanceState);
		/*
		 * mRightBtn = (Button) findViewById(R.id.right_btn);
		 * mRightBtn.setOnClickListener(new Button.OnClickListener() { @Override
		 * public void onClick(View v) { showPopupWindow
		 * (MainWeixin.this,mRightBtn); } });
		 */
		// Start the service for (1)G force record, (2)....
		serviceIntent = new Intent(this, NeutronService.class);
		startService(serviceIntent);

		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		layout1 = (LinearLayout)findViewById(R.id.layout_today);
		layout2 = (LinearLayout)findViewById(R.id.layout_family);
		layout3 = (LinearLayout)findViewById(R.id.layout3);
		layout4 = (LinearLayout)findViewById(R.id.layout4);
		mTab1 = (ImageView) findViewById(R.id.img_weixin);
		mTab2 = (ImageView) findViewById(R.id.img_address);
		mTab3 = (ImageView) findViewById(R.id.img_friends);
		mTab4 = (ImageView) findViewById(R.id.img_settings);
		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		layout1.setOnClickListener(new MyOnClickListener(0));
		layout2.setOnClickListener(new MyOnClickListener(1));
		layout3.setOnClickListener(new MyOnClickListener(2));
		layout4.setOnClickListener(new MyOnClickListener(3));
		Display currDisplay = getWindowManager().getDefaultDisplay();// ��ȡ��Ļ��ǰ�ֱ���
		int displayWidth = currDisplay.getWidth();
		int displayHeight = currDisplay.getHeight();
		one = displayWidth / 4; // ����ˮƽ����ƽ�ƴ�С
		two = one * 2;
		three = one * 3;
		// Log.i("info", "��ȡ����Ļ�ֱ���Ϊ" + one + two + three + "X" + displayHeight);

		// InitImageView();//ʹ�ö���
		// ��Ҫ��ҳ��ʾ��Viewװ��������
		LayoutInflater mLi = LayoutInflater.from(this);
//		View view_tab_today = mLi.inflate(R.layout.main_tab_today, null);
//		View view2 = mLi.inflate(R.layout.main_tab_family, null);
		View view3 = mLi.inflate(R.layout.main_tab_friends, null);
		View view4 = mLi.inflate(R.layout.main_tab_settings, null);

		// ÿ��ҳ���view����
		final ArrayList<View> views = new ArrayList<View>();
//		views.add(view_tab_today);
		Intent intent = this.getIntent();
		Log.d("-----", "" + intent.getExtras().getInt("userid"));
		intent.setClass(this, MainTabToday.class);
		Log.d("-----", "" + intent.getExtras().getInt("userid"));
		views.add(manager.startActivity("MainTabToday", intent).getDecorView());
//		views.add(manager.startActivity("MainTabToday", new Intent(this, MainTabToday.class)).getDecorView());
		views.add(manager.startActivity("MainTabFamily", new Intent(this, MainTabFamily.class)).getDecorView());
//		views.add(view2);
		views.add(view3);
		views.add(view4);
		// ���ViewPager������������
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			// @Override
			// public CharSequence getPageTitle(int position) {
			// return titles.get(position);
			// }

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};

		mTabPager.setAdapter(mPagerAdapter);
		mTabPager.setCurrentItem(0);
		manager.startActivity("MainTabToday", new Intent(this, MainTabToday.class)); 
	}

	

	

	public void onPause() {
		/*
		 * �ܹؼ��Ĳ��֣�ע�⣬˵���ĵ����ᵽ����ʹactivity���ɼ���ʱ�򣬸�Ӧ����Ȼ������Ĺ��������Ե�ʱ����Է��֣�û��������ˢ��Ƶ��
		 * Ҳ��ǳ��ߣ�����һ��Ҫ��onPause�����йرմ����������򽲺ķ��û������������ܲ�����
		 */
//		sensorManager.unregisterListener(sensorEventListener);
//		updateTimer.cancel();
		super.onPause();
	}

	public void onResume() {
		super.onResume();
//		updateTimer = new Timer("gForceUpdate");
/*		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sensorManager.registerListener(sensorEventListener,
						accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
			}
		}, 0, 5000);*/
	}

	/**
	 * ͷ��������
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
			switch(index)
			{
			case 0:
				manager.startActivity("MainTabToday", new Intent(instance, MainTabToday.class)).getDecorView();
				break;
			case 1:
				manager.startActivity("MainTabFamily", new Intent(instance, MainTabFamily.class)).getDecorView();
				break;
				
			}
		}
	};

	/*
	 * ҳ���л�����(ԭ����:D.Winter)
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_weixin_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_address_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_frd_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	

//	@SuppressWarnings("deprecation")
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // ��ȡ
//																				// back��
//
//			if (menu_display) { // ��� Menu�Ѿ��� ���ȹر�Menu
//				menuWindow.dismiss();
//				menu_display = false;
//			} else {
//				Intent intent = new Intent();
//				intent.setClass(MainNeutron.this, Exit.class);
//				startActivity(intent);
//			}
//		}
//
//		else if (keyCode == KeyEvent.KEYCODE_MENU) { // ��ȡ Menu��
//			if (!menu_display) {
//				// ��ȡLayoutInflaterʵ��
//				inflater = (LayoutInflater) this
//						.getSystemService(LAYOUT_INFLATER_SERVICE);
//				// �����main��������inflate�м����Ŷ����ǰ����ֱ��this.setContentView()�İɣ��Ǻ�
//				// �÷������ص���һ��View�Ķ����ǲ����еĸ�
//				layout = inflater.inflate(R.layout.main_menu, null);
//
//				// ��������Ҫ�����ˣ����������ҵ�layout���뵽PopupWindow���أ������ܼ�
//				menuWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT,
//						LayoutParams.WRAP_CONTENT); // ������������width��height
//				// menuWindow.showAsDropDown(layout); //���õ���Ч��
//				// menuWindow.showAsDropDown(null, 0, layout.getHeight());
//				menuWindow.showAtLocation(this.findViewById(R.id.mainweixin),
//						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
//				// ��λ�ȡ����main�еĿؼ��أ�Ҳ�ܼ�
//				mClose = (LinearLayout) layout.findViewById(R.id.menu_close);
//				mCloseBtn = (LinearLayout) layout
//						.findViewById(R.id.menu_close_btn);
//
//				// �����ÿһ��Layout���е����¼���ע��ɡ�����
//				// ���絥��ĳ��MenuItem��ʱ�����ı���ɫ�ı�
//				// ����׼����һЩ����ͼƬ������ɫ
//				mCloseBtn.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						// Toast.makeText(Main.this, "�˳�",
//						// Toast.LENGTH_LONG).show();
//						Intent intent = new Intent();
//						intent.setClass(MainNeutron.this, Exit.class);
//						startActivity(intent);
//						menuWindow.dismiss(); // ��Ӧ����¼�֮��ر�Menu
//					}
//				});
//				menu_display = true;
//			} else {
//				// �����ǰ�Ѿ�Ϊ��ʾ״̬������������
//				menuWindow.dismiss();
//				menu_display = false;
//			}
//
//			return false;
//		}
//		return false;
//	}

	// ���ñ������Ҳఴť������
	public void btnmainright(View v) {
		Intent intent = new Intent(MainNeutron.this, MainTopRightDialog.class);
		startActivity(intent);
		// Toast.makeText(getApplicationContext(), "����˹��ܰ�ť",
		// Toast.LENGTH_LONG).show();
	}

	public void startchat(View v) { // С�� �Ի�����
		Intent intent = new Intent(MainNeutron.this, ChatActivity.class);
		startActivity(intent);
		// Toast.makeText(getApplicationContext(), "��¼�ɹ�",
		// Toast.LENGTH_LONG).show();
	}

	public void exit_settings(View v) { // �˳� α���Ի��򡱣���ʵ��һ��activity
		Intent intent = new Intent(MainNeutron.this, ExitFromSettings.class);
		startActivity(intent);
	}

	public void btn_shake(View v) { // �ֻ�ҡһҡ
		Intent intent = new Intent(MainNeutron.this, ShakeActivity.class);
		startActivity(intent);
	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    @SuppressWarnings("deprecation")
		Activity subActivity = manager.getCurrentActivity();
	    if (subActivity instanceof OnTabActivityResultListener)
	    {
	        OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
	        listener.onTabActivityResult(requestCode, resultCode, data);  
	    }
	}
}
