/*
 * Copyright (C) 2013 Michael Koppen, Christoph Ott, Andy Klay
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.fhb.mi.paperfly.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

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
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import de.fhb.mi.paperfly.PaperFlyApp;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.dto.RegisterAccountDTO;
import de.fhb.mi.paperfly.dto.RoomDTO;
import de.fhb.mi.paperfly.dto.TokenDTO;

/**
 * This Class implements the connection to the REST-Service of the PaperFly-Server
 *
 * @author Christoph Ott
 * @author Andy Klay klay@fh-brandenburg.de
 */
public class RestConsumerService extends Service implements RestConsumer {


//    public static final String URL_LOGIN_BASIC = "http://46.137.173.175:8080/PaperFlyServer-web/secure/";
//    public static final String URL_LOGIN = "http://46.137.173.175:8080/PaperFlyServer-web/rest/v1/auth/login";
//    public static final String URL_GET_ACCOUNT = "http://46.137.173.175:8080/PaperFlyServer-web/rest/v1/account/";
//    public static final String URL_LOGOUT = "http://46.137.173.175:8080/PaperFlyServer-web/rest/v1/auth/logout";

    public static final String URL_LOGIN_BASIC = "http://10.0.2.2:8080/PaperFlyServer-web/secure/";
    public static final String URL_LOGIN = "http://10.0.2.2:8080/PaperFlyServer-web/rest/v1/auth/login";
    public static final String URL_GET_ACCOUNT = "http://10.0.2.2:8080/PaperFlyServer-web/rest/v1/account/";
    public static final String URL_LOGOUT = "http://10.0.2.2:8080/PaperFlyServer-web/rest/v1/auth/logout";


    private static final String TAG = "RestConsumerService";
    IBinder mbinder = new RestConsumerBinder();

    @Override
    public AccountDTO editAccount(AccountDTO account) {
        Log.d(TAG, "getAccount");

        return null;
    }

    @Override
    public AccountDTO getAccount(String mail) {
        Log.d(TAG, "getAccount");

        return null;
    }

    @Override
    public AccountDTO getAccountByUsername(String username) {
        Log.d(TAG, "getAccountByUsername");

        HttpUriRequest request = new HttpGet(URL_GET_ACCOUNT + username);
//        request.setHeader(((PaperFlyApp) getApplication()).getToken());
//        request.addHeader("user", mail);
//        request.addHeader("pw", password);
        AccountDTO account = null;

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "getAccountByUsername: " + response.getStatusLine().getStatusCode());
                return null;
                // TODO switch mit status code
//                switch (response.getStatusLine().getStatusCode()) {
//                    case HttpStatus.SC_BAD_GATEWAY:
//                        ;
//                        break;
//                    case HttpStatus.SC_ACCEPTED:
//                        ;
//                        break;
//                    case HttpStatus.SC_BAD_REQUEST:
//                        ;
//                        break;
//                    case HttpStatus.SC_CONTINUE:
//                        ;
//                        break;
//                    case HttpStatus.SC_CONFLICT:
//                        ;
//                        break;
//                    //...
//                }
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

            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            Log.d("json", responseObj.toString());
            account = gson.fromJson(responseObj.toString(), AccountDTO.class);
//            ((PaperFlyApp) getApplication()).setAccount(account);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public List<AccountDTO> getAccountsInRoom(long roomID) {
        Log.d(TAG, "getAccountsInRoom");

        return null;
    }

    @Override
    public RoomDTO locateAccount(String username) {
        Log.d(TAG, "locateAccount");

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

    public class JsonDateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String dateAsString = json.getAsJsonPrimitive().getAsString();
            long dateAsLong = Long.parseLong(dateAsString.substring(6, dateAsString.length() - 2));
            Date date = new Date(dateAsLong);
            return date;
        }
    }
}
