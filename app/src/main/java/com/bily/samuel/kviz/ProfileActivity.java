package com.bily.samuel.kviz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bily.samuel.kviz.lib.database.DatabaseHelper;
import com.bily.samuel.kviz.lib.database.User;

public class ProfileActivity extends AppCompatActivity {

    private FragmentManager fm;
    private DatabaseHelper db;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new DatabaseHelper(getApplicationContext());
        User user = db.getUser();
        name = user.getName();
    }

    public void onChangeNameButtonClicked(View view){
        fm = getSupportFragmentManager();
        ChangeName changeName = new ChangeName();
        changeName.show(fm, "Zmena mena");
    }

    public void onChangePassButtonClicked(View view){
        fm = getSupportFragmentManager();
        ChangePass changePass = new ChangePass();
        changePass.show(fm, "Zmena hesla");
    }

    public void onAboutButtonClicked(View view){
        Intent i = new Intent(getApplicationContext(),AboutActivity.class);
        startActivity(i);
    }

    public void onLogoutButtonClicked(View view){
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.dropTable();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(i);
    }

    @SuppressLint("ValidFragment")
    public class ChangeName extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_change_name, container, false);
            getDialog().setTitle("Zmena mena");
            EditText editText = (EditText)rootView.findViewById(R.id.fragName);
            editText.setText(name);
            Button button = (Button)rootView.findViewById(R.id.saveName);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });
            return rootView;
        }
    }

    @SuppressLint("ValidFragment")
    public class ChangePass extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
            getDialog().setTitle("Zmena hesla");
            Button button = (Button)rootView.findViewById(R.id.savePass);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return rootView;
        }
    }
}
