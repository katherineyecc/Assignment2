package com.example.assignment2;

public class Activity {

    private long ID;
    private String time;
    private String type;

    public Activity(long id, String time, String type){
        this.ID = id;
        this.time = time;
        this.type = type;
    }

    public Activity(String time, String type){
        this.time = time;
        this.type = type;
    }

    public Activity(){}

    public void setID(long i){
        this.ID = i;
    }

    public long getID(){
        return ID;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
