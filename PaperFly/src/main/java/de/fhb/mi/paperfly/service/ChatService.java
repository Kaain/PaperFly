package de.fhb.mi.paperfly.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.fhb.mi.paperfly.PaperFlyApp;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.dto.Message;
import de.fhb.mi.paperfly.dto.MessageType;
import de.fhb.mi.paperfly.dto.RoomDTO;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;
import lombok.Getter;

/**
 * A Service which holds the connections to two chats. These chats are connected through a webSocket.
 * <p/>
 * If the user wants to join another chat you should call {@link de.fhb.mi.paperfly.service.ChatService#connectToRoom(String, de.fhb.mi.paperfly.service.ChatService.MessageReceiver)}.
 *
 * @author Christoph Ott
 */
public class ChatService extends Service {

    public static final String URL_CHAT_BASE = "ws://" + RestConsumerSingleton.AWS_IP + ":" + RestConsumerSingleton.PORT + "/PaperFlyServer-web/ws/chat/";
    private static final String TAG = ChatService.class.getSimpleName();
    private static final int UPDATE_INTERVAL = 1000 * 10;
    public static String ROOM_GLOBAL_NAME = "Global";
    public static final String URL_CHAT_GLOBAL = URL_CHAT_BASE + ROOM_GLOBAL_NAME;
    public static long ROOM_GLOBAL_ID = 1l;
    IBinder binder = new ChatServiceBinder();
    private WebSocketConnection globalConnection = null;
    private WebSocketConnection roomConnection = new WebSocketConnection();
    private String webSocketUriSpecificRoom = "";
    @Getter
    private RoomDTO actualRoom;

    private MessageReceiver currentMessageReceiverGlobal;
    private MessageReceiver currentMessageReceiverSpecific;

    private Handler globalHandler = new Handler();
    private Handler specificHandler = new Handler();

    private Runnable globalRunnable = new GlobalRunnable();
    private Runnable specificRunnable = new SpecificRunnable();

    private List<AccountDTO> usersInGlobal = new ArrayList<AccountDTO>();
    private List<AccountDTO> usersInSpecific = new ArrayList<AccountDTO>();

    @Getter
    private List<Message> globalMessages = new ArrayList<Message>();
    @Getter
    private List<Message> specificMessages = new ArrayList<Message>();

    private Timer globalTimer;
    private Timer specificTimer;

    private boolean globalTimerRunning = false;
    private boolean specificTimerRunning = false;

    private boolean globalDisconnectAfterTimeout = false;
    private boolean specificDisconnectAfterTimeout = false;

    private boolean connectToGlobal() {
        if (globalConnection == null) {
            try {
                globalMessages.clear();
                globalConnection = new WebSocketConnection();
                globalConnection.connect(URL_CHAT_GLOBAL, null, new MyWebSocketConnectionHandler(URL_CHAT_GLOBAL, RoomType.GLOBAL), new WebSocketOptions(), createHeaders());
            } catch (WebSocketException e) {
                e.printStackTrace();
                return false;
            }
        } else if (!globalConnection.isConnected()) {
            globalConnection = new WebSocketConnection();
            try {
                globalConnection.connect(URL_CHAT_GLOBAL, null, new MyWebSocketConnectionHandler(URL_CHAT_GLOBAL, RoomType.GLOBAL), new WebSocketOptions(), createHeaders());
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
        } else if (!globalTimerRunning) {
            startGlobalTimer();
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
                actualRoom = ((PaperFlyApp) getApplication()).getActualRoom();
                break;
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
                specificMessages.clear();
                roomConnection = new WebSocketConnection();
                roomConnection.connect(webSocketUri, null, new MyWebSocketConnectionHandler(webSocketUri, RoomType.SPECIFIC), new WebSocketOptions(), createHeaders());
            } catch (WebSocketException e) {
                e.printStackTrace();
                return false;
            }
            webSocketUriSpecificRoom = webSocketUri;
        } else if (!roomConnection.isConnected()) {
            // room is the same but connection was lost
            roomConnection = new WebSocketConnection();
            try {
                roomConnection.connect(webSocketUri, null, new MyWebSocketConnectionHandler(webSocketUri, RoomType.SPECIFIC), new WebSocketOptions(), createHeaders());
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
        } else if (!specificTimerRunning) {
            startSpecificTimer();
        }
        return true;
    }

    private List<BasicNameValuePair> createHeaders() {
        List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
        List<Cookie> cookies = RestConsumerSingleton.getInstance().getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JSESSIONID")) {
                Log.d(TAG, "Websocket Cookie: " + cookie.toString());
                headers.add(new BasicNameValuePair("Cookie", cookie.getName() + "=" + cookie.getValue()));
            }
        }
        return headers;
    }

    public void disconnectAfterTimeout() {
        if (globalConnection.isConnected() && globalConnection != null) {
            globalConnection.disconnect();
            globalConnection = null;
            globalDisconnectAfterTimeout = true;
        }
        if (roomConnection.isConnected() && roomConnection != null) {
            roomConnection.disconnect();
            specificDisconnectAfterTimeout = true;
        }
    }

    /**
     * disconnect all connections
     */
    public void disconnectConnections() {
        if (globalConnection != null && globalConnection.isConnected()) {
            globalConnection.disconnect();
        }
        if (globalTimer != null && globalTimerRunning) {
            globalTimer.cancel();
        }
        if (roomConnection != null && roomConnection.isConnected()) {
            roomConnection.disconnect();
        }
        if (specificTimer != null && specificTimerRunning) {
            specificTimer.cancel();
        }
    }

    /**
     * Method for getting the current users in the given room.
     * Should only be called if the chat is connected.
     *
     * @param roomType the chat type you want the online users
     *
     * @return a list of accounts who are online in the chat
     */
    public List<AccountDTO> getUsersInRoom(RoomType roomType) {
        if (roomType == RoomType.GLOBAL) {
            return usersInGlobal;
        } else if (roomType == RoomType.SPECIFIC) {
            return usersInSpecific;
        } else {
            return new ArrayList<AccountDTO>();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        disconnectConnections();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnBind");
        stopTimers();
        currentMessageReceiverGlobal = null;
        currentMessageReceiverSpecific = null;
        return true;
    }

    public void reconnect() {
        if (globalConnection.isConnected() && globalConnection != null) {
            globalConnection.reconnect();
        }
        if (roomConnection.isConnected() && roomConnection != null) {
            roomConnection.reconnect();
        }
    }

    /**
     * Sends a message to the current visible room.
     *
     * @param messageFromUI the message to send
     * @param roomType      the roomType the message should be send
     */
    public void sendTextMessage(String messageFromUI, RoomType roomType) {
        Log.d(TAG, "sendTextMessage: " + messageFromUI);
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

    private void startGlobalTimer() {
        globalTimer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                globalRunnable = new GlobalRunnable();
                globalHandler.post(globalRunnable);
            }
        };
        globalTimer.schedule(doAsynchronousTask, 0, UPDATE_INTERVAL);
        globalTimerRunning = true;
    }

    private void startSpecificTimer() {
        specificTimer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                specificRunnable = new SpecificRunnable();
                specificHandler.post(specificRunnable);
            }
        };
        specificTimer.schedule(doAsynchronousTask, 0, UPDATE_INTERVAL);
        specificTimerRunning = true;
    }

    private void stopTimer(RoomType roomType) {
        switch (roomType) {
            case GLOBAL:
                if (globalTimer != null && globalTimerRunning) {
                    Log.d(TAG, "stopTimer GLOBAL");
                    globalTimer.cancel();
                    globalTimerRunning = false;
                }
                break;
            case SPECIFIC:
                if (specificTimer != null && specificTimerRunning) {
                    Log.d(TAG, "stopTimer SPECIFIC");
                    specificTimer.cancel();
                    specificTimerRunning = false;
                }
                break;
        }
    }

    public void stopTimers() {
        Log.d(TAG, "stopTimers");
        stopTimer(RoomType.GLOBAL);
        stopTimer(RoomType.SPECIFIC);
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
         * This method is called when the users in room list is updated
         *
         * @param usersInRoom the users in the room
         */
        void onUserListUpdated(List<AccountDTO> usersInRoom);

        /**
         * Gets a message for further processing. (e.g. Displaying on the UI)
         *
         * @param message the message to process
         */
        void receiveMessage(Message message);
    }

    /**
     * AsyncTask for getting accounts in room
     */
    public class GetAccountsInRoomTask extends AsyncTask<String, Void, Boolean> {

        private long roomID;

        public GetAccountsInRoomTask(long roomID) {
            this.roomID = roomID;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (RestConsumerSingleton.getInstance().getConsumer() != null) {
                try {
                    if (roomID == 1) {
                        usersInGlobal = RestConsumerSingleton.getInstance().getUsersInRoom(roomID);
                        if (currentMessageReceiverGlobal != null && globalTimerRunning) {
                            currentMessageReceiverGlobal.onUserListUpdated(usersInGlobal);
                        }
                    } else {
                        usersInSpecific = RestConsumerSingleton.getInstance().getUsersInRoom(roomID);
                        if (currentMessageReceiverSpecific != null && specificTimerRunning)
                            currentMessageReceiverSpecific.onUserListUpdated(usersInSpecific);
                    }
                } catch (RestConsumerException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            return true;
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

    /**
     * Class for managing the connection for a webSocket.
     */
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
            switch (roomType) {
                case GLOBAL:
                    globalMessages.clear();
                    stopTimer(RoomType.GLOBAL);
                    if (globalDisconnectAfterTimeout) {
                        new Runnable() {
                            @Override
                            public void run() {
                                // Sleep to be sure the webSocket is closed
                                SystemClock.sleep(3000);
                                connectToGlobal();
                            }
                        }.run();
                        globalDisconnectAfterTimeout = false;
                    }
                    break;
                case SPECIFIC:
                    specificMessages.clear();
                    stopTimer(RoomType.SPECIFIC);
                    if (specificDisconnectAfterTimeout) {
                        new Runnable() {
                            @Override
                            public void run() {
                                // Sleep to be sure the webSocket is closed
                                SystemClock.sleep(3000);
                                connectToSpecific(actualRoom.getName());
                            }
                        }.run();
                        specificDisconnectAfterTimeout = false;
                    }
                    break;
            }
        }

        @Override
        public void onOpen() {
            Log.d(TAG, "Status: Connected to " + webSocketUri);
            switch (roomType) {
                case GLOBAL:
                    startGlobalTimer();
                    break;
                case SPECIFIC:
                    startSpecificTimer();
                    break;
            }
        }

        @Override
        public void onTextMessage(String messageJSON) {
            Log.d(TAG, "Got message: " + messageJSON);
            Gson gson = new Gson();
            Message message = gson.fromJson(messageJSON, Message.class);

            switch (roomType) {
                case GLOBAL:
                    globalMessages.add(message);
                    if (currentMessageReceiverGlobal != null) {
                        currentMessageReceiverGlobal.receiveMessage(message);
                    }
                    break;
                case SPECIFIC:
                    specificMessages.add(message);
                    if (currentMessageReceiverSpecific != null) {
                        currentMessageReceiverSpecific.receiveMessage(message);
                    }
                    break;
            }
        }
    }

    /**
     * Class responsible for executing the users in global room task.
     */
    private class GlobalRunnable implements Runnable {
        @Override
        public void run() {
            GetAccountsInRoomTask getAccountsInRoomTask = new GetAccountsInRoomTask(ROOM_GLOBAL_ID);
            getAccountsInRoomTask.execute();
        }
    }

    /**
     * Class responsible for executing the users in specific room task.
     */
    private class SpecificRunnable implements Runnable {
        @Override
        public void run() {
            if (actualRoom != null) {
                GetAccountsInRoomTask getAccountsInRoomTask = new GetAccountsInRoomTask(actualRoom.getId());
                getAccountsInRoomTask.execute();
            }
        }
    }
}
