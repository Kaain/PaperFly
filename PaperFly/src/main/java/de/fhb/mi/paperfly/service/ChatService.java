package de.fhb.mi.paperfly.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.fhb.mi.paperfly.PaperFlyApp;
import de.fhb.mi.paperfly.dto.Message;
import de.fhb.mi.paperfly.dto.MessageType;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * A Service which holds the connections to two chats. These chats are connected through a webSocket.
 * On startup the service will connect to the global chat.
 * <p/>
 * If the user wants to join another chat you should call {@link de.fhb.mi.paperfly.service.ChatService#connectToRoom(String, de.fhb.mi.paperfly.service.ChatService.MessageReceiver)}.
 *
 * @author Christoph Ott
 */
public class ChatService extends Service {

    public static final String URL_CHAT_BASE = "ws://" + RestConsumerSingleton.AWS_IP + ":" + RestConsumerSingleton.PORT + "/PaperFlyServer-web/ws/chat/";
    private static final String GLOBAL = "Global";
    public static final String URL_CHAT_GLOBAL = URL_CHAT_BASE + GLOBAL;
    private static final String TAG = ChatService.class.getSimpleName();
    IBinder binder = new ChatServiceBinder();
    private WebSocketConnection globalConnection;
    private WebSocketConnection roomConnection = new WebSocketConnection();
    private String webSocketUriSpecificRoom = "";

    private MessageReceiver currentMessageReceiverGlobal;
    private MessageReceiver currentMessageReceiverSpecific;

    private boolean connectToGlobal() {
        if (globalConnection == null) {
            try {
                globalConnection = new WebSocketConnection();
                globalConnection.connect(URL_CHAT_GLOBAL, null, new MyWebSocketConnectionHandler(URL_CHAT_GLOBAL, RoomType.GLOBAL), new WebSocketOptions(), createHeaders());
            } catch (WebSocketException e) {
                e.printStackTrace();
                return false;
            }
        } else if (!globalConnection.isConnected()) {
            globalConnection.reconnect();
        }
        return true;
    }

    /**
     * This method connects the {@link de.fhb.mi.paperfly.service.ChatService} to the given room and
     * forwards the messages from the connection to the messageReceiver.
     *
     * @param room            the room to connect to and get the messages to the {@link de.fhb.mi.paperfly.service.ChatService.MessageReceiver}
     * @param messageReceiver the messageReceiver which sends the messages to the ui
     *
     * @return true if the connection is established, false if not
     */
    public boolean connectToRoom(final String room, MessageReceiver messageReceiver) {
        RoomType roomType = (room.equalsIgnoreCase(RoomType.GLOBAL.name())) ? RoomType.GLOBAL : RoomType.SPECIFIC;
        boolean success = false;
        switch (roomType) {
            case GLOBAL:
                this.currentMessageReceiverGlobal = messageReceiver;
                success = connectToGlobal();
                break;
            case SPECIFIC:
                this.currentMessageReceiverSpecific = messageReceiver;
                success = connectToSpecific(room);
        }
        return success;
    }

    private boolean connectToSpecific(String room) {
        String webSocketUri = URL_CHAT_BASE + room;
        if (!webSocketUriSpecificRoom.equals(webSocketUri)) {
            // connect to new room
            if (roomConnection.isConnected()) {
                // if there was a connection to another room close it
                roomConnection.disconnect();
            }
            try {
                roomConnection = new WebSocketConnection();
                roomConnection.connect(webSocketUri, null, new MyWebSocketConnectionHandler(webSocketUri, RoomType.SPECIFIC), new WebSocketOptions(), createHeaders());
            } catch (WebSocketException e) {
                e.printStackTrace();
                return false;
            }
            webSocketUriSpecificRoom = webSocketUri;
        } else if (!roomConnection.isConnected()) {
            // room is the same but connection was lost
            roomConnection.reconnect();
        }
        return true;
    }

    private List<BasicNameValuePair> createHeaders() {
        List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
        List<Cookie> cookies = ((PaperFlyApp) getApplication()).getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JSESSIONID")) {
                Log.d(TAG, "Cookie: " + cookie.toString());
                headers.add(new BasicNameValuePair("Cookie", cookie.getName() + "=" + cookie.getValue()));
            }
        }
        return headers;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (globalConnection.isConnected()) {
            globalConnection.disconnect();
        }
        if (roomConnection.isConnected()) {
            roomConnection.disconnect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        connectToGlobal();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Sends a message to the current visible room.
     *
     * @param messageFromUI the message to send
     * @param roomType      the roomType the message should be send
     */
    public void sendTextMessage(String messageFromUI, RoomType roomType) {
        Gson gson = new Gson();
        Message message = new Message("", MessageType.TEXT, new Date(), messageFromUI);
        switch (roomType) {
            case GLOBAL:
                globalConnection.sendTextMessage(gson.toJson(message));
                break;
            case SPECIFIC:
                roomConnection.sendTextMessage(gson.toJson(message));
                break;
        }
    }

    /**
     * Represents the type of a room.
     */
    public enum RoomType {
        GLOBAL, SPECIFIC
    }

    /**
     * An Interface for receiving messages on the ui.
     */
    public interface MessageReceiver {

        /**
         * Gets a message for further processing. (e.g. Displaying on the UI)
         *
         * @param message the message to process
         */
        void receiveMessage(String message);
    }

    /**
     * A Binder for the BackgroundLocationService
     *
     * @see android.os.Binder
     */
    public class ChatServiceBinder extends Binder {
        public ChatService getServiceInstance() {
            return ChatService.this;
        }
    }

    private class MyWebSocketConnectionHandler extends WebSocketConnectionHandler {

        private final String webSocketUri;
        private final RoomType roomType;

        public MyWebSocketConnectionHandler(String webSocketUri, RoomType roomType) {
            this.webSocketUri = webSocketUri;
            this.roomType = roomType;
        }

        @Override
        public void onClose(int code, String reason) {
            Log.d(TAG, "Connection lost to: " + webSocketUri);
        }

        @Override
        public void onOpen() {
            Log.d(TAG, "Status: Connected to " + webSocketUri);
        }

        @Override
        public void onTextMessage(String messageJSON) {
            Log.d(TAG, "Got message: " + messageJSON);
            Gson gson = new Gson();
            Message message = gson.fromJson(messageJSON, Message.class);
            String acutalMessageToUI;
            if (message.getUsername() != null) {
                acutalMessageToUI = message.getUsername() + ": " + message.getBody();
            } else {
                acutalMessageToUI = message.getBody();
            }
            switch (roomType) {
                case GLOBAL:
                    if (currentMessageReceiverGlobal != null) {
                        currentMessageReceiverGlobal.receiveMessage(acutalMessageToUI);
                    }
                    break;
                case SPECIFIC:
                    if (currentMessageReceiverSpecific != null) {
                        currentMessageReceiverSpecific.receiveMessage(acutalMessageToUI);
                    }
                    break;
            }
        }
    }
}
