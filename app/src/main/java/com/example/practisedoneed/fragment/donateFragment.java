package com.example.practisedoneed.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.practisedoneed.R;
import com.example.practisedoneed.homePage;
//import com.example.practisedoneed.databinding.FragmentNotificationsBinding;
import com.example.practisedoneed.ui.notifications.NotificationsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class donateFragment extends Fragment {

    private Toolbar toolbar;
    private TextView donateTitle;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate,container,false);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) view.findViewById(R.id.app_toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        assert appCompatActivity != null;
        appCompatActivity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        donateTitle = toolbar.findViewById(R.id.text_Donate);
        donateTitle.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.donate_toolbar, menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}