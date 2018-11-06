package com.emmita.traductor.Retrofit;

import com.emmita.traductor.LanguageResponse;
import com.emmita.traductor.TranslateResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/detect")
    Call<LanguageResponse> getLanguage(@Query("key") String key, @Query("text") String text);

    @POST("/translate")
    Call<TranslateResponse> getText(@Query("key") String key, @Query("text") String text, @Query("lang") String lang);

}
