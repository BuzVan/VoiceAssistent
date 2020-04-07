package com.voiceassistent;

import android.annotation.SuppressLint;

import androidx.core.util.Consumer;

import com.voiceassistent.forecast.ForecastToString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static String getHelp() {
        return
                "Вот что вы можете спросить у меня:\n" +
                        "\tЧем ты можешь ПОМОчь мне\n\n" +
                        "\tПРИВЕТствую\n\n" +
                        "\tКАК ДЕЛА\n\n" +
                        "\tскажи, ЧТО ДЕЛАЕШЬ сейчас или ЧЕМ ЗАНИМАЕШЬСЯ?\n\n" +

                        "\tкакой СЕГОДНЯ день?\n\n" +
                        "\tсколько ВРЕМени\n\n" +
                        "\tКакой ДЕНЬ НЕДЕЛИ\n\n" +
                        "\tСКОЛЬКО ДНЕЙ ДО {нового года / дня рождения}\n\n" +
                        "\tПОГОДА В ГОРОДЕ {название города}\n\n" +

                        "\tбольшое СПАСИБО за помощь\n\n" +

                        "*большими буквами выделены ключевые слова поиска в словаре";
    }

    private static HashMap<String, String> InitializationDictionary(){
        HashMap<String, String> dict = new HashMap<>();
        dict.put("привет", "Привет");
        dict.put("как дела", "Неплохо");
        dict.put("что делаешь", "Отвечаю на вопросы");
        dict.put("чем занимаешься", "Отвечаю на вопросы");
        dict.put("спасибо", "Обращайтесь");

        dict.put("сегодня", "&today");
        dict.put("час", "&time");
        dict.put("врем", "&time");
        dict.put("день недели", "&day_of_week");
        dict.put("сколько дней до", "&days_before");
        dict.put("погода в городе", "&weather");

        dict.put("помощь", "&help");
        dict.put("помо", "&help");
        return dict;
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
        else callback.accept("Не поняла Вас");
    }


    private static void getSpecialAnswer(String question, String val, Consumer<String> callback) {

        switch (val){
            case "&help": callback.accept(getHelp()); break;
            case "&today": callback.accept(getToday()); break;
            case "&time": callback.accept(getTime()); break;
            case "&day_of_week": callback.accept(getTodayOfWeek()); break;
            case "&days_before": callback.accept(getDaysBefore(question)); break;
            case "&weather":
                getWeather(question, new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        callback.accept(s);
                    }
                });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + val);
        }
    }


    private static void getWeather(String question, Consumer<String> callback){
        Pattern cityPattern = Pattern.compile(
                "погода в городе (\\p{L}+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(question);
        if (matcher.find()) {
            final String[] cityName = {matcher.group(1)};
            ForecastToString.getForecast(cityName[0], new Consumer<String>() {
                @Override
                public void accept(String s) {
                    callback.accept(s);
                }
            });
        }
        else callback.accept("Я не поняла ");
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
        return "Я не знаю, когда это будет";
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


}
