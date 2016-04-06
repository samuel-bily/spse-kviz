package com.bily.samuel.kviz;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bily.samuel.kviz.lib.JSONParser;
import com.bily.samuel.kviz.lib.adapter.QuestionArrayAdapter;
import com.bily.samuel.kviz.lib.database.Answer;
import com.bily.samuel.kviz.lib.database.DatabaseHelper;
import com.bily.samuel.kviz.lib.database.Option;
import com.bily.samuel.kviz.lib.database.Question;
import com.bily.samuel.kviz.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private ActionProcessButton sendButton;

    private GetQuestions getQuestions;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseHelper db;

    private QuestionArrayAdapter questionsAdapter;

    private int optionId;
    private int testId;
    private User user;
    private int answered;
    private ArrayList<HashMap<String,String>> questionsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent i = getIntent();
        testId = i.getIntExtra("testId",0);
        setTitle(i.getStringExtra("test"));
        answered = i.getIntExtra("answered", 0);
        db = new DatabaseHelper(getApplicationContext());
        user = db.getUser();
        sendButton = (ActionProcessButton) findViewById(R.id.btnAnswer);
        getQuestions = new GetQuestions();
        getQuestions.execute();
        if(answered<1) {
            loadDataToListView();
            sendButton.setMode(ActionProcessButton.Mode.ENDLESS);
            sendButton.setColorScheme(getResources().getColor(R.color.colorLigth), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorDark));
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SendAnswers().execute();
                }
            });
        }else{
            sendButton.setVisibility(View.GONE);
            loadDataToListView();
        }


        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeQuiz);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorLigth, R.color.colorDark);
        swipeRefreshLayout.setRefreshing(true);
        new GetQuestions().execute();
        new GetAllOptions().execute();

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(450);
                } catch (InterruptedException e) {
                    Log.e("InterruptedException",e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                new GetAnswers().execute();
                new GetQuestions().execute();
            }
        }.execute();
    }

    @Override
    public void onRefresh() {
        getQuestions = new GetQuestions();
        getQuestions.execute();
    }

    public void loadDataToListView(){
        try {
            final ArrayList<Question> questions = db.getQuestions(testId);

            questionsAdapter = new QuestionArrayAdapter(getApplicationContext(),R.layout.list_row);
            for(int i = 0;i<questions.size();i++){
                Question question = questions.get(i);
                questionsAdapter.add(question);
            }
            ListView listView = (ListView) findViewById(R.id.listQuestions);
            //ListAdapter adapter = new SimpleAdapter(getApplicationContext(), questions, R.layout.list_row, new String[]{"name","id_q"}, new int[]{R.id.questionText,R.id.questionId});
            listView.setAdapter(questionsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Question question = questionsAdapter.getItem(position);
                    String text = question.getName();
                    String sId = "" + question.getId();
                    int questionId = Integer.valueOf(sId);
                    if(answered<1) {
                        Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
                        i.putExtra("text", text);
                        i.putExtra("id", questionId);
                        i.putExtra("testId", testId);
                        i.putExtra("answered", answered);
                        i.putExtra("isRight", question.getRight());
                        startActivity(i);
                    }else{
                        Intent i = new Intent(getApplicationContext(), QuestionAnsweredActivity.class);
                        i.putExtra("text", text);
                        i.putExtra("id", questionId);
                        i.putExtra("testId", testId);
                        i.putExtra("isRight", question.getRight());
                        startActivity(i);
                    }
                }
            });
        }catch(CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    private class GetAnswers extends  AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params){
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            JSONParser jsonParser = new JSONParser();
            HashMap<String,String> values = new HashMap<>();
            values.put("tag","getAnswers");
            values.put("test",""+testId);
            values.put("idu","" + user.getId_u());
            try {
                JSONObject jsonObject = jsonParser.makePostCall(values);
                if(jsonObject.getInt("success")==1){
                    JSONArray answers = jsonObject.getJSONArray("answers");
                    for(int i = 0; i < answers.length();i++){
                        JSONObject jsonObjectQ = answers.getJSONObject(i);
                        //Log.e("GETTIN Quesion",jsonObjectQ.toString());
                        Answer answer = db.setAnswer(testId,jsonObjectQ.getInt("idq"),jsonObjectQ.getInt("type"));
                        //answer.setIdO(jsonObjectQ.getInt("ido"));
                        db.storeAnswer(answer);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDataToListView();
                        }
                    });
                }else{
                }
            } catch (JSONException e) {
                //Log.e("GETTIN ANSWERS", e.toString());
            }
            return null;
        }
    }

    private class GetQuestions extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            JSONParser jsonParser = new JSONParser();
            HashMap<String,String> values = new HashMap<>();
            values.put("tag","getTest");
            values.put("test",""+testId);
            try {
                JSONObject jsonObject = jsonParser.makePostCall(values);
                if(jsonObject.getInt("success")==1){
                    db.dropQuestions(testId);
                    JSONArray questions = jsonObject.getJSONArray("questions");
                    for(int i = 0; i < questions.length();i++){
                        JSONObject jsonObjectQ = questions.getJSONObject(i);
                        Question question = new Question();
                        question.setIdT(testId);
                        question.setId(jsonObjectQ.getInt("id"));
                        question.setName(jsonObjectQ.getString("text"));
                        question.setType(jsonObjectQ.getInt("type"));
                        if(answered == 1) {
                            try {
                                Answer answer = db.getAnswer(question.getId());
                                if (answer.getIdO() == jsonObjectQ.getInt("id_o")) {
                                    question.setRight(1);
                                } else {
                                    question.setRight(0);
                                }
                            } catch (NullPointerException e) {
                                question.setRight(2);
                            }
                        }else{
                            question.setRight(2);
                        }
                        db.storeQuestion(question);
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RelativeLayout layout = (RelativeLayout) findViewById(R.id.quizLayout);
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
                    swipeRefreshLayout.setRefreshing(false);
                    new GetAnswers().execute();
                    loadDataToListView();
                }
            });

            return null;
        }
    }

    private class GetAllOptions extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            ArrayList<Question> questions = db.getQuestions(testId);
            for(int i = 0; i < questions.size(); i++ ) {
                Question question = questions.get(i);
                int questionId = question.getId();
                HashMap<String, String> values = new HashMap<>();
                values.put("tag", "getOptions");
                values.put("question", "" + questionId);
                try {
                    JSONObject jsonObject = jsonParser.makePostCall(values);
                    if (jsonObject.getInt("success") == 1) {
                        JSONArray options = jsonObject.getJSONArray("options");
                        for (int ii = 0; ii < options.length(); ii++) {
                            JSONObject JOption = options.getJSONObject(ii);
                            Option option = new Option();
                            option.setIdO(JOption.getInt("id"));
                            option.setIdQ(questionId);
                            option.setType(JOption.getInt("type"));
                            option.setName(JOption.getString("name"));
                            db.storeOption(option);
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RelativeLayout layout = (RelativeLayout) findViewById(R.id.quizLayout);
                                Snackbar.make(layout, "Ste offline.", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }

    private class SendAnswers extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            int answersCount = db.getAnswersCount(testId);
            int questionsCount = db.getQuestionsCount(testId);

            if(answersCount == questionsCount) {
                JSONParser jsonParser = new JSONParser();
                ArrayList<Answer> answers = db.getAnswers(testId);
                HashMap<String, String> values = new HashMap<>();
                values.put("tag", "setAnswers");
                values.put("answers", "{ \"answers\":" + answers.toString() + "}");
                try {
                    JSONObject jsonObject = jsonParser.makePostCall(values);
                    if (jsonObject.getInt("success") == 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RelativeLayout layout = (RelativeLayout) findViewById(R.id.quizLayout);
                                Snackbar.make(layout, "Úspešne odoslané!", Snackbar.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                finish();
                                startActivity(i);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout layout = (RelativeLayout) findViewById(R.id.quizLayout);
                        Snackbar.make(layout, "Všetky otázky musia byť zodpovedné!", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }


}
