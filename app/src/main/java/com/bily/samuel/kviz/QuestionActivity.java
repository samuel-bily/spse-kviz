package com.bily.samuel.kviz;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bily.samuel.kviz.lib.JSONParser;
import com.bily.samuel.kviz.lib.database.Answer;
import com.bily.samuel.kviz.lib.database.DatabaseHelper;
import com.bily.samuel.kviz.lib.database.Option;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionActivity extends AppCompatActivity {

    private int questionId;
    private int optionId;
    private int testId;
    private DatabaseHelper db;

    private ActionProcessButton saveButton;

    private RadioButton radioButton0;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        db = new DatabaseHelper(getApplicationContext());

        radioButton0 = (RadioButton)findViewById(R.id.radioButton0);
        radioButton1 = (RadioButton)findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton)findViewById(R.id.radioButton3);

        Intent i = getIntent();
        questionId = i.getIntExtra("id", 0);
        testId = i.getIntExtra("testId",0);
        String questionText = i.getStringExtra("text");

        TextView title = (TextView)findViewById(R.id.titleView);
        title.setText(questionText);
        setRadioButtonText();
        new GetOptions().execute();

        saveButton = (ActionProcessButton)findViewById(R.id.btnSave);
        saveButton.setMode(ActionProcessButton.Mode.ENDLESS);
        saveButton.setColorScheme(getResources().getColor(R.color.colorLigth), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorDark));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnswer(optionId);
                finish();
            }
        });
        setClick();
    }

    public void showToast(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        @SuppressWarnings("ConstantConditions") int Y = getSupportActionBar().getHeight();
        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,Y);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void setRadioButtonText(){
        try {
            ArrayList<HashMap<String,String>> options = db.getOptions(questionId);
            for(int i = 0;i<options.size();i++) {
                HashMap<String,String> option = options.get(i);
                String stype = option.get("type");
                int type = Integer.parseInt(stype);
                String text = option.get("name");
                switch (type) {
                    case 0:
                        radioButton0.setText(text);
                        break;
                    case 1:
                        radioButton1.setText(text);
                        break;
                    case 2:
                        radioButton2.setText(text);
                        break;
                    case 3:
                        radioButton3.setText(text);
                        break;
                }
            }
        }catch(CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioButton0:
                if (checked)
                    optionId = 0;
                break;
            case R.id.radioButton1:
                if (checked)
                    optionId = 1;
                break;
            case R.id.radioButton2:
                if (checked)
                    optionId = 2;
                break;
            case R.id.radioButton3:
                if (checked)
                    optionId = 3;
                break;
        }
    }

    public void setAnswer(int type){
        db.storeAnswer(db.setAnswer(testId, questionId, type));
    }

    public void setClick(){
        try {
            Answer answer = db.getAnswer(questionId);
            int idO = answer.getIdO();
            Option option = db.getOption(questionId, idO);
            int type = option.getType();
            switch (type) {
                case 0:
                    radioButton0.setChecked(true);
                    break;
                case 1:
                    radioButton1.setChecked(true);
                    break;
                case 2:
                    radioButton2.setChecked(true);
                    break;
                case 3:
                    radioButton3.setChecked(true);
                    break;
            }
            optionId = type;
        }catch(NullPointerException e){
            Log.e("NullPointerException", e.toString());
        }
    }

    private class GetOptions extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String,String> values = new HashMap<>();
            values.put("tag","getOptions");
            values.put("question",""+questionId);
            try {
                JSONObject jsonObject = jsonParser.makePostCall(values);
                Log.e("Gettin",jsonObject.toString());
                if(jsonObject.getInt("success")==1){
                    JSONArray options = jsonObject.getJSONArray("options");
                    for(int i = 0; i < options.length();i++) {
                        JSONObject JOption = options.getJSONObject(i);
                        Option option = new Option();
                        option.setIdO(JOption.getInt("id"));
                        option.setIdQ(questionId);
                        option.setType(JOption.getInt("type"));
                        option.setName(JOption.getString("name"));
                        db.storeOption(option);
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Ste offline");
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setRadioButtonText();
                }
            });

            return null;
        }

    }

}
