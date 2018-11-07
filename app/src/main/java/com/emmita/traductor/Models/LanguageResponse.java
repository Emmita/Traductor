package com.emmita.traductor.Models;

import com.google.gson.annotations.SerializedName;

public class LanguageResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("lang")
    private String lang;

    public int getCode(){
        return code;
    }

    public void setCode(int code){
        this.code = code;
    }

    public String getLang(){
        return lang;
    }

    public void setLang(String lang){
        this.lang = lang;
    }

}
