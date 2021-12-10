package com.valorain.playtogether.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.valorain.playtogether.Model.SliderItem;
import com.valorain.playtogether.R;
import com.valorain.playtogether.View.MainActivity;
import com.valorain.playtogether.adapter.SliderAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import io.paperdb.Paper;


public class SettingsFragment extends Fragment {


    private View view;
    private ViewPager2 viewPager2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_settings, container, false);

         viewPager2 = view.findViewById(R.id.viewPagerImageSlider);

         //Here, i'm preparing list of images from drawable,
        //  You can get it from API as well.
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.dialog_csgo));
        sliderItems.add(new SliderItem(R.drawable.dialog_dota2));
        sliderItems.add(new SliderItem(R.drawable.dialog_lol));
        sliderItems.add(new SliderItem(R.drawable.dialog_other));
        sliderItems.add(new SliderItem(R.drawable.dialog_pubg));
        sliderItems.add(new SliderItem(R.drawable.dialog_valorant));

    viewPager2.setAdapter(new SliderAdapter(sliderItems,viewPager2));
    viewPager2.setClipToPadding(false);
    viewPager2.setClipChildren(false);
    viewPager2.setOffscreenPageLimit(4);
    viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r  * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);

    return view;
    }


}