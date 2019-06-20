package com.example.andrew.chatsystem;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                return new ChatsFragment();

            case 1:
                return new RecommendationsFragment();

            case 2:
                return new DoctorsContactsFragment();

            case 3:
                return new RequestsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Chats" ;

            case 1:
                return "Recoms" ;

            case 2:
                return "Contacts" ;

            case 3:
                return "Requests";

            default:
                return null;
        }
    }
}
