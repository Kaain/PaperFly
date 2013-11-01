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
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import de.fhb.mi.paperfly.auth.AuthHelper;
import de.fhb.mi.paperfly.auth.LoginActivity;
import de.fhb.mi.paperfly.fragments.ChatFragment;
import de.fhb.mi.paperfly.navigation.NavItemModel;
import de.fhb.mi.paperfly.navigation.NavKey;
import de.fhb.mi.paperfly.navigation.NavListAdapter;
import de.fhb.mi.paperfly.navigation.NavListAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String TITLE_LEFT_DRAWER = "Navigation";
    private static final String TITLE_RIGHT_DRAWER = "Status";
    private static final int REQUESTCODE_QRSCAN = 100;
    private DrawerLayout drawerLayout;
    private ListView drawerRightList;
    private ListView drawerLeftList;
    private List<String> drawerRightValues;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        initViewsById();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if (!AuthHelper.authenticate(MainActivity.this)) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        });
        thread.start();
        // DUMMY DATA
        drawerRightValues = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            drawerRightValues.add(TITLE_RIGHT_DRAWER + i);
        }
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
        drawerRightList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        // Set the list's click listener
        drawerLeftList.setOnItemClickListener(new DrawerItemClickListener());

        generateNavigation();

        if (savedInstanceState == null) {
//            navigateTo(getResources().getString(R.string.nav_item_global));
            navigateTo(NavKey.GLOABAL);
        }
    }

    private void generateNavigation() {
        Log.d(TAG, "generateNavigation");
        NavListAdapter mAdapter = new NavListAdapter(this);
        mAdapter.addHeader(this.getResources().getString(R.string.nav_header_general));
        mAdapter.addItem(NavKey.CHECK_PRESENCE, this.getResources().getString(R.string.nav_item_check_presence), -1);

        mAdapter.addHeader(this.getResources().getString(R.string.nav_header_chats));
        mAdapter.addItem(NavKey.GLOABAL, this.getResources().getString(R.string.nav_item_global), -1);
        mAdapter.addItem(NavKey.ENTER_ROOM, this.getResources().getString(R.string.nav_item_enter_room), android.R.drawable.ic_menu_camera);

        mAdapter.addHeader(this.getResources().getString(R.string.nav_header_help));
        mAdapter.addItem(NavKey.ABOUT, this.getResources().getString(R.string.nav_item_about), android.R.drawable.ic_menu_help);

        drawerLeftList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Initializes all views in the layout.
     */
    private void initViewsById() {
        Log.d(TAG, "initViewsById");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerRightList = (ListView) findViewById(R.id.right_drawer);
        drawerLeftList = (ListView) findViewById(R.id.left_drawer);
    }

    /**
     * Creates a {@link android.support.v4.app.ActionBarDrawerToggle}.
     *
     * @return the ActionBarDrawerToggle
     */
    private ActionBarDrawerToggle createActionBarDrawerToggle() {
        Log.d(TAG, "createActionBarDrawerToggle");
        return new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if (item != null && item.getItemId() == android.R.id.home && drawerToggle.isDrawerIndicatorEnabled()) {
                    openDrawerAndCloseOther(Gravity.LEFT);
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.d(TAG, "onAttachFragment");
        super.onAttachFragment(fragment);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerToggle.onOptionsItemSelected(item);
                return true;
            case R.id.action_scanQR:
                return doQRScan();
            case R.id.action_maps:
                startActivity(new Intent(this, PathDescription.class));
                return true;
            case R.id.action_websockettest:
                Intent intent = new Intent(this, WebSocketTestMainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_show_persons:
                openDrawerAndCloseOther(Gravity.RIGHT);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_logout:
                deleteFile(AuthHelper.FILE_NAME);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opens the specified drawer and closes the other one, if it is visible
     *
     * @param drawerGravity the drawer to be opened
     */
    private void openDrawerAndCloseOther(int drawerGravity) {
        Log.d(TAG, "openDrawerAndCloseOther");
        switch (drawerGravity) {
            case Gravity.LEFT:
                if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else if (drawerLayout.isDrawerVisible(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                    drawerLayout.openDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case Gravity.RIGHT:
                if (drawerLayout.isDrawerVisible(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    drawerLayout.openDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
        }
    }

    /**
     * Creates a new Intent for QR scan.
     *
     * @return true if the scan was successful, false if not
     */
    private boolean doQRScan() {
        Log.d(TAG, "doQRScan");
        PackageManager pm = this.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, REQUESTCODE_QRSCAN);
            return true;
        } else {
            // TODO only for mockup test
            switchToChatRoom("INFZ_305", "");
            Toast.makeText(this, "Keine Kamera da :(", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == REQUESTCODE_QRSCAN) {
            if (resultCode == RESULT_OK) {
                String room = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                switchToChatRoom(room, format);
                Toast.makeText(this, room, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // TODO only for mockup test
                String testRoom = "INFZ 305";
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                switchToChatRoom(testRoom, "");
            }

        }
    }

    private void switchToChatRoom(String room, String format) {
        Fragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ChatFragment.ARG_CHAT_ROOM, room);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        NavItemModel enterRoomNav = (NavItemModel) drawerLeftList.getItemAtPosition(drawerLeftList.getCheckedItemPosition());
        enterRoomNav.setTitle(room);
        enterRoomNav.setIconID(-1);
        ((BaseAdapter) drawerLeftList.getAdapter()).notifyDataSetChanged();
    }

    private void switchToGlobalChat() {
        Fragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ChatFragment.ARG_CHAT_ROOM, ChatFragment.ROOM_GLOBAL);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    /**
     * Swaps fragments in the main content view
     *
     * @param navkey
     */
    private void navigateTo(NavKey navkey) {
        Log.d(TAG, "navigateTo: " + navkey);
        // Create a new fragment and specify the planet to show based on position
        switch (navkey) {
            case ENTER_ROOM:
                doQRScan();
                break;
            case GLOABAL:
                switchToGlobalChat();
                break;
            case CHECK_PRESENCE:
                break;
            case ABOUT:
                break;
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick Navigation");
            ViewHolder vh = (ViewHolder) view.getTag();
            drawerLeftList.setSelection(position);
            drawerLayout.closeDrawer(Gravity.LEFT);
            navigateTo(vh.key);
        }
    }

}
