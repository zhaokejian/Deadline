package com.example.hsq.deadline;

/**
 * Created by kejian on 2015/12/18.
 */
public class Mydate {
    private String date;
    private String w;
    private String d;

    public Mydate(String ww, String dd){
        w=ww;
        d=dd;
        date=w+" "+d;
    }
    public String getdate(){
        return date;
    }
    public String getD(){
        return d;
    }
    public String getW(){
        return w;
    }
}