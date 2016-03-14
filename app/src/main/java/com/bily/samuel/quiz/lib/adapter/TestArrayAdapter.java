package com.bily.samuel.quiz.lib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bily.samuel.quiz.R;
import com.bily.samuel.quiz.lib.database.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 29.1.2016.
 */
public class TestArrayAdapter extends ArrayAdapter {

    private List<Test> testList = new ArrayList<>();

    public TestArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Test test){
        super.add(test);
        testList.add(test);
    }

    public Test getItem(int index){
        return this.testList.get(index);
    }

    public void remove(){
        testList.clear();
    }

    public View getView(int position, View row, ViewGroup parent){
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_test, parent, false);
        }

        final Test test = getItem(position);

        TextView name = (TextView)row.findViewById(R.id.testTextView);
        TextView id = (TextView)row.findViewById(R.id.testId);
        ImageView imageView = (ImageView)row.findViewById(R.id.testImage);

        name.setText(test.getName());
        id.setText("" + test.getIdT());

        if(test.isAnswered() == 1){
            imageView.setImageResource(R.drawable.clipboard109);
        }else{
            imageView.setImageResource(R.drawable.inspiration);
        }

        return row;
    }

}
