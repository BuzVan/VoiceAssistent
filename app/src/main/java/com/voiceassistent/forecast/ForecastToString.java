package com.voiceassistent.forecast;

import android.util.Log;

import androidx.core.util.Consumer;

import com.voiceassistent.Service.WordGender;
import com.voiceassistent.Service.WordsFormService;
import com.voiceassistent.numToText.TranslateToString;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getForecast(final String city, final Consumer<String> callback){
        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast result = response.body();
                if (result!=null){

                    TranslateToString.getTranslate(result.current.weather_descriptions.get(0), new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            String descr = "";
                            if (!s.equals("не могу перевести")) descr = s;
                            String answer = "Сейчас в городе " + city + " " +
                                    result.current.temperature + " " +
                                    WordsFormService.getGoodWordFormAfterNum(result.current.temperature, "градус", WordGender.MALE_GENDER) +
                                    ". " + descr;
                            callback.accept(answer);
                        }
                    });

                }
                else callback.accept("Не могу узнать погоду");
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.v("WEATHER", t.getMessage());
                callback.accept("Нет соединения с интернетом");
            }
        });

    }


}
