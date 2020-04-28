package com.voiceassistent;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.core.util.Consumer;

import com.voiceassistent.forecast.ForecastToString;
import com.voiceassistent.numToText.TranslateToString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AI {
    private static Context context;
    private static HashMap<String,String> dict;
    private static HashMap<String,Calendar> dates;
    public static  void setContext(Context Context) {
        context = Context;
        dict = InitializationDictionary();
        dates = InitializationDates();

    }
    private static HashMap<String, Calendar> InitializationDates() {
        HashMap<String, Calendar> dict = new HashMap<>();
        dict.put(context.getString(R.string.before_birthday), new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR),Calendar.SEPTEMBER, 27));
        dict.put(context.getString(R.string.before_new_year), new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) + 1,Calendar.JANUARY, 1));
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DATE));
        tomorrow.add(Calendar.DATE,1);
        dict.put(context.getString(R.string.before_tomorrow), tomorrow);
        return  dict;
    }
    private static HashMap<String, String> InitializationDictionary(){
        HashMap<String, String> dict = new HashMap<>();
        dict.put(context.getString(R.string.q_hi), context.getString(R.string.a_hi));
        dict.put(context.getString(R.string.q_how_are_you), context.getString(R.string.a_how_are_you));
        dict.put(context.getString(R.string.q_what_doing), context.getString(R.string.a_what_doing));
        dict.put(context.getString(R.string.q_what_doing_2), context.getString(R.string.a_what_doing));
        dict.put(context.getString(R.string.q_thank), context.getString(R.string.a_thank));

        dict.put(context.getString(R.string.q_today), "&today");
        dict.put(context.getString(R.string.q_time), "&time");
        dict.put(context.getString(R.string.q_time2), "&time");
        dict.put(context.getString(R.string.q_day_of_week), "&day_of_week");
        dict.put(context.getString(R.string.q_days_before), "&days_before");
        dict.put(context.getString(R.string.q_weather_in_city), "&weather");

        dict.put(context.getString(R.string.q_help), "&help");
        dict.put(context.getString(R.string.q_translate), "&translate");

        dict.put(context.getString(R.string.q_say), "&say_text");
        return dict;
    }

    private static String getHelp() {
        return
                String.format(context.getString(R.string.help_info));
    }


    static void getAnswer(String question, Consumer<String> callback){
        question = question.toLowerCase();
        String val = null;
        for (String key:
                dict.keySet()) {
            if (question.contains(key))
            {
                val = dict.get(key);
                break;
            }
        }
        if (val!=null){

                if (!val.startsWith("&"))  callback.accept(val);
                else{
                     getSpecialAnswer(question, val, new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            callback.accept(s);
                        }
                    });
                }
            }
        else callback.accept(context.getString(R.string.question_error));
    }


    private static void getSpecialAnswer(String question, String val, Consumer<String> callback) {

        switch (val){
            case "&help": callback.accept(getHelp()); break;
            case "&today": callback.accept(getToday()); break;
            case "&time": callback.accept(getTime()); break;
            case "&day_of_week": callback.accept(getTodayOfWeek()); break;
            case "&days_before": callback.accept(getDaysBefore(question)); break;
            case "&translate": getTranslation(question, s ->callback.accept(s));break;
            case "&weather": getWeather(question, s -> callback.accept(s)); break;
            case "&say_text": callback.accept(getRepeatText(question));  break;
            default:
                throw new IllegalStateException("Unexpected value: " + val);
        }
    }

    private static String getRepeatText(String question) {
        Pattern translPattern = Pattern.compile(
                // . - любой символ. Т.е. можно попросить ассистента: "Переведи Hello, world!"
                context.getString(R.string.q_say) + "(.+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = translPattern.matcher(question);
        if (matcher.find()) {
            return matcher.group(1);
        }
        else  return context.getString(R.string.q_say);
    }

    private static void getTranslation(String question, Consumer<String> callback){
        Pattern translPattern = Pattern.compile(
                // . - любой символ. Т.е. можно попросить ассистента: "Переведи Hello, world!"
                 context.getString(R.string.q_translate)+" (.+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = translPattern.matcher(question);
        if (matcher.find()) {
            final String[] text = {matcher.group(1)};
            TranslateToString.getTranslate(context, text[0], new Consumer<String>() {
                @Override
                public void accept(String text) {
                    callback.accept(text);
                }
            });
        }
        else callback.accept(context.getString(R.string.question_error));
    }
    private static void getWeather(String question, Consumer<String> callback){
        Pattern cityPattern = Pattern.compile(
                context.getString(R.string.q_weather_in_city) + " (\\p{L}+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(question);
        if (matcher.find()) {
            final String[] cityName = {matcher.group(1)};
            ForecastToString.getForecast(context, cityName[0], new Consumer<String>() {
                @Override
                public void accept(String s) {
                    callback.accept(s);
                }
            });
        }
        else callback.accept(context.getString(R.string.question_error));
    }

    private static String getDaysBefore(String question) {
        for (String key:
                dates.keySet()) {
            if (question.contains(key)) {
                Calendar date = dates.get(key);
                Date now = new Date();

                assert date != null;
                long msDateDistance = date.getTimeInMillis() - now.getTime();
                long msDay = 1000 * 60 * 60 * 24;
                int dayCount = (int) (msDateDistance / msDay) + 1;
                return  dayCount + "";
            }

        }
        return context.getString(R.string.a_before_error);
    }


    private static String getTodayOfWeek() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(" EEEE");
        return context.getString(R.string.a_day_of_week) + dateFormat.format(new Date());
    }

    private static String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(" HH:mm");
        return context.getString(R.string.a_time) + dateFormat.format(new Date());
    }

    private static String getToday() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(" d MMMM, EEEE");
        return context.getString(R.string.a_today) + dateFormat.format(new Date());
    }

}
