package de.fhb.mi.paperfly.user;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerSingleton;
import de.fhb.mi.paperfly.util.AsyncDelegate;

/**
 * The activity to show if the user searches for another user.
 */
public class UserSearchActivity extends ListActivity implements AsyncDelegate {
    private static final String TAG = UserSearchActivity.class.getSimpleName();
    ArrayAdapter<String> arrayAdapter;
    List<AccountDTO> searchResults;
    private TextView emptyView;

    @Override
    public void asyncComplete(boolean success) {
        for (AccountDTO accountDTO : searchResults) {
            arrayAdapter.add(accountDTO.getUsername());
        }
        arrayAdapter.notifyDataSetChanged();
        emptyView.setText(getResources().getString(R.string.nothing_found));
    }

    /**
     * Searches for users and shows the result in the list view
     *
     * @param queryStr the string to search for
     */
    private void doSearch(String queryStr) {
        Log.d(TAG, "doSearch: " + queryStr);
        SearchUserTask searchUserTask = new SearchUserTask(this);
        searchUserTask.execute(queryStr);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        emptyView = (TextView) findViewById(android.R.id.empty);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        setListAdapter(arrayAdapter);
        handleIntent(getIntent());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick");
        Intent intent = new Intent();
        intent.putExtra(UserProfileFragment.ARGS_USER, arrayAdapter.getItem(position));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Represents an asynchronous SearchUserTask used to search for user.
     */
    public class SearchUserTask extends AsyncTask<String, Void, Boolean> {

        private AsyncDelegate delegate;

        public SearchUserTask(AsyncDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                searchResults = RestConsumerSingleton.getInstance().searchAccount(params[0]);
            } catch (RestConsumerException e) {
                Log.d(TAG, e.getMessage());
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            delegate.asyncComplete(true);
        }
    }
}