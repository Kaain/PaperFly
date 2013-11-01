package de.fhb.mi.paperfly;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.fhb.mi.paperfly.auth.AuthHelper;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class WebSocketTestMainActivity extends Activity {

    private static final String TAG = "de.tavendo.test1";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private TextView laOutput;
    private EditText inURL;
    private EditText inMessage;
    private Button buGo;

    private void start(final String wsuri, final String message) {


        try {
            mConnection.connect(wsuri, new WebSocketConnectionHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    mConnection.sendTextMessage(message);
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got echo: " + payload);
                    laOutput.setText(payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Toast.makeText(getApplicationContext(), "Connection lost.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websockets);
        inURL = (EditText) findViewById(R.id.inURL);
        inURL.setText(AuthHelper.YOUR_URL);
        inMessage = (EditText) findViewById(R.id.inMessage);
        laOutput = (TextView) findViewById(R.id.laOutput);
        buGo = (Button) findViewById(R.id.buGo);
        buGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnection.isConnected()) {
                    mConnection.sendTextMessage(inMessage.getText().toString());
                } else {
                    start(inURL.getText().toString(), inMessage.getText().toString());
                }

            }
        });

    }

}
