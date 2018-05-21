package com.app.sell;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Date;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class AskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        ChatView chatView = (ChatView) findViewById(R.id.chat_view);

        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(getString(R.string.ask_activity_shared_pref), MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(getString(R.string.ask_activity_shared_pref), MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.apply();
    }

    public void sendIsStillAvailableMsg(View view) {
        ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage(getString(R.string.still_available), new Date().getTime(), ChatMessage.Type.SENT));
    }

    public void sendToBuyMsg(View view) {
        ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage(getString(R.string.like_to_buy), new Date().getTime(), ChatMessage.Type.SENT));
    }

    public void sendToMeetMsg(View view) {
        ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage(getString(R.string.meet_today), new Date().getTime(), ChatMessage.Type.SENT));
    }
}
