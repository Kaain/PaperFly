/*
 * Copyright (C) 2013 Andy Klay
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

package de.fhb.mi.paperfly.user;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import de.fhb.mi.paperfly.PaperFlyApp;
import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerSingleton;
import de.fhb.mi.paperfly.util.AsyncDelegate;

/**
 * ListView of private contacts, a friendlist
 *
 * @author Andy Klay (klay@fh-brandenburg.de)
 */
public class FriendListFragment extends Fragment implements AdapterView.OnItemClickListener, AsyncDelegate {

    public static final String TAG = FriendListFragment.class.getSimpleName();
    private View rootView;
    private ListView friendListView;
    private FriendListAdapter listAdapter;
    AccountDTO account = null;
    private DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        this.rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        initViewsById();
        this.drawerLayout = (DrawerLayout) container.getParent();
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.nav_item_open_friendlist);

        UpdateAccountTask updateAccountTask = new UpdateAccountTask(this);
        updateAccountTask.execute();

        listAdapter = new FriendListAdapter(rootView.getContext());
        friendListView.setAdapter(listAdapter);
        friendListView.setOnItemClickListener(this);
        return rootView;
    }

    private void initViewsById() {
        friendListView = (ListView) rootView.findViewById(R.id.friendsList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            inflater.inflate(R.menu.user_friends, menu);
        }


        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_user).getActionView();

        // Get the menu item from the action bar
        MenuItem menuItem = menu.findItem(R.id.action_search_user);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "Search activated. Locking drawers.");
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "Search deactivated. Unlocking drawers.");
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                return true;
            }
        });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick");
        Fragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(UserProfileFragment.ARGS_USER, listAdapter.getItem(position).getUsername());
        args.putBoolean(UserProfileFragment.ARGS_USER_IS_FRIEND, true);
        fragment.setArguments(args);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public void asyncComplete(boolean success) {
        account = ((PaperFlyApp) getActivity().getApplication()).getAccount();
        listAdapter.addAll(account.getFriendList());
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Represents an asynchronous GetMyAccountTask used to get an user
     */
    public class UpdateAccountTask extends AsyncTask<String, Void, Boolean> {

        private AsyncDelegate delegate;

        public UpdateAccountTask(AsyncDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                RestConsumerSingleton.getInstance().updateMyAccount();
            } catch (RestConsumerException e) {
                Log.d(TAG, e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, e.getMessage());
            }
            return account != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            delegate.asyncComplete(true);
        }
    }

    public class FriendListAdapter extends ArrayAdapter<AccountDTO> {
        private final Context context;

        public FriendListAdapter(Context context) {
            super(context, 0);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
            TextView textView2 = (TextView) rowView.findViewById(android.R.id.text2);
            final AccountDTO actualAccount = this.getItem(position);
            textView.setText(actualAccount.getUsername());
            textView2.setText(actualAccount.getStatus().name());
            switch (actualAccount.getStatus()) {
                case ONLINE:
                    textView2.setTextColor(Color.GREEN);
                    break;
                case OFFLINE:
                    textView2.setTextColor(Color.RED);
                    break;
                case AWAY:
                    textView2.setTextColor(Color.YELLOW);
                    break;
            }
            return rowView;
        }
    }

}
