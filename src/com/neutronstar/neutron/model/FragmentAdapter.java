package com.neutronstar.neutron.model;

import com.neutronstar.neutron.MainNeutron1;
import com.neutronstar.neutron.MainTabTodayFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {
	
	public final static int TAB_COUNT = 1;
	MainTabTodayFragment todayFragment;

	public FragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int id) {
		switch (id) {
		case MainNeutron1.TAB_TODAY:
			todayFragment = new MainTabTodayFragment();
			return todayFragment;
//		case MainNeutron1.TAB_FAMILY:
////			MainTabTodayFragment todayFragment = new MainTabTodayFragment();
//			return todayFragment;
//		case MainNeutron1.TAB_MORE:
////			MainTabTodayFragment todayFragment = new MainTabTodayFragment();
//			return todayFragment;
//		case MainNeutron1.TAB_SETTINGS:
////			MainTabTodayFragment todayFragment = new MainTabTodayFragment();
//			return todayFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return TAB_COUNT;
	}

}
