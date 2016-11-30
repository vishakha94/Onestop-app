package com.example.android.onestop.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Zixiao on 11/27/2016.
 */

public class CategoriesFragment extends Fragment {
    private ExpandableListAdapter adapter;
    private ExpandableListView categoryExpListView;
    private List<String> mCategoriesList;
    private HashMap<String, List<String>> categoryToEventsMap;
    private DataBaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // database connect
        dbHelper = new DataBaseHelper(getActivity().getApplicationContext());

        // date setup
        mCategoriesList = Arrays.asList(getResources().getStringArray(R.array.categories_array));
        categoryToEventsMap = new HashMap<String, List<String>>();
        for (String category : mCategoriesList) {
            List<String> tmp = new ArrayList<>();
            List<OneStopEvent> events = dbHelper.getAllEventsByCategory(category);
            for (OneStopEvent e : events) tmp.add(e.getEvent_name());
            categoryToEventsMap.put(category, tmp);
        }

        // Categories List stuff handling
        categoryExpListView = (ExpandableListView) view.findViewById(R.id.categories);
        adapter = new ExpandableListAdapter(getActivity(),
                mCategoriesList,
                categoryToEventsMap);
        categoryExpListView.setAdapter(adapter);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
