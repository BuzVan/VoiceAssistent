package com.voiceassistent.numToText;

import android.content.Context;
import android.util.Log;

import androidx.core.util.Consumer;

import com.voiceassistent.R;
import com.voiceassistent.Service.WordGender;
import com.voiceassistent.Service.WordsFormService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TranslateToString {
    public static void getTranslate(Context context, final String text, final Consumer<String> callback){
        TranslateApi api = TranslateService.getApi();
        Call<Translate> call = api.getTranslate(text);
        call.enqueue(new Callback<Translate>() {
            @Override
            public void onResponse(Call<Translate> call, Response<Translate> response) {
                Translate translate = response.body();
                if (translate!=null){
                    //запрос выполнен успешно
                        callback.accept(translate.text.get(0));
                }
                else
                    callback.accept(context.getString(R.string.translate_error));
            }

            @Override
            public void onFailure(Call<Translate> call, Throwable t) {
                Log.v("TRANSLATE", t.getMessage());
                callback.accept(context.getString(R.string.translate_error));
            }
        });

    }


}
