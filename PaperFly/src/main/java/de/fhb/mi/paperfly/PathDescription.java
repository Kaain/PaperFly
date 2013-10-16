package de.fhb.mi.paperfly;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PathDescription extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_description);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.path_description, menu);
        return true;
    }
    
}
