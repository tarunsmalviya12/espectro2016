package in.mbm.espectro.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.mbm.espectro.fragment.ContactUs;
import in.mbm.espectro.fragment.Event;
import in.mbm.espectro.fragment.Notification;
import in.mbm.espectro.fragment.Profile;
import in.mbm.espectro.fragment.Sponsor;

/**
 * Created by tarunsmalviya12 on 7/7/2015.
 */
public class HomeAdapter extends FragmentStatePagerAdapter {

    /**
     * Variables.
     */
    int numberOfViewPagerChildren = 5;

    public HomeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return Profile.newInstance(String.valueOf(i));
            case 1:
                return Event.newInstance(String.valueOf(i));
            case 2:
                return Notification.newInstance(String.valueOf(i));
            case 3:
                return Sponsor.newInstance(String.valueOf(i));
            case 4:
                return ContactUs.newInstance(String.valueOf(i));
        }
        return null;
    }

    @Override
    public int getCount() {
        return numberOfViewPagerChildren;
    }
}
