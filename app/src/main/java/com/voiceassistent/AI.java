package com.voiceassistent;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

class AI {
    private static HashMap<String,String> dict = InitializationDictionary();
    private static HashMap<String,Calendar> dates = InitializationDates();

    private static HashMap<String, Calendar> InitializationDates() {
        HashMap<String, Calendar> dict = new HashMap<>();
        dict.put("дня рождения", new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR),Calendar.SEPTEMBER, 27));
        dict.put("нового года", new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) + 1,Calendar.JANUARY, 1));
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DATE));
        tomorrow.add(Calendar.DATE,1);
        dict.put("завтра", tomorrow);
        return  dict;
    }

    static String getAnswer(String question){
        question = question.toLowerCase();
        for (String key:
                dict.keySet()) {
            if (question.contains(key))
            {
                String val = dict.get(key);
                assert val != null;
                if (!val.startsWith("&")) return val;
                else{
                    return getSpecialAnswer(question, val);
                }
            }

        }
        return "Капец вы вопрос задали конешно...";
    }

    private static String getSpecialAnswer(String question, String val) {
        switch (val){
            case "&today": return getToday();
            case "&time": return getTime();
            case "&day_of_week": return getTodayOfWeek();
            case "&days_before": return getDaysBefore(question);
            default:
                throw new IllegalStateException("Unexpected value: " + val);
        }
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
                return "Дней до " + key + ": " + dayCount;
            }

        }
        return "Я не знаю когда это будет";
    }


    private static String getTodayOfWeek() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("Сегодня EEEE");
        return dateFormat.format(new Date());
    }

    private static String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("Сейчас HH:mm");
        return dateFormat.format(new Date());
    }

    private static String getToday() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("Сегодня d MMMM, EEEE");
        return dateFormat.format(new Date());
    }

    private static HashMap<String, String> InitializationDictionary(){
        HashMap<String, String> dict = new HashMap<>();
        dict.put("привет", "Привет");
        dict.put("как дела", "Не плохо");
        dict.put("что делаешь", "Отвечаю на вопросы");
        dict.put("чем занимаешься", "Отвечаю на вопросы");
        dict.put("спасибо", "Обращайтесь");

        dict.put("сегодня", "&today");
        dict.put("час", "&time");
        dict.put("врем", "&time");
        dict.put("день недели", "&day_of_week");
        dict.put("сколько дней до", "&days_before");
        return dict;
    }
}
