package com.example.apgrate.screens.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apgrate.R;
import com.example.apgrate.helper.RVTestsAdapter;
import com.example.apgrate.screens.test.TestActivity;
import com.example.apgrate.utils.Tester;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TestsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RVTestsAdapter mAdapter;
    private MainViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tests, container, false);

        mRecyclerView = v.findViewById(R.id.rv_tests);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RVTestsAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        setObservers();
    }

    private void setObservers() {
        mViewModel.getTests().observe(this, tests -> {
            mAdapter.updateData(tests);
        });
    }
}