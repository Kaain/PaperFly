package de.fhb.mi.paperfly.auth;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import lombok.Cleanup;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
    public static final String YOUR_URL = "http://46.137.173.175:8080/PaperFlyServer-web/secure/";
    public static final String FILE_NAME = "secure";


    private static boolean authenticate(Context context, String encodedCredentials) throws IOException {
        Log.d(TAG, "authenticate with server");
        HttpUriRequest request = new HttpGet(YOUR_URL); // Or HttpPost(), depends on your needs
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
    }

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
}
