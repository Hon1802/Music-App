package hcmute.edu.vn.mp3app.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import hcmute.edu.vn.mp3app.fragment.FavoriteFragment;
import hcmute.edu.vn.mp3app.fragment.HomeFragment;
import hcmute.edu.vn.mp3app.fragment.PlaylistsFragment;
import hcmute.edu.vn.mp3app.fragment.SongsFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SongsFragment();
            case 1:
                return new FavoriteFragment();
            case 2:
                return new PlaylistsFragment();
//            case 3:
//                return new SongsFragment();
            default:
                return new SongsFragment();
        }
    }
    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    public CharSequence getPageTitle(int position){
        String title="";
        switch (position){
            case 0:
                title ="Songs";
                break;
            case 1:
                title ="Favorite";
                break;
            case 2:
                title ="Playlists";
                break;
//            case 3:
//                title ="Songs";
//                break;
            default:
                title ="Songs";
                break;
        }
        return title;
    }


}
