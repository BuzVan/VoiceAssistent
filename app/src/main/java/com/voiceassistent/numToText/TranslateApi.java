package com.voiceassistent.numToText;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TranslateApi {
    @GET("translate?key=trnsl.1.1.20200324T152839Z.d3f55364d64951e9.d4acd44306d00880ee876168a2a89d20bcb95d5f&lang=en-ru")
    Call<Translate> getTranslate(@Query("text") String text);
}