package com.app.sell;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.app.sell.dao.ChatMessageDao;
import com.app.sell.dao.ChatroomDao;
import com.app.sell.dao.LoginDao;
import com.app.sell.dao.OfferDao;
import com.app.sell.dao.UserDao;
import com.app.sell.events.ChatMessageQueuedEvent;
import com.app.sell.events.ChatMessagesUpdatedEvent;
import com.app.sell.events.ChatroomLoadedEvent;
import com.app.sell.events.OfferLoadedEvent;
import com.app.sell.events.UserLoadedEvent;
import com.app.sell.model.Chatroom;
import com.app.sell.model.Offer;
import com.app.sell.model.User;
import com.app.sell.util.GlideLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.klinker.android.badged_imageview.BadgedImageView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import de.hdodenhof.circleimageview.CircleImageView;

@EActivity(R.layout.activity_ask)
public class AskActivity extends AppCompatActivity {

    @Bean
    ChatroomDao chatroomDao;
    @Bean
    LoginDao loginDao;
    @Bean
    ChatMessageDao chatMessageDao;
    @Bean
    UserDao userDao;
    @Bean
    OfferDao offerDao;
    @ViewById(R.id.chat_view)
    ChatView mChatView;
    @ViewById(R.id.ask_profile_image)
    CircleImageView profileImageView;
    @ViewById(R.id.ask_offer_image_view)
    BadgedImageView offerImageView;
    @ViewById(R.id.ask_username_text_view)
    TextView usernameTextView;
    private String mChatroomId;
    private String mOfferId;
    private String mOffererId;
    private List<com.app.sell.model.ChatMessage> queuedMessages = new ArrayList<>();

    @AfterViews
    protected void initViews() {
        mChatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                return sendChatMessage(chatMessage);
            }
        });
    }

    private boolean sendChatMessage(ChatMessage chatMessage) {
        com.app.sell.model.ChatMessage databaseChatMessage = makeDatabaseChatMessage(chatMessage);
        return chatMessageDao.sendChatroomMessage(mChatroomId, mOfferId, mOffererId, databaseChatMessage);
    }

    private com.app.sell.model.ChatMessage makeDatabaseChatMessage(ChatMessage chatMessage) {
        com.app.sell.model.ChatMessage result = new com.app.sell.model.ChatMessage();

        result.setMessage(chatMessage.getMessage());
        result.setTimestamp(chatMessage.getTimestamp());
        result.setSenderId(loginDao.getCurrentUser().getUid());
        result.setSenderUsername(loginDao.getCurrentUser().getUsername());

        return result;
    }

    @AfterInject
    @Background
    protected void initData() {
        String chatroomIdFromNotification = getIntent().getStringExtra(this.getString(R.string.field_chatroom_id));
        if (chatroomIdFromNotification != null) {
            mChatroomId = chatroomIdFromNotification;
            chatroomDao.loadChatroom(mChatroomId);
        } else {
            mOffererId = getIntent().getStringExtra(this.getString(R.string.field_offerer_id));
            mOfferId = getIntent().getStringExtra(this.getString(R.string.field_offer_id));
            String userId = loginDao.getCurrentUser().getUid();

            userDao.loadUser(mOffererId);
            offerDao.loadOffer(mOfferId);
            chatroomDao.loadChatroom(userId, mOffererId, mOfferId);
        }
    }

    @Subscribe
    public void offererLoaded(UserLoadedEvent userLoadedEvent) {
        bindOfferer(userLoadedEvent.user);
    }

    @UiThread
    private void bindOfferer(User user) {
        mOffererId = user.getUid();
        usernameTextView.setText(user.getUsername());
        GlideLoader.loadIfValid(this, user.getImage(), profileImageView);
    }

    @Subscribe
    public void chatroomLoaded(ChatroomLoadedEvent chatroomLoadedEvent) {
        mChatroomId = chatroomLoadedEvent.chatroom.getId();
        chatMessageDao.initFor(mChatroomId);
        if (mOfferId == null)
            offerDao.loadOffer(chatroomLoadedEvent.chatroom.getOfferId());
        if (mOffererId == null) {
            Chatroom chatroom = chatroomLoadedEvent.chatroom;
            for (String userId : chatroom.getUsers().keySet()) {
                if (!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().contentEquals(userId)) {
                    userDao.loadUser(userId);
                    break;
                }
            }
        }

        for (com.app.sell.model.ChatMessage queuedMessage :
                queuedMessages) {
            chatMessageDao.sendChatroomMessage(mChatroomId, mOfferId, mOffererId, queuedMessage);
        }
        queuedMessages.clear();
    }

    @Subscribe
    public void offerLoaded(OfferLoadedEvent offerLoadedEvent) {
        bindOffer(offerLoadedEvent.offer);
    }

    @UiThread
    private void bindOffer(Offer offer) {
        mOfferId = offer.getId();
        offerImageView.setBadge(String.valueOf(offer.getPrice()));
        GlideLoader.loadIfValid(this, offer.getImage(), offerImageView);
    }

    @Subscribe
    public void newMessageQueued(ChatMessageQueuedEvent chatMessageQueuedEvent) {
        queuedMessages.add(chatMessageQueuedEvent.chatMessage);
    }

    @Subscribe
    public void addMessages(ChatMessagesUpdatedEvent chatMessagesUpdatedEvent) {
        List<com.app.sell.model.ChatMessage> chatMessages = chatMessageDao.getChatMessageList();

        populateMessages(chatMessages);
    }

    private void populateMessages(List<com.app.sell.model.ChatMessage> chatMessages) {
        mChatView.clearMessages();
        for (com.app.sell.model.ChatMessage databaseChatMessage : chatMessages) {

            long timestamp = databaseChatMessage.getTimestamp();
            String message = databaseChatMessage.getMessage();
            ChatMessage.Type messageType = ChatMessage.Type.RECEIVED;
            if (databaseChatMessage.getSenderId().contentEquals(loginDao.getCurrentUser().getUid())) {
                messageType = ChatMessage.Type.SENT;
            }
            mChatView.addMessage(new ChatMessage(message, timestamp, messageType));
        }
    }

    public void sendIsStillAvailableMsg(View view) {
        ChatMessage chatMessage = createChatMessageFromStringResource(R.string.still_available);
        sendChatMessage(chatMessage);
    }

    public void sendToBuyMsg(View view) {
        ChatMessage chatMessage = createChatMessageFromStringResource(R.string.like_to_buy);
        sendChatMessage(chatMessage);
    }

    public void sendToMeetMsg(View view) {
        ChatMessage chatMessage = createChatMessageFromStringResource(R.string.meet_today);
        sendChatMessage(chatMessage);
    }

    @NonNull
    private ChatMessage createChatMessageFromStringResource(int resourceLocation) {
        return new ChatMessage(getString(resourceLocation), new Date().getTime(), ChatMessage.Type.SENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(getString(R.string.ask_activity_shared_pref), MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.apply();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(getString(R.string.ask_activity_shared_pref), MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.apply();

        EventBus.getDefault().unregister(this);

        super.onStop();
    }
}
