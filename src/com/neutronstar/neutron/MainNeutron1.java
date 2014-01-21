package com.neutronstar.neutron;

import com.neutronstar.neutron.NeutronContract.ITEM;
import com.neutronstar.neutron.model.FragmentAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

public class MainNeutron1 extends FragmentActivity implements OnClickListener{
	
	public static final int TAB_TODAY = 0;
	public static final int TAB_FAMILY = 1;
	public static final int TAB_MORE = 2;
	public static final int TAB_SETTINGS = 3;
	
	private ViewPager viewPager;
	private RadioButton rb_today, rb_family, rb_more, rb_settings;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_neutron1);
		
		viewPager = (ViewPager) findViewById(R.id.main_view_pager);
		rb_today = (RadioButton) findViewById(R.id.main_tab_rb_today);
		rb_family = (RadioButton) findViewById(R.id.main_tab_rb_family);
		rb_more = (RadioButton) findViewById(R.id.main_tab_rb_more);
		rb_settings = (RadioButton) findViewById(R.id.main_tab_rb_settings);
		rb_today.setOnClickListener(this);
		rb_family.setOnClickListener(this);
		rb_more.setOnClickListener(this);
		rb_settings.setOnClickListener(this);
		
		FragmentAdapter adapter = new FragmentAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			@Override
			public void onPageSelected(int id) {
				switch (id) {
				case TAB_TODAY:
					rb_today.setChecked(true);
					break;
				case TAB_FAMILY:
					rb_family.setChecked(true);
					break;
				case TAB_MORE:
					rb_more.setChecked(true);
					break;
				case TAB_SETTINGS:
					rb_settings.setChecked(true);
					break;
				default:
					break;
				}
			}
		});
			
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_tab_today:
			viewPager.setCurrentItem(TAB_TODAY);
			break;
/*		case R.id.main_tab_catagory:
			viewPager.setCurrentItem(TAB_FAMILY);
			break;
		case R.id.main_tab_car:
			viewPager.setCurrentItem(TAB_MORE);
			break;
		case R.id.main_tab_buy:
			viewPager.setCurrentItem(TAB_SETTINGS);
			break;
*/		default:
			break;
		}			
	}
	
}
