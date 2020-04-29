package com.voiceassistent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.core.util.Consumer;

import com.voiceassistent.forecast.ForecastToString;
import com.voiceassistent.htmlParsing.ParsingHtmlService;
import com.voiceassistent.numToText.TranslateToString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

class AI {
    private static Context context;
    private static HashMap<String,String> dict;
    private static HashMap<String,Calendar> dates;
    static  void setContext(Context Context) {
        context = Context;
        dict = InitializationDictionary();
        dates = InitializationDates();

    }
    private static HashMap<String, Calendar> InitializationDates() {
        HashMap<String, Calendar> dates = new HashMap<>();
        dates.put(context.getString(R.string.before_birthday), new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR),Calendar.SEPTEMBER, 27));
        dates.put(context.getString(R.string.before_new_year), new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) + 1,Calendar.JANUARY, 1));

        Calendar today = Calendar.getInstance();
        //today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
        dates.put(context.getString(R.string.today),today);

        Calendar tomorrow = Calendar.getInstance();
        //tomorrow.set(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DATE));
        tomorrow.add(Calendar.DATE,1);
        dates.put(context.getString(R.string.tomorrow),tomorrow);

        Calendar yesterday = Calendar.getInstance();
        //yesterday.set(yesterday.get(Calendar.YEAR), yesterday.get(Calendar.MONTH), yesterday.get(Calendar.DATE));
        yesterday.add(Calendar.DATE,-1);
        dates.put(context.getString(R.string.yesterday), yesterday);
        return dates;
    }
    private static HashMap<String, String> InitializationDictionary(){
        HashMap<String, String> dict = new HashMap<>();
        dict.put(context.getString(R.string.q_hi), context.getString(R.string.a_hi));
        dict.put(context.getString(R.string.q_how_are_you), context.getString(R.string.a_how_are_you));
        dict.put(context.getString(R.string.q_what_doing), context.getString(R.string.a_what_doing));
        dict.put(context.getString(R.string.q_what_doing_2), context.getString(R.string.a_what_doing));
        dict.put(context.getString(R.string.q_thank), context.getString(R.string.a_thank));
        dict.put(context.getString(R.string.q_holiday), "&holiday");
        dict.put(context.getString(R.string.today), "&today");
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
                context.getString(R.string.help_info);
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
                     getSpecialAnswer(question, val, callback);
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
            case "&translate": getTranslation(question, callback);break;
            case "&weather": getWeather(question, callback); break;
            case "&say_text": callback.accept(getRepeatText(question));  break;
            case "&holiday": getHolidays(question, callback); break;
            default:
                throw new IllegalStateException("Unexpected value: " + val);
        }
    }

    private static void getHolidays(String question, Consumer<String> callback) {
        String findDate = getDate(question);
        Log.i("DATE", findDate );
        final String[] answer = {""};
        /*
        new AsyncTask<String, Integer, Void>(){
            @Override
            protected Void doInBackground(String... strings) {
                try {
                    ArrayList<String> holidays = ParsingHtmlService.getHolidays(strings[0]);
                    Log.i("BACKGROUND", strings[0] );
                    if (holidays.size() ==0) {
                        answer[0] = context.getString(R.string.a_no_holidays);
                        return null;
                    }
                    String res =  "";
                    for (String str:
                            holidays) {

                        res+= (String.format("\n%s", str));
                    }
                    if (Locale.getDefault().getLanguage().equals("en")) {
                        TranslateToString.getTranslate(context, "ru-en", res.trim(), new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                answer[0] = s;
                                onPostExecute(null);
                            }
                        });
                    }
                    else answer[0] = res.trim();
                    return null;
                } catch (Exception e) {
                    answer[0] = context.getString(R.string.question_error);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (answer[0].length()>0)
                    callback.accept(answer[0]);
            }
        }.execute(findDate);
        */
        Observable.fromCallable(() -> {
            try {
                ArrayList<String> holidays = ParsingHtmlService.getHolidays(findDate);
                Log.i("BACKGROUND", findDate );
                if (holidays.size() ==0) {
                    return context.getString(R.string.a_no_holidays);
                }
                String res =  "";
                for (String str:
                        holidays) {
                    res+= (String.format("%s\n", str));
                }
                
                res = res.trim();
                if (Locale.getDefault().getLanguage().equals("en")) {
                    TranslateToString.getTranslate(context, "ru-en", res, new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            callback.accept(s);
                        }
                    });
                    return answer[0];
                }
                else  return res;
            } catch (Exception e) {
                return context.getString(R.string.question_error);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (result.length()>0)
                        callback.accept(result);
                });

    }

    @SuppressLint("SimpleDateFormat")
    private static String getDate(String question) {
        String[] patterns = {
                "d MMMM",
                "d MM",
                "d.MM",
                "d MMMM YYYY"
        };
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mainDataFormat = new SimpleDateFormat("d MMMM 2020", new Locale("ru"));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat;
        Date date = null;

       Pattern pattern = Pattern.compile( "\\d{1,2}[\\s.](\\d{1,2}|[а-яА-Яa-zA-Z]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(question);
        if (matcher.find()) {
            question = matcher.group().toLowerCase();
            System.out.println(question);
            int i = 0;
            boolean exit = false;
            while (!exit && i < patterns.length) {
                try {
                    date = new SimpleDateFormat(patterns[i]).parse(question);
                    exit = true;
                    System.out.println(patterns[i]);
                } catch (Exception e) {
                    i++;
                }
            }
            if (exit) return mainDataFormat.format(date);
        }
        //поиск даты в словаре
        for (String key:
                dates.keySet()) {
            if (question.contains(key)){
                date = dates.get(key).getTime();
                break;
            }
        }
        if (date!=null) return mainDataFormat.format(date);
        else return context.getString(R.string.question_error);
    }

    private static String getRepeatText(String question) {
        Pattern translPattern = Pattern.compile(
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
            TranslateToString.getTranslate(context,"en-ru",text[0], new Consumer<String>() {
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM EEEE");
        return context.getString(R.string.a_today)+ " " + dateFormat.format(new Date());
    }

}
