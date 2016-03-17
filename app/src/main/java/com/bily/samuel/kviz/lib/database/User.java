package com.bily.samuel.kviz.lib.database;

/**
 * Created by samuel on 21.11.2015.
 */
public class User {

    private int id;
    private int id_u;
    private String name;
    private String email;
    private String pass;
    private int gender;

    public User(){

    }

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    public User(int id, String name,String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_u() {
        return id_u;
    }

    public void setId_u(int id_u) {
        this.id_u = id_u;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
