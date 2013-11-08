package de.fhb.mi.paperfly;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Ott
 */
public class PaperFlyApp extends Application {
    private List<String> chatGlobal;
    private List<String> chatRoom;

    @Override
    public void onCreate() {
        super.onCreate();
        chatGlobal = new ArrayList<String>();
        chatRoom = new ArrayList<String>();
    }

    public List<String> getChatGlobal() {
        return chatGlobal;
    }

    public void setChatGlobal(List<String> chatGlobal) {
        this.chatGlobal = chatGlobal;
    }

    public List<String> getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(List<String> chatRoom) {
        this.chatRoom = chatRoom;
    }
}
