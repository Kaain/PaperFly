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
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerService;

public class EditAccountDataActivity extends Activity {

    private static final String TAG = "EditAccountDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_data);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_account_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private View rootView;
        private EditText accountUsername;
        private EditText accountFirstname;
        private EditText accountLastname;
        private EditText accountMail;

        private GetMyAccountTask mMyAccountTask = null;
        private AccountDTO account = null;

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

                mMyAccountTask = new GetMyAccountTask();
                mMyAccountTask.execute();
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

        public PlaceholderFragment() {

        }

        private void initViews(View rootView) {
            accountUsername = (EditText) rootView.findViewById(R.id.accountUserName);
            accountFirstname = (EditText) rootView.findViewById(R.id.accountFirstName);
            accountLastname = (EditText) rootView.findViewById(R.id.accountLastName);
            accountMail = (EditText) rootView.findViewById(R.id.accountMail);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_edit_account_data, container, false);
            initViews(rootView);
            return rootView;
        }


        /**
         * Represents an asynchronous GetMyAccountTask used to get an user
         */
        public class GetMyAccountTask extends AsyncTask<String, Void, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                String username = params[0];

                try {
                    account = mRestConsumerService.getMyAccount();
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
                mMyAccountTask = null;

                if (success) {
                    Log.d("onPostExecute", "success");

                    if (mRestConsumerService != null) {
                        Log.d(TAG, "mRestConsumerService exists");

                        if (account != null) {
                            accountUsername.setText(account.getUsername());
                            accountFirstname.setText(account.getFirstName());
                            accountLastname.setText(account.getLastName());
                            accountMail.setText(account.getEmail());
                        }
                    }

                } else {
                    Log.d("onPostExecute", "no success");
                }
            }
        }
    }

}
