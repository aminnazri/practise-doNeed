package com.example.practisedoneed.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.practisedoneed.MainActivity;
import com.example.practisedoneed.ProfileSetting;
import com.example.practisedoneed.R;

import com.example.practisedoneed.homePage;
import com.example.practisedoneed.test;
import com.example.practisedoneed.ui.dashboard.DashboardViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class profileFragment extends Fragment {

    Intent intent;
    private Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        intent = new Intent(getActivity(), test.class);
        final Button button = (Button) rootView.findViewById(R.id.setting_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        logout = (Button) rootView.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        return rootView;


    }
}