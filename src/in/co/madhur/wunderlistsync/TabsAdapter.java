package in.co.madhur.wunderlistsync;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * This is a helper class that implements the management of tabs and all
 * details of connecting a ViewPager with associated TabHost.  It relies on a
 * trick.  Normally a tab host has a simple API for supplying a View or
 * Intent that each tab will show.  This is not sufficient for switching
 * between pages.  So instead we make the content part of the tab host
 * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
 * view to show as the tab content.  It listens to changes in tabs, and takes
 * care of switch to the correct paged in the ViewPager whenever the selected
 * tab changes.
 */
@SuppressLint("NewApi")
public class TabsAdapter extends FragmentPagerAdapter
{

	public TabsAdapter(FragmentManager fm)
	{
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}
       
}
