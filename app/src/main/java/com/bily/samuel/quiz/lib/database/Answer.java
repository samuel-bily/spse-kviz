package com.bily.samuel.quiz.lib.database;

/**
 * Created by samuel on 25.1.2016.
 */
public class Answer {

    private int id;
    private int idO;
    private int idU;
    private int idQ;
    private int idT;
    private String timestramp;

    public Answer(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdO() {
        return idO;
    }

    public void setIdO(int idO) {
        this.idO = idO;
    }

    public int getIdU() {
        return idU;
    }

    public void setIdU(int idU) {
        this.idU = idU;
    }

    public String getTimestramp() {
        return timestramp;
    }

    public void setTimestramp(String timestramp) {
        this.timestramp = timestramp;
    }

    public int getIdQ() {
        return idQ;
    }

    public void setIdQ(int idQ) {
        this.idQ = idQ;
    }

    public int getIdT() {
        return idT;
    }

    public void setIdT(int idT) {
        this.idT = idT;
    }

    @Override
    public String toString() {
        return "{" +
                " \"idO\":" + idO +
                ",\"idU\":" + idU +
                ",\"idQ\":" + idQ +
                ",\"idT\":" + idT +
                ",\"date\":" + "\"" + timestramp + "\"" +
                '}';
    }
}
