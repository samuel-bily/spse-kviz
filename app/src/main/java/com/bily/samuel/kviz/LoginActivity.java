package com.bily.samuel.kviz;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bily.samuel.kviz.lib.JSONParser;
import com.bily.samuel.kviz.lib.database.DatabaseHelper;
import com.bily.samuel.kviz.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private String name;
    private String email;
    private ActionProcessButton buttonRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            getSupportActionBar().hide();
        }catch(NullPointerException e){

        }

        buttonRegister = (ActionProcessButton)findViewById(R.id.btnLogIn);
        buttonRegister.setMode(ActionProcessButton.Mode.ENDLESS);
        buttonRegister.setColorScheme(getResources().getColor(R.color.colorLigth),getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorDark));
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRegister.setProgress(50);
                EditText editName = (EditText) findViewById(R.id.editPass);
                EditText editEmail = (EditText) findViewById(R.id.editEmail);
                name = editName.getText().toString();
                email = editEmail.getText().toString();
                Login login = new Login();
                login.execute();
            }
        });
    }

    public void onRegisterButtonClicked(View view){
        Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(i);
        finish();
    }

    class Login extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            JSONParser parser = new JSONParser();
            HashMap<String,String> values = new HashMap<>();
            values.put("pass", name);
            values.put("email", email);
            values.put("tag", "login");
            try {
                JSONObject jsonObject = parser.makePostCall(values);
                int success = jsonObject.getInt("success");
                if(success == 1){
                    JSONObject jUser = jsonObject.getJSONObject("user");
                    User user = new User();
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    user.setId_u(jUser.getInt("id"));
                    user.setEmail(jUser.getString("email"));
                    user.setName(jUser.getString("name"));
                    user.setGender(jUser.getInt("gender"));
                    db.storeUser(user);

                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    final String message = jsonObject.getString("message");
                    if(!jsonObject.getString("message").isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLogin);
                                Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
                                buttonRegister.setProgress(0);
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RelativeLayout layout = (RelativeLayout) findViewById(R.id.registerRelative);
                                Snackbar.make(layout, "Something went wrong.", Snackbar.LENGTH_LONG).show();
                                buttonRegister.setProgress(0);
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout layout = (RelativeLayout) findViewById(R.id.registerRelative);
                        Snackbar.make(layout, "Something went wrong.", Snackbar.LENGTH_LONG).show();
                        buttonRegister.setProgress(0);
                    }
                });
            }
            return null;
        }
    }
}
