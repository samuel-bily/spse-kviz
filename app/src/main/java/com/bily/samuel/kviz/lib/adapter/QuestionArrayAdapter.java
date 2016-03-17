package com.bily.samuel.kviz.lib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bily.samuel.kviz.R;
import com.bily.samuel.kviz.lib.database.Question;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 31.1.2016.
 */
public class QuestionArrayAdapter extends ArrayAdapter {

    private List<Question> questionList = new ArrayList<Question>();

    public QuestionArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void remove(){
        questionList.clear();
    }

    public Question getItem(int index){
        return this.questionList.get(index);
    }

    public void add(Question question){
        super.add(question);
        questionList.add(question);
    }

    public View getView(int position, View row, ViewGroup parent){
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, parent, false);
        }

        final Question question = getItem(position);
        TextView idView = (TextView)row.findViewById(R.id.questionId);
        TextView nameView = (TextView)row.findViewById(R.id.questionText);
        ImageView img = (ImageView)row.findViewById(R.id.imageView4);

        idView.setText("" + question.getId());
        nameView.setText(question.getName());

        if(question.getRight()==1){
            img.setImageResource(R.drawable.checkmark17);
        }else if(question.getRight()==0){
            img.setImageResource(R.drawable.error5);
        }else{
            img.setVisibility(View.GONE);
            RelativeLayout imageLayout = (RelativeLayout)row.findViewById(R.id.imageLayout);
            imageLayout.setVisibility(View.GONE);
        }

        return row;
    }

}
