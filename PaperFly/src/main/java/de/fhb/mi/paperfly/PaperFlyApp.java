package de.fhb.mi.paperfly;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;

import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.service.BackgroundLocationService;
import de.fhb.mi.paperfly.service.RestConsumerSingleton;
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
    private AccountDTO account;

    private final Object lock = new Object();
    private CookieStore cookieStore = null;

    /**
     * Builds a new HttpClient with the same CookieStore than the previous one.
     * This allows to follow the http session, without keeping in memory the
     * full DefaultHttpClient.
     */
    public HttpClient getHttpClient() {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        synchronized (lock) {
            if (cookieStore == null) {
                cookieStore = httpClient.getCookieStore();
            } else {
                httpClient.setCookieStore(cookieStore);
            }
        }
        return httpClient;
    }

    /**
     * Checks if the given Service is running.
     *
     * @param serviceToCheck the service to check
     *
     * @return true if the service is running, false if not
     */
    public boolean isMyServiceRunning(Class<? extends Service> serviceToCheck) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceToCheck.getName().equals(service.service.getClassName())) {
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

        RestConsumerSingleton.getInstance().init(this);
    }
}
