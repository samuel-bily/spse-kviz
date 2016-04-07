package com.bily.samuel.kviz.lib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by samuel on 21.11.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "quiz";
    private static final String TABLE_USER = "user";
    private static final String TABLE_TESTS = "tests";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_OPTIONS = "options";
    private static final String TABLE_ANSWERS = "answers";

    private static final String KEY_ID = "id";
    private static final String KEY_IDU = "id_u";
    private static final String KEY_IDT = "id_t";
    private static final String KEY_IDQ = "id_q";
    private static final String KEY_IDO = "id_o";
    private static final String KEY_TIMESTRAMP = "timestramp";
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_INSTRUCTOR = "instructor";
    private static final String KEY_RIGHT = "right";
    private static final String KEY_DESC = "desc";
    private static final String KEY_TYPE = "type";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GENDER = "gender";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IDU + " INTEGER," + KEY_NAME + " TEXT, " + KEY_EMAIL + " TEXT," + KEY_GENDER + " INTEGER" + " )";
    private static final String CREATE_TABLE_TEST = "CREATE TABLE " + TABLE_TESTS + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IDT + " INTEGER," + KEY_ANSWERED + " INTEGER," + KEY_DESC + " TEXT,"+ KEY_INSTRUCTOR + " TEXT," + KEY_NAME + " TEXT" + " )";
    private static final String CREATE_TABLE_QUESTION = "CREATE TABLE " + TABLE_QUESTIONS + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " INTEGER," + KEY_IDT + " INTEGER," + KEY_IDQ + " INTEGER," + KEY_RIGHT + " INTEGER," + KEY_NAME + " TEXT" + " )";
    private static final String CREATE_TABLE_OPTIONS = "CREATE TABLE " + TABLE_OPTIONS + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IDQ + " INTEGER," + KEY_IDO + " INTEGER," + KEY_TYPE + " INTEGER," + KEY_NAME + " TEXT" + " )";
    private static final String CREATE_TABLE_ANSWERS = "CREATE TABLE " + TABLE_ANSWERS + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IDU + " INTEGER," + KEY_IDO + " INTEGER," + KEY_IDT + " INTEGER," + KEY_IDQ + " INTEGER," + KEY_TIMESTRAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + " )";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_TEST);
        db.execSQL(CREATE_TABLE_QUESTION);
        db.execSQL(CREATE_TABLE_OPTIONS);
        db.execSQL(CREATE_TABLE_ANSWERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IDU, user.getId_u());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_GENDER, user.getGender());
        db.insert(TABLE_USER, null, values);

    }

    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE 1", null);
        User user = new User();
        try {
            if (c.moveToFirst()) {
                user.setId_u(c.getInt(c.getColumnIndex(KEY_IDU)));
                user.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                user.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
                user.setGender(c.getInt(c.getColumnIndex(KEY_GENDER)));

                c.close();
                return user;
            }
        }catch (IllegalStateException e){
            Log.e("IllegalStateException", e.toString());
        }

        c.close();
        return null;
    }

    public void changeName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME,name);
        db.update(TABLE_USER, cv, "1", new String[]{});
    }

    public void storeTest(Test test) {
        //Log.e("STORING", test.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IDT, test.getIdT());
        values.put(KEY_NAME, test.getName());
        values.put(KEY_ANSWERED, test.isAnswered());
        values.put(KEY_INSTRUCTOR, test.getInstructor());
        values.put(KEY_DESC, test.getQuestions());
        db.insert(TABLE_TESTS, null, values);

    }

    public ArrayList<Test> getTests() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Test> tests = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TESTS, null);
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            Test test = new Test();
            test.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            test.setIdT(c.getInt(c.getColumnIndex(KEY_IDT)));
            test.setAnswered(c.getInt(c.getColumnIndex(KEY_ANSWERED)));
            test.setInstructor(c.getString(c.getColumnIndex(KEY_INSTRUCTOR)));
            test.setQuestions(c.getString(c.getColumnIndex(KEY_DESC)));
            tests.add(test);
        }
        c.close();
        return tests;
    }

    public void storeQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IDQ, question.getId());
        values.put(KEY_NAME, question.getName());
        values.put(KEY_IDT, question.getIdT());
        values.put(KEY_RIGHT, question.getRight());
        values.put(KEY_TYPE, question.getType());
        db.insert(TABLE_QUESTIONS, null, values);

    }

    public void updateQuestionType(int idq, int type){
        SQLiteDatabase db = this.getReadableDatabase();
        db.rawQuery("UPDATE " + TABLE_QUESTIONS + " SET " + KEY_TYPE + "=" + type + " WHERE " + KEY_IDQ + "=" + idq, null);
    }

    public int getQuestionType(int idq){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_IDQ + " = " + idq, null);
        c.moveToFirst();
        int type = c.getInt(c.getColumnIndex(KEY_TYPE));
        c.close();
        return type;
    }

    public ArrayList<Question> getQuestions(int idt) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Question> questions = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_IDT + " = " + idt, null);
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            Question question = new Question();
            question.setId(c.getInt(c.getColumnIndex(KEY_IDQ)));
            question.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            question.setIdT(c.getInt(c.getColumnIndex(KEY_IDT)));
            question.setRight(c.getInt(c.getColumnIndex(KEY_RIGHT)));
            question.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
            questions.add(question);
        }
        c.close();
        return questions;
    }

    public int getQuestionsCount(int idt){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_IDT + " = " + idt, null);
        int num = c.getCount();
        c.close();
        return num;
    }

    public void storeOption(Option option) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IDO, option.getIdO());
        values.put(KEY_NAME, option.getName());
        values.put(KEY_IDQ, option.getIdQ());
        values.put(KEY_TYPE, option.getType());
        db.insert(TABLE_OPTIONS, null, values);

    }

    public ArrayList<HashMap<String, String>> getOptions(int idq) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> options = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_OPTIONS + " WHERE " + KEY_IDQ + " = " + idq, null);
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            HashMap<String, String> option = new HashMap<>();
            option.put(KEY_IDO, "" + c.getInt(c.getColumnIndex(KEY_IDO)));
            option.put(KEY_NAME, c.getString(c.getColumnIndex(KEY_NAME)));
            option.put(KEY_TYPE, "" + c.getInt(c.getColumnIndex(KEY_TYPE)));
            options.add(option);
        }
        c.close();

        return options;
    }

    public Option getOption(int idQ, int idO) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_OPTIONS + " WHERE " + KEY_IDQ + " = " + idQ + " AND " + KEY_IDO + " = " + idO, null);
        if (c.moveToFirst()) {
            Option option = new Option();
            option.setIdO(c.getInt(c.getColumnIndex(KEY_IDO)));
            option.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
            option.setIdQ(c.getInt(c.getColumnIndex(KEY_IDQ)));
            c.close();
            return option;
        }
        c.close();
        return null;
    }

    public Answer setAnswer(int idt, int idq, int type){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_OPTIONS + " WHERE " + KEY_IDQ + " = " + idq + " AND " + KEY_TYPE + " = " + type, null);
        if(c.moveToFirst()){
            Answer answer = new Answer();
            User user = this.getUser();
            answer.setIdO(c.getInt(c.getColumnIndex(KEY_IDO)));
            answer.setIdU(user.getId_u());
            answer.setIdQ(idq);
            answer.setIdT(idt);
            //answer.setTimestramp(c.getString(c.getColumnIndex(KEY_TIMESTRAMP)));
            c.close();
            return answer;
        }

        c.close();
        return null;
    }

    public void storeAnswer(Answer answer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ANSWERS + " WHERE " + KEY_IDQ + " = " + answer.getIdQ(),null);
        if(c.getCount()>0) {
            db.delete(TABLE_ANSWERS, KEY_IDQ + " = ?", new String[]{"" + answer.getIdQ()});
        }
        c.close();
        values.put(KEY_IDU, answer.getIdU());
        values.put(KEY_IDO,answer.getIdO());
        values.put(KEY_IDQ,answer.getIdQ());
        values.put(KEY_IDT,answer.getIdT());
        //values.put(KEY_TIMESTRAMP,dateFormat.format(date));
        db.insert(TABLE_ANSWERS, null, values);
    }

    public Answer getAnswer(int idq){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ANSWERS + " WHERE " + KEY_IDQ + " = " + idq, null);
        if(c.moveToFirst()){
            Answer answer = new Answer();
            answer.setIdO(c.getInt(c.getColumnIndex(KEY_IDO)));
            answer.setIdQ(c.getInt(c.getColumnIndex(KEY_IDQ)));
            //answer.setTimestramp(c.getString(c.getColumnIndex(KEY_TIMESTRAMP)));
            answer.setIdU(c.getInt(c.getColumnIndex(KEY_IDU)));
            c.close();
            return answer;
        }
        c.close();
        return null;
    }

    public ArrayList<Answer> getAnswers(int idT){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Answer> answers = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ANSWERS + " WHERE " + KEY_IDT + " = " + idT, null);
        for(int i = 0; i<c.getCount(); i++){
            c.moveToPosition(i);
            Answer answer = new Answer();
            answer.setIdO(c.getInt(c.getColumnIndex(KEY_IDO)));
            answer.setIdQ(c.getInt(c.getColumnIndex(KEY_IDQ)));
            answer.setIdT(c.getInt(c.getColumnIndex(KEY_IDT)));
            answer.setIdU(c.getInt(c.getColumnIndex(KEY_IDU)));
            answer.setTimestramp(c.getString(c.getColumnIndex(KEY_TIMESTRAMP)));
            answers.add(answer);
        }
        c.close();
        return answers;
    }

    public int getAnswersCount(int idt){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ANSWERS + " WHERE " + KEY_IDT + " = " + idt, null);
        int num = c.getCount();
        c.close();
        return num;
    }

    public void dropQuestions(int idt) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, KEY_IDT + " = ?", new String[]{"" + idt});
    }

    public void dropTests() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TESTS, null, null);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        onCreate(db);
    }

}
