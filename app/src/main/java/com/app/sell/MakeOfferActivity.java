package com.app.sell;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.app.sell.dao.ChatMessageDao;
import com.app.sell.dao.ChatroomDao;
import com.app.sell.dao.LoginDao;
import com.app.sell.events.ChatroomLoadedEvent;
import com.app.sell.model.ChatMessage;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EActivity(R.layout.activity_make_offer)
public class MakeOfferActivity extends AppCompatActivity {

    String offerId;
    String offererId;
    @Bean
    ChatroomDao chatroomDao;
    @Bean
    LoginDao loginDao;
    @Bean
    ChatMessageDao chatMessageDao;
    @ViewById(R.id.make_offer_price_text_view)
    EditText priceEditText;
    @ViewById(R.id.make_offer_button)
    Button makeOfferButton;

    @AfterViews
    void init() {
        offerId = getIntent().getStringExtra(getString(R.string.field_offer_id));
        offererId = getIntent().getStringExtra(getString(R.string.field_offerer_id));
    }

    @Click(R.id.make_offer_button)
    public void offerClicked(Button button) {
        chatroomDao.loadChatroom(loginDao.getCurrentUser().getUid(), offererId, offerId);
    }

    //TODO: cover edge case where chatroom does not already exist
    @Subscribe
    public void chatroomLoaded(ChatroomLoadedEvent chatroomLoadedEvent) {

        String message = getString(R.string.offer_message);
        message += getString(R.string.default_currency) + priceEditText.getText().toString();
        long timestamp = System.currentTimeMillis();
        String senderId = loginDao.getCurrentUser().getUid();
        String senderUsername = loginDao.getCurrentUser().getUsername();

        ChatMessage offerChatMessage = new ChatMessage("", senderId, senderUsername, message, timestamp);
        chatMessageDao.sendChatroomMessage(chatroomLoadedEvent.chatroom.getId(), offerId, offererId, offerChatMessage);
        Snackbar.make(makeOfferButton, R.string.offer_message_sent, Snackbar.LENGTH_SHORT);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
