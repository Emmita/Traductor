package com.emmita.traductor.Retrofit;

import com.emmita.traductor.Models.TranslateResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/translate")
    Call<TranslateResponse> getText(@Query("key") String key, @Query("text") String text, @Query("lang") String lang);

}
