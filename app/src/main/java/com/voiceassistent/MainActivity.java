package com.voiceassistent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected TextToSpeech textToSpeech;
    protected Button sendButton;
    protected EditText questionText;
    protected TextView chatWindow;
    private CharSequence chatText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(new Locale("ru"));
                }
            }
        });
        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatWindow = findViewById(R.id.chatWindow);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putCharSequence("chat", chatWindow.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        chatWindow.append(savedInstanceState.getCharSequence("chat"));
    }

    public void sendButtonOnClick(View view) {
        String text = questionText.getText().toString();
        String answer = AI.getAnswer(text);
        chatWindow.append(String.format("   >> %s\n",text));
        chatWindow.append(String.format("<< %s\n",answer));
        questionText.getText().clear();
        textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH,null,null);
    }
}
