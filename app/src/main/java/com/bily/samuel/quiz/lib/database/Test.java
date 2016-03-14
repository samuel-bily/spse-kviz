package com.bily.samuel.quiz.lib.database;

/**
 * Created by samuel on 19.1.2016.
 */
public class Test {

    private int id;
    private int idT;
    private String name;
    private String questions;
    private int answered;

    public Test(){}

    public Test(String name, int idT, int answered, String questions){
        this.name = name;
        this.idT = idT;
        this.answered = answered;
        this.questions = questions;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getIdT() {
        return idT;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public int isAnswered() {
        return answered;
    }

    public void setAnswered(int answered) {
        this.answered = answered;
    }

    public void setIdT(int idT) {
        this.idT = idT;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", idT=" + idT +
                ", name='" + name + '\'' +
                ", questions='" + questions + '\'' +
                ", answered=" + answered +
                '}';
    }
}
