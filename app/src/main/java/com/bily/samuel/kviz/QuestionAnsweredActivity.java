package com.bily.samuel.kviz;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bily.samuel.kviz.lib.JSONParser;
import com.bily.samuel.kviz.lib.database.Answer;
import com.bily.samuel.kviz.lib.database.DatabaseHelper;
import com.bily.samuel.kviz.lib.database.Option;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionAnsweredActivity extends AppCompatActivity {

    private DatabaseHelper db;

    private int questionId;
    private int testId;
    private int right;

    private TextView option0;
    private TextView option1;
    private TextView option2;
    private TextView option3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answered);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5904250473227915~5288732783");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("BC4B821966D64C77880972C0F5467548")
                .build();
        mAdView.loadAd(adRequest);

        db = new DatabaseHelper(getApplicationContext());

        Intent i = getIntent();
        questionId = i.getIntExtra("id", 0);
        testId = i.getIntExtra("testId",0);
        String questionText = i.getStringExtra("text");
        right = i.getIntExtra("isRight",0);

        TextView title = (TextView)findViewById(R.id.title);
        title.setText(questionText);

        option0 = (TextView)findViewById(R.id.option0);
        option1 = (TextView)findViewById(R.id.option1);
        option2 = (TextView)findViewById(R.id.option2);
        option3 = (TextView)findViewById(R.id.option3);

        setText();
        setStyle();
        new GetOptions().execute();

    }

    public void setStyle(){
        try {
            Answer answer = db.getAnswer(questionId);
            int idO = answer.getIdO();
            Option option = db.getOption(questionId, idO);
            int type = option.getType();
            if(right<1) {
                int rightType = db.getQuestionType(questionId);
                switch (type) {
                    case 0:
                        option0.setTextColor(getResources().getColor(R.color.colorPrimary));
                        break;
                    case 1:
                        option1.setTextColor(getResources().getColor(R.color.colorPrimary));
                        break;
                    case 2:
                        option2.setTextColor(getResources().getColor(R.color.colorPrimary));
                        break;
                    case 3:
                        option3.setTextColor(getResources().getColor(R.color.colorPrimary));
                        break;
                }
                switch (rightType) {
                    case 0:
                        option0.setTextColor(getResources().getColor(R.color.colorRight));
                        break;
                    case 1:
                        option1.setTextColor(getResources().getColor(R.color.colorRight));
                        break;
                    case 2:
                        option2.setTextColor(getResources().getColor(R.color.colorRight));
                        break;
                    case 3:
                        option3.setTextColor(getResources().getColor(R.color.colorRight));
                        break;
                }
            }else{
                switch (type) {
                    case 0:
                        option0.setTextColor(getResources().getColor(R.color.colorRight));
                        break;
                    case 1:
                        option1.setTextColor(getResources().getColor(R.color.colorRight));
                        break;
                    case 2:
                        option2.setTextColor(getResources().getColor(R.color.colorRight));
                        break;
                    case 3:
                        option3.setTextColor(getResources().getColor(R.color.colorRight));
                        break;
                }
            }

        }catch(NullPointerException e) {
            Log.e("NullPointerException", e.toString());
        }
    }

    public void setText(){
        try {
            ArrayList<HashMap<String,String>> options = db.getOptions(questionId);
            for(int i = 0;i<options.size();i++) {
                HashMap<String,String> option = options.get(i);
                String stype = option.get("type");
                int type = Integer.parseInt(stype);
                String text = option.get("name");
                switch (type) {
                    case 0:
                        option0.setText(text);
                        break;
                    case 1:
                        option1.setText(text);
                        break;
                    case 2:
                        option2.setText(text);
                        break;
                    case 3:
                        option3.setText(text);
                        break;
                }
            }
        }catch(CursorIndexOutOfBoundsException e){
            e.printStackTrace();
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
                    db.updateQuestionType(questionId, jsonObject.getInt("rightType"));
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RelativeLayout layout = (RelativeLayout) findViewById(R.id.questionLayoutA);
                            Snackbar.make(layout, "Ste offline.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setText();
                    setStyle();
                }
            });

            return null;
        }

    }
}
