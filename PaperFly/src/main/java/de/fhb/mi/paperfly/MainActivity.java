package de.fhb.mi.paperfly;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TITLE_LEFT_DRAWER = "Navigation";
    private static final String TITLE_RIGHT_DRAWER = "Status";
    private DrawerLayout drawerLayout;
    private ListView drawerRightList;
    private ListView drawerLeftList;
    private List<String> drawerRightValues;
    private List<String> drawerLeftValues;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence mTitle;
    private ListView messagesList;
    private EditText messageInput;
    private ImageButton buSend;
    private ArrayAdapter<String> messagesAdapter;
    private View testMapsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewsById();

        // DUMMY DATA
        drawerRightValues = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            drawerRightValues.add(TITLE_RIGHT_DRAWER + i);
        }
        drawerLeftValues = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            drawerLeftValues.add(TITLE_LEFT_DRAWER + i);
        }

        messagesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    buSend.setAlpha(1.0f);
                    buSend.setClickable(true);
                } else {
                    buSend.setAlpha(0.5f);
                    buSend.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // make button not clickable
        buSend.setAlpha(0.5f);
        buSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesAdapter.add(messageInput.getText().toString());
                messagesAdapter.notifyDataSetChanged();
                messageInput.setText("");
            }
        });
        buSend.setClickable(false);


        // make button not clickable
        testMapsButton.setAlpha(0.5f);
        testMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check out new activity
                startActivity(new Intent(MainActivity.this, PathDescription.class));
            }
        });
        testMapsButton.setClickable(false);

        messagesList.setAdapter(messagesAdapter);
        messagesList.setStackFromBottom(true);
        messagesList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        mTitle = getTitle();

        drawerToggle = createActionBarDrawerToggle();
        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        drawerRightList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerRightValues));
        // Set the list's click listener
        drawerRightList.setOnItemClickListener(new DrawerItemClickListener());

        drawerLeftList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerLeftValues));
        // Set the list's click listener
        drawerLeftList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     */
    private void initViewsById() {
        messagesList = (ListView) findViewById(R.id.messagesList);
        messageInput = (EditText) findViewById(R.id.messageInput);
        buSend = (ImageButton) findViewById(R.id.buSend);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerRightList = (ListView) findViewById(R.id.right_drawer);
        drawerLeftList = (ListView) findViewById(R.id.left_drawer);
         testMapsButton = findViewById(R.id.testMapsButton);
    }

    private ActionBarDrawerToggle createActionBarDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if (item != null && item.getItemId() == android.R.id.home && drawerToggle.isDrawerIndicatorEnabled()) {
                    if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
                        drawerLayout.closeDrawer(Gravity.LEFT);
                    } else if (drawerLayout.isDrawerVisible(Gravity.RIGHT)) {
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                        drawerLayout.openDrawer(Gravity.LEFT);
                    } else {
                        drawerLayout.openDrawer(Gravity.LEFT);
                    }
                    return true;
                } else {
                    return false;
                }
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    getActionBar().setTitle(TITLE_RIGHT_DRAWER);
                }
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    getActionBar().setTitle(TITLE_LEFT_DRAWER);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerToggle.onOptionsItemSelected(item);
                return true;
            case R.id.action_scanQR:
                return doQRScan();
            case R.id.action_search:
                //Toast.makeText(this, "Ich will was suchen aber es geht noch nicht!", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(MainActivity.this, PathDescription.class));
                startActivity(new Intent(MainActivity.this, NeueActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates a new Intent for QR scan.
     *
     * @return true if the scan was successful, false if not
     */
    private boolean doQRScan() {
        PackageManager pm = this.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
            return true;
        }else{
            Toast.makeText(this, "Keine Kamera da :(", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast.makeText(this, contents, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putInt(PersonFragment.ARG_PERSON_NUMBER, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        drawerRightList.setItemChecked(position, true);
        setTitle(drawerRightValues.get(position));
        drawerLayout.closeDrawer(drawerRightList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

}
