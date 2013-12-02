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

import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.fhb.mi.paperfly.PaperFlyApp;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.dto.RegisterAccountDTO;
import de.fhb.mi.paperfly.dto.RoomDTO;
import de.fhb.mi.paperfly.dto.TokenDTO;

/**
 * This Class implements the connection to the REST-Service of the PaperFly-Server.
 *
 * @author Christoph Ott
 * @author Andy Klay (klay@fh-brandenburg.de)
 */
public class RestConsumerService extends Service implements RestConsumer {

    public static final String LOCAL_IP = "10.0.2.2";
    public static final String AWS_IP = "46.137.173.175";
    public static final boolean CONNECT_LOCAL = true;
    public static final String PORT = "8080";

    public static final String URL_LOGIN_BASIC = "PaperFlyServer-web/secure/";
    public static final String URL_LOGIN = "PaperFlyServer-web/rest/v1/auth/login";
    public static final String URL_GET_ACCOUNT = "PaperFlyServer-web/rest/v1/account/";
    public static final String URL_LOGOUT = "PaperFlyServer-web/rest/v1/auth/logout";

    public static final String URL_REGISTER_ACCOUNT = "PaperFlyServer-web/rest/v1/account/register";
    public static final String URL_SEARCH_ACCOUNT = "PaperFlyServer-web/rest/v1/account/search/";
    public static final String URL_EDIT_ACCOUNT = "PaperFlyServer-web/rest/v1/myaccount/edit";
    public static final String URL_ADD_FRIEND = "PaperFlyServer-web/rest/v1/myaccount/friend/";
    public static final String URL_ACCOUNTS_IN_ROOM = "PaperFlyServer-web/rest/v1/room/accounts/";
    public static final String URL_LOCATE_ACCOUNT = "PaperFlyServer-web/rest/v1/room/locateAccount/";

    private static final String TAG = "RestConsumerService";
    IBinder mbinder = new RestConsumerBinder();

    @Override
    public AccountDTO editAccount(AccountDTO editedAccount) throws RestConsumerException, UnsupportedEncodingException {
        Log.d(TAG, "getAccountByMail");

        AccountDTO responseAccount = null;
        HttpUriRequest request = new HttpPost(getConnectionURL(URL_EDIT_ACCOUNT));

        Gson sendMapper = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateSerializer()).create();

        String jsonToSend = sendMapper.toJson(editedAccount);
        StringEntity entityToSend = new StringEntity(jsonToSend);
        Log.d(TAG, jsonToSend);

        entityToSend.setContentEncoding("UTF-8");
        entityToSend.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        ((HttpPut) request).setEntity(entityToSend);
        request.addHeader("accept", "application/json");

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "getAccountByUsername: " + response.getStatusLine().getStatusCode());
                switch (response.getStatusLine().getStatusCode()) {
                    case 412:
                        throw new RestConsumerException(RestConsumerException.INVALID_INPUT_MESSAGE);
                    case 500:
                        throw new RestConsumerException(RestConsumerException.INTERNAL_SERVER_MESSAGE);
                    default:
                        throw new RestConsumerException("Response:" + response.getStatusLine().getStatusCode());
                }
            }

            String responseObjAsString = readInEntity(response);
            Log.d("json", responseObjAsString);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            responseAccount = gson.fromJson(responseObjAsString, AccountDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseAccount;
    }

    @Override
    public AccountDTO getAccountByMail(String mail) throws RestConsumerException {
        Log.d(TAG, "getAccountByMail");

        //TODO not yet implemented
        HttpUriRequest request = new HttpGet(getConnectionURL(URL_SEARCH_ACCOUNT) + mail);
        AccountDTO account = null;

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "getAccountByMail: " + response.getStatusLine().getStatusCode());
                switch (response.getStatusLine().getStatusCode()) {
                    case 500:
                        throw new RestConsumerException(RestConsumerException.INTERNAL_SERVER_MESSAGE);
                    default:
                        return null;
                }
            }

            String responseObjAsString = readInEntity(response);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            Log.d(TAG, "json: " + responseObjAsString);

            account = gson.fromJson(responseObjAsString, AccountDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public AccountDTO getAccountByUsername(String username) throws RestConsumerException {
        Log.d(TAG, "getAccountByUsername");

        HttpUriRequest request = new HttpGet(getConnectionURL(URL_GET_ACCOUNT) + username);
        AccountDTO account = null;

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "getAccountByUsername: " + response.getStatusLine().getStatusCode());
                switch (response.getStatusLine().getStatusCode()) {
                    case 500:
                        throw new RestConsumerException(RestConsumerException.INTERNAL_SERVER_MESSAGE);
                    default:
                        throw new RestConsumerException("Response:" + response.getStatusLine().getStatusCode());
                }
            }

            String responseObjAsString = readInEntity(response);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            Log.d(TAG, "json: " + responseObjAsString);

            account = gson.fromJson(responseObjAsString, AccountDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public List<AccountDTO> getAccountsInRoom(long roomID) throws RestConsumerException {
        Log.d(TAG, "getAccountsInRoom");

        HttpUriRequest request = new HttpGet(getConnectionURL(URL_ACCOUNTS_IN_ROOM) + roomID);
        List<AccountDTO> accountsInRoom = new ArrayList<AccountDTO>();
        Type collectionType = new TypeToken<ArrayList<AccountDTO>>(){}.getType();

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "getAccountsInRoom: " + response.getStatusLine().getStatusCode());
                switch (response.getStatusLine().getStatusCode()) {
                    case 500:
                        throw new RestConsumerException(RestConsumerException.INTERNAL_SERVER_MESSAGE);
                    default:
                        throw new RestConsumerException("Response:" + response.getStatusLine().getStatusCode());
                }
            }

            String responseObjAsString = readInEntity(response);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            Log.d(TAG, "json: " + responseObjAsString);

            accountsInRoom = gson.fromJson(responseObjAsString, collectionType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return accountsInRoom;
    }

    @Override
    public RoomDTO locateAccount(String username) throws RestConsumerException {
        Log.d(TAG, "locateAccount");

        HttpUriRequest request = new HttpGet(getConnectionURL(URL_LOCATE_ACCOUNT) + username);
        RoomDTO room = null;

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "locateAccount: " + response.getStatusLine().getStatusCode());
                switch (response.getStatusLine().getStatusCode()) {
                    case 500:
                        throw new RestConsumerException(RestConsumerException.INTERNAL_SERVER_MESSAGE);
                    default:
                        throw new RestConsumerException("Response:" + response.getStatusLine().getStatusCode());
                }
            }

            String responseObjAsString = readInEntity(response);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            Log.d(TAG, "json: " + responseObjAsString);

            room = gson.fromJson(responseObjAsString, RoomDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return room;

    }

    @Override
    public boolean login(String mail, String password) throws RestConsumerException {
        Log.d(TAG, "login");
        HttpUriRequest request = new HttpGet(getConnectionURL(URL_LOGIN)); // Or HttpPost(), depends on your needs
        request.addHeader("user", mail);
        request.addHeader("pw", password);

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "loginUser: " + response.getStatusLine().getStatusCode());
                switch (response.getStatusLine().getStatusCode()) {
                    case 500:
                        throw new RestConsumerException(RestConsumerException.INTERNAL_SERVER_MESSAGE);
                    default:
                        return false;
                }
            }

            String responseObjAsString = readInEntity(response);
            Gson gson = new Gson();
            TokenDTO tokendto = gson.fromJson(responseObjAsString, TokenDTO.class);

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
    public TokenDTO register(RegisterAccountDTO registerAccount) throws UnsupportedEncodingException, RestConsumerException {

        TokenDTO requestToken = null;
        HttpUriRequest request = new HttpPut(getConnectionURL(URL_REGISTER_ACCOUNT));

        Gson sendMapper = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateSerializer()).create();

        String jsonToSend = sendMapper.toJson(registerAccount);
        StringEntity entityToSend = new StringEntity(jsonToSend);
        Log.d(TAG, jsonToSend);

        entityToSend.setContentEncoding("UTF-8");
        entityToSend.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        ((HttpPut) request).setEntity(entityToSend);
        request.addHeader("accept", "application/json");

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "getAccountByUsername: " + response.getStatusLine().getStatusCode());
                switch (response.getStatusLine().getStatusCode()) {
                    case 412:
                        throw new RestConsumerException(RestConsumerException.INVALID_INPUT_MESSAGE);
                    case 500:
                        throw new RestConsumerException(RestConsumerException.INTERNAL_SERVER_MESSAGE);
                    default:
                        throw new RestConsumerException("Response:" + response.getStatusLine().getStatusCode());
                }
            }

            String responseObjAsString = readInEntity(response);
            Log.d("json", responseObjAsString);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            requestToken = gson.fromJson(responseObjAsString, TokenDTO.class);

            ((PaperFlyApp) getApplication()).setToken(requestToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestToken;
    }

    @Override
    public List<AccountDTO> searchAccount(String query)throws RestConsumerException{

        Log.d(TAG, "searchAccount");

        HttpUriRequest request = new HttpGet(getConnectionURL(URL_SEARCH_ACCOUNT) + query);
        List<AccountDTO> searchResultList = new ArrayList<AccountDTO>();
        Type collectionType = new TypeToken<ArrayList<AccountDTO>>(){}.getType();

        Log.d(TAG, request.getRequestLine().toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.d(TAG, "searchAccount: " + response.getStatusLine().getStatusCode());
                switch (response.getStatusLine().getStatusCode()) {
                    case 500:
                        throw new RestConsumerException(RestConsumerException.INTERNAL_SERVER_MESSAGE);
                    default:
                        throw new RestConsumerException("Response:" + response.getStatusLine().getStatusCode());
                }
            }

            String responseObjAsString = readInEntity(response);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            Log.d(TAG, "json: " + responseObjAsString);

            searchResultList = gson.fromJson(responseObjAsString, collectionType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResultList;
    }

    private String readInEntity(HttpResponse response) throws IOException {
        InputStream is = response.getEntity().getContent();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder responseObj = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            responseObj.append(line);
            responseObj.append('\r');
        }
        rd.close();
        return responseObj.toString();
    }

    public String getConnectionURL(String restURL) {

        StringBuilder urlToBuild = new StringBuilder();
        urlToBuild.append("http://");

        if (CONNECT_LOCAL) {
            urlToBuild.append(LOCAL_IP);
        } else {
            urlToBuild.append(AWS_IP);
        }

        urlToBuild.append(":" + PORT + "/");
        urlToBuild.append(restURL);

        return urlToBuild.toString();
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

    public class JsonDateSerializer implements JsonSerializer<Date> {
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.getTime());
        }
    }
}
