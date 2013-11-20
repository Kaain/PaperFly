package de.fhb.mi.paperfly;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import de.fhb.mi.paperfly.dto.TokenDTO;
import de.fhb.mi.paperfly.service.BackgroundLocationService;
import de.fhb.mi.paperfly.service.RestConsumerService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Ott
 */

public class PaperFlyApp extends Application {
    @Getter
    @Setter
    private List<String> chatGlobal;
    @Getter
    @Setter
    private List<String> chatRoom;
    @Getter
    @Setter
    private TokenDTO token;

    public boolean isMyServiceRunning(Service serviceToCheck) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceToCheck.getClass().getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BackgroundLocationService.servicesAvailable(this)) {
            startService(new Intent(this, BackgroundLocationService.class));
        }
        startService(new Intent(this, RestConsumerService.class));

        chatGlobal = new ArrayList<String>();
        chatRoom = new ArrayList<String>();
    }
}
