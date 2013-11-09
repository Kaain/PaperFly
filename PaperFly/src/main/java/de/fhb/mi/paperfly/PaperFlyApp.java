package de.fhb.mi.paperfly;

import android.app.Application;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Ott
 */
@Getter
@Setter
public class PaperFlyApp extends Application {
    private List<String> chatGlobal;
    private List<String> chatRoom;

    @Override
    public void onCreate() {
        super.onCreate();
        chatGlobal = new ArrayList<String>();
        chatRoom = new ArrayList<String>();
    }
}
