package com.bily.samuel.kviz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bily.samuel.kviz.lib.JSONParser;
import com.bily.samuel.kviz.lib.database.DatabaseHelper;
import com.bily.samuel.kviz.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String pass;
    private FragmentManager fm;
    private ActionProcessButton buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        buttonRegister = (ActionProcessButton)findViewById(R.id.btnSignIn);
        buttonRegister.setMode(ActionProcessButton.Mode.ENDLESS);
        buttonRegister.setColorScheme(getResources().getColor(R.color.colorLigth), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorDark));
        buttonRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
                if(checkBox.isChecked()) {
                    EditText editName = (EditText) findViewById(R.id.regName);
                    EditText editEmail = (EditText) findViewById(R.id.regEmail);
                    EditText editPass = (EditText) findViewById(R.id.regPass);
                    name = editName.getText().toString();
                    email = editEmail.getText().toString();
                    pass = editPass.getText().toString();
                    if (!name.matches("") && !email.matches("") && !pass.matches("")) {
                        Registration registration = new Registration();
                        registration.execute();
                        buttonRegister.setProgress(50);
                    } else {
                        RelativeLayout layout = (RelativeLayout) findViewById(R.id.registerRelative);
                        Snackbar.make(layout, "Please enter your details.", Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    Snackbar.make(findViewById(android.R.id.content), "You need to accept privacy policy.", Snackbar.LENGTH_LONG)
                            .setAction("ACCEPT", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkBox.setChecked(true);
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorLigth)).show();
                }
            }
        });
    }

    public void showToast(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void onLoginButtonClicked(View view){
        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void onPolicyButton(View view){
        fm = getSupportFragmentManager();
        dialogFragment dFragment = new dialogFragment();
        dFragment.show(fm, "Policy");
    }

    @SuppressLint("ValidFragment")
    public class dialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_policy, container, false);
            getDialog().setTitle("Privacy policy");
            return rootView;
        }
    }

    private class Registration extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            JSONParser parser = new JSONParser();
            HashMap<String,String> values = new HashMap<>();
            values.put("name", name);
            values.put("email",email);
            values.put("pass",pass);
            //values.put("gender", String.valueOf(gender));
            values.put("tag", "register");
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
                                buttonRegister.setProgress(0);
                                showToast(message);
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buttonRegister.setProgress(0);
                                showToast("Ste offline");
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonRegister.setProgress(0);
                        showToast("Ste offline");
                    }
                });
            }
            return null;
        }
    }
}
