package com.bily.samuel.kviz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bily.samuel.kviz.lib.JSONParser;
import com.bily.samuel.kviz.lib.adapter.TestArrayAdapter;
import com.bily.samuel.kviz.lib.database.DatabaseHelper;
import com.bily.samuel.kviz.lib.database.Test;
import com.bily.samuel.kviz.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private TextView nameText;
    private TextView emailText;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TestArrayAdapter testArray;
    private FragmentManager fm;
    private ActionProcessButton button;
    private DatabaseHelper db;
    private GetTests getTests;
    private int id;
    private String regId;
    private String PROJECT_NUMBER = "821868220510";
    private String test ="";
    private String instructor = "";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(getApplicationContext());
        user = db.getUser();
        if (user == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            try {
                loadDataToListView();
            } catch (IllegalStateException e) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);
            }
            nameText = (TextView) findViewById(R.id.nameText);
            emailText = (TextView) findViewById(R.id.emailText);
            nameText.setText(user.getName());
            emailText.setText(user.getEmail());
            id = user.getId_u();

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipaMain);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setColorSchemeColors(R.color.colorLigth, R.color.colorDark);
            swipeRefreshLayout.setRefreshing(true);

            testArray = new TestArrayAdapter(getApplicationContext(), R.layout.list_test);
            getTests = new GetTests();
            getTests.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.find:
                filter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTests = new GetTests();
        getTests.execute();
        db = new DatabaseHelper(getApplicationContext());
        user = db.getUser();
        nameText = (TextView) findViewById(R.id.nameText);
        emailText = (TextView) findViewById(R.id.emailText);
        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        id = user.getId_u();
    }

    public void onProfileButtonClicked(View view) {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
    }

    public void loadDataToListView() {
        try {
            final ArrayList<Test> tests = db.getTests();
            testArray = new TestArrayAdapter(getApplicationContext(), R.layout.list_test);
            for (int i = 0; i < tests.size(); i++) {
                Test test = tests.get(i);
                testArray.add(test);
            }
            ListView listView = (ListView) findViewById(R.id.listView);
            //ListAdapter adapter = new SimpleAdapter(getApplicationContext(), tests, R.layout.list_test, new String[]{"name","id_t"}, new int[]{R.id.testTextView,R.id.testId});
            listView.setAdapter(testArray);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Test test = testArray.getItem(position);
                    String text = test.getName();
                    String sId = "" + test.getIdT();
                    int testId = Integer.valueOf(sId);
                    Intent i = new Intent(getApplicationContext(), QuizActivity.class);
                    i.putExtra("test", text);
                    i.putExtra("testId", testId);
                    i.putExtra("answered", test.isAnswered());
                    startActivity(i);
                }
            });
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void filter() {
        fm = getSupportFragmentManager();
        SetFilter setFilter = new SetFilter();
        setFilter.show(fm, "Filter");
    }

    @Override
    public void onRefresh() {
        getTests = new GetTests();
        getTests.execute();
    }

    @SuppressLint("ValidFragment")
    public class SetFilter extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
            getDialog().setTitle("Filter");
            final EditText editTest = (EditText)rootView.findViewById(R.id.editTestName);
            final EditText editInstructor = (EditText)rootView.findViewById(R.id.editInstructor);
            final Switch filter = (Switch)rootView.findViewById(R.id.switch1);
            filter.setChecked(true);
            filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        editTest.setEnabled(true);
                        editInstructor.setEnabled(true);
                    }else{
                        editTest.setEnabled(false);
                        editInstructor.setEnabled(false);
                        test = "";
                        instructor = "";
                        getTests = new GetTests();
                        getTests.execute();
                    }
                }
            });
            editTest.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    test = editTest.getText().toString();
                    instructor = editInstructor.getText().toString();
                    getTests = new GetTests();
                    getTests.execute();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    test = editTest.getText().toString();
                    instructor = editInstructor.getText().toString();
                    getTests = new GetTests();
                    getTests.execute();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            editInstructor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    test = editTest.getText().toString();
                    instructor = editInstructor.getText().toString();
                    getTests = new GetTests();
                    getTests.execute();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    test = editTest.getText().toString();
                    instructor = editInstructor.getText().toString();
                    getTests = new GetTests();
                    getTests.execute();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            return rootView;
        }
    }


    private class GetTests extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String, String> values = new HashMap<>();
            values.put("tag", "getTests");
            values.put("id_u", "" + user.getId_u());
            values.put("test", "%"+test+"%");
            values.put("instructor", "%"+instructor+"%");
            try {
                JSONObject jsonObject = jsonParser.makePostCall(values);
                Log.e("gettin", jsonObject.toString());
                if (jsonObject.getInt("success") == 1) {
                    JSONArray tests = jsonObject.getJSONArray("tests");
                    db.dropTests();
                    for (int i = 0; i < tests.length(); i++) {
                        JSONObject testJ = tests.getJSONObject(i);
                        Test test = new Test();
                        test.setIdT(testJ.getInt("id"));
                        test.setName(testJ.getString("name"));
                        test.setAnswered(testJ.getInt("answered"));
                        test.setInstructor(testJ.getString("instructor"));
                        if (testJ.getInt("answered") > 0) {
                            test.setQuestions("" + testJ.getDouble("stat") + "%");
                        }
                        db.storeTest(test);
                    }

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_relative);
                            Snackbar.make(layout, "Something went wrong.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                Log.e("JSON", e.toString());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    loadDataToListView();
                }
            });

            return null;
        }
    }
}
