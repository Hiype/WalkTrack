package com.hiype.walktrack.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hiype.walktrack.fragments.FriendsAddFragment;
import com.hiype.walktrack.fragments.FriendsListFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new FriendsListFragment();

            case 1:
                return new FriendsAddFragment();

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
