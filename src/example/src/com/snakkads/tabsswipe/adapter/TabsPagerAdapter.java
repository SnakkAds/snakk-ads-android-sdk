package com.snakkads.tabsswipe.adapter;

import com.snakkads.tabsswipe.BannerAdPromptFragment;
import com.snakkads.tabsswipe.BannerInterstitialFragment;
import com.snakkads.tabsswipe.BannerRichMediaFragment;
import com.snakkads.tabsswipe.BannerStandardMediaFragment;
import com.snakkads.tabsswipe.BannerVideoFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
        Fragment fragment = null;
		switch (index) {
		case 0:
			// Standard fragment activity
            fragment = new BannerStandardMediaFragment();
            break;
		case 1:
			// Rich Media fragment activity
            fragment = new BannerRichMediaFragment();
            break;
		case 2:
			// Interstitial fragment activity
            fragment = new BannerInterstitialFragment();
            break;
        case 3:
            // Ad prompt fragment activity
            fragment = new BannerAdPromptFragment();
            break;
        case 4:
            // Video fragment activity
            fragment = new BannerVideoFragment();
            break;

		}
		return fragment;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 5;
	}

}
