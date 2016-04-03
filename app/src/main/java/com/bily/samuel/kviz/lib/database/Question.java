package com.bily.samuel.kviz.lib.database;

/**
 * Created by samuel on 19.1.2016.
 */
public class Question {

    private int id;
    private int idT;
    private int type;
    private String name;
    private int right;

    public Question(){}

    public int getIdT() {
        return idT;
    }

    public void setIdT(int idT) {
        this.idT = idT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString(){
        return "id:" + id + ", idT:" + idT + ", name:" + name;
    }
}
