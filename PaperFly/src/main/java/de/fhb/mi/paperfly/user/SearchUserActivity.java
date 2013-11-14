package de.fhb.mi.paperfly.user;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.fhb.mi.paperfly.R;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends ListActivity {
    private static final String TAG = "SearchUserActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        handleIntent(getIntent());
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void onListItemClick(ListView l,
                                View v, int position, long id) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.ARGS_USER, getListAdapter().getItem(position).toString());
        startActivity(intent);
        finish();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    private void doSearch(String queryStr) {
        Log.d(TAG, "doSearch: " + queryStr);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add("UserResult" + i + " for " + queryStr);
        }
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
    }
}