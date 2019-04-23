package com.example.apgrate.screens.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apgrate.R;
import com.example.apgrate.helper.RVTestsAdapter;
import com.example.apgrate.model.Test;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class TestsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RVTestsAdapter mAdapter;
    private MainViewModel mViewModel;
    private ArrayList<Test> mTests;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tests, container, false);

        mRecyclerView = v.findViewById(R.id.rv_tests);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RVTestsAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        setObservers();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    private void setObservers() {
        mViewModel.getTests().removeObservers(this);
        mViewModel.getTests().observe(this, tests -> {
            mTests = tests;
            mAdapter.updateData(tests);
        });
    }

}