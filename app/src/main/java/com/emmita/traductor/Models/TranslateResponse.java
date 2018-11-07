package com.emmita.traductor.Models;

import com.google.gson.annotations.SerializedName;

public class TranslateResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("lang")
    private String lang;

    @SerializedName("text")
    private String[] text;

    public int getCode(){
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLang(){
        return lang;
    }

    public void setLang(String lang){
        this.lang = lang;
    }

    public String[] getText(){
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }
}
