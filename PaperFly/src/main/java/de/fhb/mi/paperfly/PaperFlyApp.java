package de.fhb.mi.paperfly;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.dto.TokenDTO;
import de.fhb.mi.paperfly.service.BackgroundLocationService;
import lombok.Getter;
import lombok.Setter;

/**
 * The application for PaperFly.
 *
 * @author Christoph Ott
 * @see android.app.Application
 */
@Getter
@Setter
public class PaperFlyApp extends Application {
    private List<String> chatGlobal;
    private List<String> chatRoom;
    private TokenDTO token;

    /**
     * Checks if the given Service is running.
     *
     * @param serviceToCheck the service to check
     * @return true if the service is running, false if not
     */
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

        chatGlobal = new ArrayList<String>();
        chatRoom = new ArrayList<String>();
    }

    public void setAccount(AccountDTO account) {
        //TODO with lombock
    }
}
