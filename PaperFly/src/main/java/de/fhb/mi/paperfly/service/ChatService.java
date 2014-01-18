package de.fhb.mi.paperfly.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import de.fhb.mi.paperfly.PaperFlyApp;
import de.fhb.mi.paperfly.dto.Message;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * A Service which holds the connections to two chats. These chats are connected through a webSocket.
 * On startup the service will connect to the global chat.
 * If the user joins another chat the service connects to this chat by calling the {@link de.fhb.mi.paperfly.service.ChatService#connectToRoomChat(String)}.
 *
 * @author Christoph Ott
 */
public class ChatService extends Service {

    public static final String URL_CHAT_BASE = "ws://" + RestConsumerSingleton.AWS_IP + ":" + RestConsumerSingleton.PORT + "/PaperFlyServer-web/ws/chat/";
    private static final String TAG = ChatService.class.getSimpleName();
    public static final String ARGS_WS_URI = "ARGS_WS_URI";
    private WebSocketConnection globalConnection;
    private WebSocketConnection roomConnection;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String webSocketUri = intent.getStringExtra(ARGS_WS_URI);
        try {
            roomConnection.connect(webSocketUri, null, createWebSocketHandler(webSocketUri), new WebSocketOptions(), createHeaders());
        } catch (WebSocketException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public boolean connectToRoomChat(final String webSocketUri) {
        return false;
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

    private WebSocketConnectionHandler createWebSocketHandler(final String webSocketUri) {
        return new WebSocketConnectionHandler() {

            @Override
            public void onOpen() {
                Log.d(TAG, "Status: Connected to " + webSocketUri);
            }

            @Override
            public void onTextMessage(String payload) {
                Log.d(TAG, "Got payload: " + payload);
                Gson gson = new Gson();
                Message message = null;
                try {
                    message = gson.fromJson(payload, Message.class);
                    if (message.getUsername() != null) {
//                        messagesAdapter.add(message.getUsername() + ": " + message.getBody());
                    } else {
//                        messagesAdapter.add(message.getBody());
                    }
//                    messagesAdapter.notifyDataSetChanged();
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, e.getMessage());
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
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
