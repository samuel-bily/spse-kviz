package com.bily.samuel.kviz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bily.samuel.kviz.lib.JSONParser;
import com.bily.samuel.kviz.lib.database.DatabaseHelper;
import com.bily.samuel.kviz.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private FragmentManager fm;
    private DatabaseHelper db;
    private ActionProcessButton button;

    private String name;
    private int idu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new DatabaseHelper(getApplicationContext());
        User user = db.getUser();
        name = user.getName();
        idu = user.getId_u();
        TextView textView = (TextView)findViewById(R.id.settingsName);
        textView.setText(name);
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
            button = (ActionProcessButton)rootView.findViewById(R.id.btnLogIn);
            button.setMode(ActionProcessButton.Mode.ENDLESS);
            button.setColorScheme(getResources().getColor(R.color.colorLigth), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorDark));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button.setProgress(50);
                    final EditText editName = (EditText)rootView.findViewById(R.id.fragName);
                    name = editName.getText().toString();
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            JSONParser jsonParser = new JSONParser();
                            HashMap<String, String> values = new HashMap<>();
                            values.put("tag","changeName");
                            values.put("name", name);
                            values.put("idu", "" + idu);
                            try {
                                JSONObject jsonObject = jsonParser.makePostCall(values);
                                if (jsonObject.getInt("success") == 1) {
                                    db.changeName(name);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(rootView,"Ste offline.",Snackbar.LENGTH_SHORT);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                    button.setProgress(0);
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
            button = (ActionProcessButton)rootView.findViewById(R.id.btnLogIn);
            button.setMode(ActionProcessButton.Mode.ENDLESS);
            button.setColorScheme(getResources().getColor(R.color.colorLigth), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorDark));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView textView = (TextView) rootView.findViewById(R.id.errorMsg);
                    textView.setVisibility(View.GONE);
                    button.setProgress(50);
                    final EditText passNow = (EditText) rootView.findViewById(R.id.passNow);
                    final EditText newPass = (EditText) rootView.findViewById(R.id.newPass);
                    final EditText newPass02 = (EditText) rootView.findViewById(R.id.newPass02);

                    final String oldPass = passNow.getText().toString();
                    final String newpass = newPass.getText().toString();
                    String newpass02 = newPass02.getText().toString();

                    if (newpass.equals(newpass02)) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                JSONParser jsonParser = new JSONParser();
                                HashMap<String, String> values = new HashMap<>();
                                values.put("tag", "changePass");
                                values.put("oldPass", oldPass);
                                values.put("newPass", newpass);
                                values.put("idu", "" + idu);
                                try {
                                    JSONObject jsonObject = jsonParser.makePostCall(values);
                                    Log.e("Gettin", jsonObject.toString());
                                    if (jsonObject.getInt("success") == 1) {
                                        db.changeName(name);
                                        finish();
                                    } else if (!jsonObject.getString("message").isEmpty()) {
                                        final String message = jsonObject.getString("message");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textView.setText(message);
                                                textView.setVisibility(View.VISIBLE);
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(rootView, "Ste offline.", Snackbar.LENGTH_SHORT);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();

                    } else {
                        textView.setText("Nové heslá sa musia zhodovať!");
                        textView.setVisibility(View.VISIBLE);
                    }
                    button.setProgress(0);
                }
            });
            return rootView;
        }
    }
}
