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

package de.fhb.mi.paperfly.friends;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerService;
import de.fhb.mi.paperfly.user.UserProfileActivity;

/**
 * ListView of private contacts, a friendlist
 *
 * @author Andy Klay (klay@fh-brandenburg.de)
 */
public class FriendListFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "FriendListFragment";
    private View rootView;
    private ListView friendListView;
    private List<String> friendListValues;
    private ArrayAdapter<String> listAdapter;
    private GetAccountTask mAccountTask = null;
    AccountDTO account = null;

    /**
     * Begin *************************************** Rest-Connection ****************************** *
     */
    private boolean mBound = false;
    private RestConsumerService mRestConsumerService;
    private ServiceConnection mConnectionRestService = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            RestConsumerService.RestConsumerBinder binder = (RestConsumerService.RestConsumerBinder) service;
            mRestConsumerService = binder.getServerInstance();
            mBound = true;
            Toast.makeText(rootView.getContext(), "RestConsumerService Connected", Toast.LENGTH_SHORT)
                    .show();

            mAccountTask = new GetAccountTask();
            //TODO username is only template for actual user
            mAccountTask.execute("username");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(rootView.getContext(), "RestConsumerService Disconnected", Toast.LENGTH_SHORT)
                    .show();
            mBound = false;
            mRestConsumerService = null;
        }
    };

    /**
     * End *************************************** Rest-Connection ****************************** *
     */

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        this.rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        initViewsById();

        friendListValues = new ArrayList<String>();
        Log.d(TAG, "binding RestConsumerService " + Boolean.toString(mBound));

        listAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, friendListValues);
        friendListView.setAdapter(listAdapter);
        friendListView.setOnItemClickListener(this);
        return rootView;
    }

    private void initViewsById() {
        friendListView = (ListView) rootView.findViewById(R.id.friendsList);
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

        super.onStop();
        if (mBound) {
            rootView.getContext().unbindService(mConnectionRestService);
            mBound = false;
        }
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
        Intent serviceIntent = new Intent(activity.getBaseContext(), RestConsumerService.class);
        mBound = activity.getBaseContext().bindService(serviceIntent, mConnectionRestService, Context.BIND_IMPORTANT);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick");
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.ARGS_USER, listAdapter.getItem(position).toString());
        startActivity(intent);
    }

    /**
     * Represents an asynchronous GetAccountTask used to get an user
     */
    public class GetAccountTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];

            try {
                account = mRestConsumerService.getAccountByUsername(username);
            } catch (RestConsumerException e) {
                e.printStackTrace();
                Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }

            if (account != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAccountTask = null;

            //TODO Dummy data of DB...da Friendlist in Json nicht enthalten
            friendListValues.add("salaxy");
            friendListValues.add("kaain");
            friendListValues.add("yserz");
            friendListValues.add("wayne");

            if (success) {
                Log.d("onPostExecute", "success");

                if (mRestConsumerService != null) {
                    Log.d(TAG, "mRestConsumerService exists");

                    if (account != null && account.getFriendList() != null) {
                        for (AccountDTO friendAccount : account.getFriendList()) {
                            friendListValues.add(friendAccount.getUsername());
                        }
                    }
                } else {
                    friendListValues.add("mRestConsumerService is null");
                    Log.d(TAG, "create dummy entries");

                    for (int i = 0; i < 5; i++) {
                        friendListValues.add("Dummy User" + i);
                    }
                }

                listAdapter.notifyDataSetChanged();
            } else {
                Log.d("onPostExecute", "no success");
            }
        }
    }

}