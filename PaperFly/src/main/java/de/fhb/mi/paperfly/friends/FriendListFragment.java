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
import de.fhb.mi.paperfly.service.RestConsumerService;
import de.fhb.mi.paperfly.user.UserProfileActivity;

/**
 * ListView of private contacts, a friendlist
 *
 * @author Andy Klay    klay@fh-brandenburg.de
 */
public class FriendListFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "FriendListFragment";
    private View rootView;
    private ListView friendListView;
    private List<String> friendListValues;
    private ArrayAdapter<String> listAdapter;

    /** Begin *************************************** Rest-Connection ****************************** **/
    private boolean mBound = false;
    private RestConsumerService mRestConsumerService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RestConsumerService.RestConsumerBinder binder = (RestConsumerService.RestConsumerBinder) service;
            mRestConsumerService = binder.getServerInstance();
            mBound = true;
            Toast.makeText(rootView.getContext(), "Connected", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            //TODO mRestConsumerService=null;
        }
    };
    /** End *************************************** Rest-Connection ****************************** **/



    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(rootView.getContext(), RestConsumerService.class);
        mBound=rootView.getContext().bindService(serviceIntent, mConnection, Context.BIND_IMPORTANT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        this.rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        initViewsById();
        AccountDTO account;

        friendListValues = new ArrayList<String>();
        friendListValues.add("before ...");
        friendListValues.add(Boolean.toString(mBound));

        if(mRestConsumerService!=null){
            Log.d(TAG, "mRestConsumerService exists");
            //TODO get username from session or something like that
            mRestConsumerService.getAccount("test");
//            account=mRestConsumerService.getAccountByUsername("username");

//            friendListValues.add("mRestConsumerService exists");
//            if(account!=null && account.getFriendlist()!=null){
//                for(String friendName : account.getFriendlist()){
//                    friendListValues.add(friendName);
//                }
//
//            }
        }

//        account=mRestConsumerService.getAccountByUsername("username");
//
//        friendListValues.add("mRestConsumerService exists");
//        if(account!=null && account.getFriendlist()!=null){
//            for(String friendName : account.getFriendlist()){
//                friendListValues.add(friendName);
//            }
//
//        }

//        //TODO DUMMY DATA replace later
//        for (int i = 0; i < 20; i++) {
//            friendListValues.add("User" + i);
//        }

        listAdapter=new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, friendListValues);
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
            rootView.getContext().unbindService(mConnection);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
//        TODO bindService(new Intent(this, LocalWordService.class), mConnection,
//        same like in onStart()
//                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"onItemClick");
        Intent intent = new Intent(getActivity(),UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.ARGS_USER, listAdapter.getItem(position).toString());
        startActivity(intent);
    }


}