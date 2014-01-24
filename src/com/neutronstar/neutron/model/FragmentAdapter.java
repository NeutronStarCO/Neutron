package com.neutronstar.neutron.model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.neutronstar.neutron.MainNeutron1;
import com.neutronstar.neutron.MainTabFamilyFragment;
import com.neutronstar.neutron.MainTabTodayFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
	
	public final static int TAB_COUNT = 2;

	public FragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int id) {
		switch (id) {
		case MainNeutron1.TAB_TODAY:
			MainTabTodayFragment todayFragment = new MainTabTodayFragment();
			return todayFragment;
		case MainNeutron1.TAB_FAMILY:
			MainTabFamilyFragment familyFragment = new MainTabFamilyFragment();
			return familyFragment;
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
