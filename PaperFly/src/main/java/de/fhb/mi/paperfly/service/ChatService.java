package de.fhb.mi.paperfly.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import de.fhb.mi.paperfly.PaperFlyApp;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;
import lombok.Setter;

/**
 * A Service which holds the connections to two chats. These chats are connected through a webSocket.
 * On startup the service will connect to the global chat.
 * If the user joins another chat the service connects to this chat by calling the {@link de.fhb.mi.paperfly.service.ChatService#connectToRoomChat(String)}.
 * <p/>
 * Inspired by http://stackoverflow.com/questions/4300291/example-communication-between-activity-and-service-using-messaging
 *
 * @author Christoph Ott
 */
public class ChatService extends Service {

    public static final String URL_CHAT_BASE = "ws://" + RestConsumerSingleton.AWS_IP + ":" + RestConsumerSingleton.PORT + "/PaperFlyServer-web/ws/chat/";
    private static final String GLOBAL = "Global";
    private static final String TAG = ChatService.class.getSimpleName();
    public static final String ARGS_WS_URI = "ARGS_WS_URI";
    public static final String ARGS_FROM_SERVICE = "ARGS_FROM_SERVICE";
    public static final int MSG_SEND_TO_UI = 1;
    private WebSocketConnection globalConnection = new WebSocketConnection();
    private WebSocketConnection roomConnection = new WebSocketConnection();
    IBinder binder = new ChatServiceBinder();
    private String webSocketUriSpecificRoom = "";

    @Setter
    private MessageReceiverGlobal currentMessageReceiverGlobal;
    @Setter
    private MessageReceiverSpecific currentMessageReceiverSpecific;

    public void sendTextMessage(String jsonString, RoomType roomType) {
        switch (roomType) {
            case GLOBAL:
                globalConnection.sendTextMessage(jsonString);
                break;
            case SPECIFIC:
                roomConnection.sendTextMessage(jsonString);
                break;
        }
    }

    public enum RoomType {GLOBAL, SPECIFIC}

    private ArrayList<Messenger> clientsRoom = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
    private ArrayList<Messenger> clientsGlobal = new ArrayList<Messenger>();

//    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        try {
            String webSocketUri = URL_CHAT_BASE + GLOBAL;
            WebSocketConnectionHandler webSocketHandler = createWebSocketHandler(GLOBAL, RoomType.GLOBAL);
            globalConnection.connect(webSocketUri, null, webSocketHandler, new WebSocketOptions(), createHeaders());
        } catch (WebSocketException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public boolean connectToRoomChat(final String room) {
        String webSocketUri = URL_CHAT_BASE + room;
        if (!webSocketUriSpecificRoom.equals(webSocketUri)) {
            // connect to new room
            try {
                if (roomConnection.isConnected()) {
                    // if there was a connection to another room close it
                    roomConnection.disconnect();
                }
                roomConnection = new WebSocketConnection();
                roomConnection.connect(webSocketUri, null, createWebSocketHandler(room, RoomType.SPECIFIC), new WebSocketOptions(), createHeaders());
                webSocketUriSpecificRoom = webSocketUri;
            } catch (WebSocketException e) {
                e.printStackTrace();
                return false;
            }
        } else if (!roomConnection.isConnected()) {
            // room is the same but connection was lost
            roomConnection.reconnect();
        }
        return true;
    }

    /**
     * Sends Messages to clients depending on roomType
     *
     * @param message  the message to send
     * @param roomType the roomType of the message
     */
    private void sendMessageToUI(String message, RoomType roomType) {
        Log.d(TAG, "sendMessageToUI: " + message);
        //Send data as a String
        Bundle b = new Bundle();
        b.putString(ARGS_FROM_SERVICE, message);
        android.os.Message msg = android.os.Message.obtain(null, MSG_SEND_TO_UI);
        msg.setData(b);

        switch (roomType) {
            case GLOBAL:
                for (Messenger messenger : clientsGlobal) {
                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                        clientsGlobal.remove(messenger);
                    }
                }
                break;
            case SPECIFIC:
                for (Messenger messenger : clientsRoom) {
                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                        clientsRoom.remove(messenger);
                    }
                }
        }
    }

    /**
     * Register a {@link android.os.Messenger} to the service to get messages from the service.
     *
     * @param messenger the messenger to register
     * @param roomType  the roomType for which the messenger should be register to
     */
    public void registerMessenger(Messenger messenger, RoomType roomType) {
        switch (roomType) {
            case GLOBAL:
                clientsGlobal.add(messenger);
                break;
            case SPECIFIC:
                clientsRoom.add(messenger);
                break;
        }
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

    private WebSocketConnectionHandler createWebSocketHandler(final String room, final RoomType roomType) {
        final String webSocketUri = URL_CHAT_BASE + room;

        return new WebSocketConnectionHandler() {

            @Override
            public void onOpen() {
                Log.d(TAG, "Status: Connected to " + webSocketUri);
            }

            @Override
            public void onTextMessage(String message) {
                Log.d(TAG, "Got message: " + message);
//                sendMessageToUI(message, roomType);
                switch (roomType) {
                    case GLOBAL:
                        if (currentMessageReceiverGlobal != null) {
                            currentMessageReceiverGlobal.receiveMessageGlobal(message);
                        }
                        break;
                    case SPECIFIC:
                        if (currentMessageReceiverSpecific != null) {
                            currentMessageReceiverSpecific.receiveMessageSpecific(message);
                        }
                        break;
                }
            }

            @Override
            public void onClose(int code, String reason) {
                Log.d(TAG, "Connection lost to: " + webSocketUri);
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        globalConnection.disconnect();
        if (roomConnection != null) {
            roomConnection.disconnect();
        }
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

    public interface MessageReceiverGlobal {

        void receiveMessageGlobal(String message);
    }

    public interface MessageReceiverSpecific {

        void receiveMessageSpecific(String message);
    }

}
