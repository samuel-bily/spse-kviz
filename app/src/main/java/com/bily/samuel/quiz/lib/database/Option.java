package com.bily.samuel.quiz.lib.database;

/**
 * Created by samuel on 19.1.2016.
 */
public class Option {

    private int id;
    private int idQ;
    private int idO;
    private int type;
    private String name;

    public Option(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdQ() {
        return idQ;
    }

    public void setIdQ(int idQ) {
        this.idQ = idQ;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdO() {
        return idO;
    }

    public void setIdO(int idO) {
        this.idO = idO;
    }

    public String toString(){
        return "id:" + id + ", idQ:" + idQ + ", name:" + name;
    }
}
