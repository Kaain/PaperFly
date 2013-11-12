package de.fhb.mi.paperfly.auth;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import lombok.Cleanup;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Christoph Ott
 */
public class AuthHelper {

    public static final String TAG = "AuthHelper";
    public static final String URL_LOGIN = "http://46.137.173.175:8080/PaperFlyServer-web/secure/";
    public static final String URL_LOGIN_DIGEST = "http://46.137.173.175:8080/PaperFlyServer-web/rest/service/v1/login";
    public static final String URL_LOGOUT = "http://46.137.173.175:8080/PaperFlyServer-web/rest/service/v1/logout";
    public static final String URL_CHAT_GLOBAL = "ws://46.137.173.175:8080/PaperFlyServer-web/ws/chat/global";
    public static final String FILE_NAME = "secure";

    public static boolean logout() throws IOException {
        HttpUriRequest request = new HttpGet(URL_LOGOUT); // Or HttpPost(), depends on your needs

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            Log.d(TAG, "Logout successful");
            return true;
        } else {
            Log.d(TAG, "Logout not successful");
            return false;
        }
    }

    private static boolean authenticate(Context context, String encodedCredentials) throws IOException {
        Log.d(TAG, "authenticate with server");
        HttpUriRequest request = new HttpGet(URL_LOGIN); // Or HttpPost(), depends on your needs
        request.addHeader("Authorization", "Basic " + encodedCredentials);

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(request);
        Log.d(TAG, response.getStatusLine().getStatusCode() + "");

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String FILENAME = FILE_NAME;

            Log.d(TAG, "write password to file");
            @Cleanup FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(encodedCredentials.getBytes());
            return true;
        }
        return false;
    }

    public static boolean authenticate(Context context, String mail, String password) throws IOException {
        String credentials = mail + ":" + password;
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return authenticate(context, base64EncodedCredentials);

//        try {
//            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//            messageDigest.update(password.getBytes());
//            byte[] byteData = messageDigest.digest();
//            StringBuffer sb = new StringBuffer();
//
//            for (int i = 0; i < byteData.length; i++) {
//                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
//            }
//            return authDigst(context, mail, sb.toString());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return false;
    }

    private static boolean authDigst(Context context, String mail, String hashPw) {
        HttpUriRequest request = new HttpPost(URL_LOGIN_DIGEST);
        return true;
    }

    /**
     * Authenticates the user, if he was logged at anytime.
     *
     * @param context the activity context
     * @return true if the login was successful, false otherwise
     */
    public static boolean authenticate(Context context) {
        Log.d(TAG, "authenticate file");
        try {
            @Cleanup FileInputStream fileInputStream = context.openFileInput(AuthHelper.FILE_NAME);

            int content;
            StringBuilder sb = new StringBuilder();
            while ((content = fileInputStream.read()) != -1) {
                sb.append((char) content);
            }
            return authenticate(context, sb.toString());
        } catch (FileNotFoundException e) {
            Log.d(TAG, "FileNotFound", e);
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
        }
        return false;
    }

    /**
     * Registers an user.
     *
     * @param mail the users email address
     * @param pw   the password
     * @return true if registration was successful, false otherwise.
     */
    public static AuthStatus register(String mail, String pw) {
        // TODO implement register

        boolean valid = false;
        if (valid) {
            return AuthStatus.REGISTER_SUCCESSFUL;
        } else {
            return AuthStatus.REGISTER_EMAIL_ALREADY_REGISTERED;
        }
    }
}
