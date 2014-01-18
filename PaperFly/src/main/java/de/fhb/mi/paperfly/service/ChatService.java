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
    private static final String ARGS_FROM_SERVICE = "ARGS_FROM_SERVICE";
    private static final int MSG_SEND_TO_UI = 1;
    private WebSocketConnection globalConnection = new WebSocketConnection();
    private WebSocketConnection roomConnection = new WebSocketConnection();
    IBinder binder = new ChatServiceBinder();

    private enum RoomType {GLOBAL, SPECIFIC}

    private ArrayList<Messenger> mClientsRoom = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
    private ArrayList<Messenger> mClientsGlobal = new ArrayList<Messenger>();

//    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
        try {
            roomConnection.connect(webSocketUri, null, createWebSocketHandler(room, RoomType.SPECIFIC), new WebSocketOptions(), createHeaders());
        } catch (WebSocketException e) {
            e.printStackTrace();
            return false;
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
        //Send data as a String
        Bundle b = new Bundle();
        b.putString(ARGS_FROM_SERVICE, message);
        android.os.Message msg = android.os.Message.obtain(null, MSG_SEND_TO_UI);
        msg.setData(b);

        switch (roomType) {
            case GLOBAL:
                for (Messenger messenger : mClientsGlobal) {
                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                        mClientsGlobal.remove(messenger);
                    }
                }
                break;
            case SPECIFIC:
                for (Messenger messenger : mClientsRoom) {
                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                        mClientsRoom.remove(messenger);
                    }
                }
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
                sendMessageToUI(message, roomType);
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
}
