package de.fhb.mi.paperfly.user;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.fhb.mi.paperfly.R;

/**
 * The activity to show if the user searches for another user.
 */
public class UserSearchActivity extends ListActivity {
    private static final String TAG = UserSearchActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        handleIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick");
        Intent intent = new Intent();
        intent.putExtra(UserProfileFragment.ARGS_USER, getListAdapter().getItem(position).toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Handles the intent from another activity.
     *
     * @param intent the intent
     */
    private void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    /**
     * Searches for users and shows the result in the list view
     *
     * @param queryStr the string to search for
     */
    private void doSearch(String queryStr) {
        Log.d(TAG, "doSearch: " + queryStr);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add("UserResult" + i + " for " + queryStr);
        }
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
    }
}