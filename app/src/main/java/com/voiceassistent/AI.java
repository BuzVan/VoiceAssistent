package com.voiceassistent;

import java.util.HashMap;

class AI {
    private static HashMap<String,String> dict = InitializationDictionary();
    static String getAnswer(String question){
        question = question.toLowerCase();
        for (String key:
                dict.keySet()) {
            if (question.contains(key))
                return dict.get(key);
        }
        return "Капец вы вопрос задали конешн...";
    }

    private static HashMap<String, String> InitializationDictionary(){
        HashMap<String, String> dict = new HashMap<>();
        dict.put("привет", "Привет");
        dict.put("как дела", "Не плохо");
        dict.put("что делаешь", "Отвечаю на вопросы");
        dict.put("чем занимаешься", "Отвечаю на вопросы");
        return dict;
    }
}
