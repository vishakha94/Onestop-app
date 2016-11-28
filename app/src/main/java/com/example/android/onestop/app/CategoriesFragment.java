package com.example.android.onestop.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Zixiao on 11/27/2016.
 */

public class CategoriesFragment extends Fragment {
    private ListView categoryListView;
    private String[] mCategoriesList;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle navigation view item clicks here.
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        mCategoriesList = getResources().getStringArray(R.array.categories_array);

        // Categories List stuff handling
        categoryListView = (ListView) view.findViewById(R.id.categories);
        adapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_categories,
                R.id.list_item_categories_textview,
                mCategoriesList);
        categoryListView.setAdapter(adapter);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
