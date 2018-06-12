package com.app.sell;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.sell.dao.ChatMessageDao;
import com.app.sell.dao.ChatroomDao;
import com.app.sell.dao.LoginDao;
import com.app.sell.dao.OfferDao;
import com.app.sell.events.ChatMessageSentEvent;
import com.app.sell.events.ChatroomCreatedEvent;
import com.app.sell.events.ChatroomLoadedEvent;
import com.app.sell.events.OfferLoadedEvent;
import com.app.sell.model.ChatMessage;
import com.app.sell.model.Offer;
import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EActivity(R.layout.activity_make_offer)
public class MakeOfferActivity extends AppCompatActivity {

    String offerId;
    String offererId;
    String chatroomId;
    @Bean
    ChatroomDao chatroomDao;
    @Bean
    LoginDao loginDao;
    @Bean
    OfferDao offerDao;
    @Bean
    ChatMessageDao chatMessageDao;
    @ViewById(R.id.make_offer_price_text_view)
    EditText priceEditText;
    @ViewById(R.id.make_offer_button)
    Button makeOfferButton;
    @ViewById(R.id.make_offer_offer_image_view)
    ImageView offerImageView;

    @AfterViews
    void init() {
        offerId = getIntent().getStringExtra(getString(R.string.field_offer_id));
        offererId = getIntent().getStringExtra(getString(R.string.field_offerer_id));
        offerDao.loadOffer(offerId);
    }

    @Subscribe
    public void onOfferLoaded(OfferLoadedEvent offerLoadedEvent) {
        Offer offer = offerLoadedEvent.offer;

        Double offerPrice = offer.getPrice();
        boolean fixPrice = offer.getFirmOnPrice();
        String offerImage = offer.getImage();
        priceEditText.setText(String.valueOf(offerPrice));
        if(fixPrice) {
            priceEditText.setEnabled(false);
        }
        String senderId = loginDao.getCurrentUser().getUid();
        chatroomDao.loadChatroom(senderId, offererId, offerId);
        if(offerImage != null)
            Glide.with(this).load(offerImage).into(offerImageView);
    }

    @Subscribe
    public void chatroomCreated(ChatroomCreatedEvent chatroomCreatedEvent) {
        offerClicked();
    }

    @Click(R.id.make_offer_button)
    public void offerClicked() {
        ChatMessage offerChatMessage = createChatMessage();
        chatMessageDao.sendChatroomMessage(chatroomId, offerId, offererId, offerChatMessage);
        Snackbar.make(makeOfferButton, R.string.offer_message_sending, Snackbar.LENGTH_SHORT);
    }

    @NonNull
    private ChatMessage createChatMessage() {
        String message = getString(R.string.offer_message);
        message += " " + getString(R.string.default_currency) + priceEditText.getText().toString();
        long timestamp = System.currentTimeMillis();
        String senderId = loginDao.getCurrentUser().getUid();
        String senderUsername = loginDao.getCurrentUser().getUsername();

        return new ChatMessage("", senderId, senderUsername, message, timestamp);
    }

    @Subscribe
    public void chatroomLoaded(ChatroomLoadedEvent chatroomLoadedEvent) {
        chatroomId = chatroomLoadedEvent.chatroom.getId();
        String imageUri = chatroomLoadedEvent.chatroom.getOfferImageUri();
        Glide.with(this).load(imageUri).into(offerImageView);
    }

    @Subscribe
    public void chatMessageSent(ChatMessageSentEvent chatMessageSentEvent) {
        Snackbar.make(makeOfferButton, R.string.offer_message_sending, Snackbar.LENGTH_SHORT);
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
