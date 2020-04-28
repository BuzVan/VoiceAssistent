package com.voiceassistent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.voiceassistent.messageView.MessageListAdapter;
import com.voiceassistent.messageView.Message;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected TextToSpeech textToSpeech;
    protected Button sendButton;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    protected MessageListAdapter messageListAdapter;
    protected LinearLayoutManager layoutManager;
    private  boolean IslastItemVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AI.setContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);
        messageListAdapter = new MessageListAdapter();
        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);
        layoutManager = (LinearLayoutManager) chatMessageList.getLayoutManager();
        //cобытие при пролистывании
        chatMessageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy!=0)
                    IslastItemVisible = layoutManager.findLastVisibleItemPosition() == chatMessageList.getAdapter().getItemCount() - 1;
            }
        });

        //событие при изменении размеров экрана (при открытии клавиатуры)
        chatMessageList.addOnLayoutChangeListener(new RecyclerView.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (IslastItemVisible && oldBottom > bottom)
                {
                    chatMessageList.post(() -> chatMessageList.scrollToPosition(messageListAdapter.messageList.size() -1));
                }
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status!=TextToSpeech.ERROR){
                    Log.i("LocaleDef", Locale.getDefault().toLanguageTag());
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
    }
    //сохранение данных
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("chat", messageListAdapter.messageList.toArray());
        outState.putInt("last_visible_item_rv",layoutManager.findLastCompletelyVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }
    // восстановление данных
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Object[] messages = (Object[]) savedInstanceState.getSerializable("chat");
        for (Object message:
             messages) {
            messageListAdapter.messageList.add((Message) message);
        }
        messageListAdapter.notifyDataSetChanged();
        chatMessageList.scrollToPosition(savedInstanceState.getInt("last_visible_item_rv"));
    }

  public void sendButtonOnClick(View view) {
        String text = questionText.getText().toString().trim();
        if (text.length()>0){
            questionText.getText().clear();
            sendButton.setEnabled(false);
            AI.getAnswer(text, new Consumer<String>() {
                @Override
                public void accept(String answer) {
                    textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH,null,null);
                    messageListAdapter.messageList.add(new Message(text, true));
                    messageListAdapter.messageList.add(new Message(answer, false));

                    messageListAdapter.notifyDataSetChanged();
                    chatMessageList.scrollToPosition(messageListAdapter.messageList.size() -1);

                    sendButton.setEnabled(true);
                    IslastItemVisible = true;

                }
            });
        }

    }


}
