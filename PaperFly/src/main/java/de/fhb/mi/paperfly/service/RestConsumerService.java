package de.fhb.mi.paperfly.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;
import de.fhb.mi.paperfly.PaperFlyApp;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.dto.RegisterAccountDTO;
import de.fhb.mi.paperfly.dto.RoomDTO;
import de.fhb.mi.paperfly.dto.TokenDTO;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Christoph Ott
 */
public class RestConsumerService extends Service implements RestConsumer {


    public static final String URL_LOGIN_BASIC = "http://46.137.173.175:8080/PaperFlyServer-web/secure/";
    public static final String URL_LOGIN = "http://46.137.173.175:8080/PaperFlyServer-web/rest/v1/auth/login";
    public static final String URL_LOGOUT = "http://46.137.173.175:8080/PaperFlyServer-web/rest/v1/auth/logout";
    private static final String TAG = "RestConsumerService";
    IBinder mbinder = new RestConsumerBinder();

    @Override
    public AccountDTO editAccount(AccountDTO account) {
        return null;
    }

    @Override
    public AccountDTO getAccount(String mail) {
        return null;
    }

    @Override
    public AccountDTO getAccountByUsername(String username) {
        return null;
    }

    @Override
    public List<AccountDTO> getAccountsInRoom(long roomID) {
        return null;
    }

    @Override
    public RoomDTO locateAccount(String username) {
        return null;
    }

    @Override
    public boolean login(String mail, String password) {
        Log.d(TAG, "login");
        HttpUriRequest request = new HttpGet(URL_LOGIN); // Or HttpPost(), depends on your needs
        request.addHeader("user", mail);
        request.addHeader("pw", password);

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);


            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "loginUser: " + response.getStatusLine().getStatusCode());
                return false;
                // TODO switch mit status code
            }

            InputStream is = response.getEntity().getContent();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder responseObj = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                responseObj.append(line);
                responseObj.append('\r');
            }
            rd.close();

            Gson gson = new Gson();
            TokenDTO tokendto = gson.fromJson(responseObj.toString(), TokenDTO.class);
            ((PaperFlyApp) getApplication()).setToken(tokendto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mbinder;
    }

    @Override
    public AccountDTO register(RegisterAccountDTO account) {
        return null;
    }

    @Override
    public List<AccountDTO> searchAccount(String query) {
        return null;
    }

    public class RestConsumerBinder extends Binder {
        public RestConsumerService getServerInstance() {
            return RestConsumerService.this;
        }
    }
}
