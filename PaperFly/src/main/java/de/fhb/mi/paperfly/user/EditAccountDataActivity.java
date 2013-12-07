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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerSingleton;

public class EditAccountDataActivity extends Activity {

    private static final String TAG = EditAccountDataActivity.class.getSimpleName();

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

        private AccountEditTask mMyAccountEditTask = null;

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

        private void pressUpdate() {
            Log.d(TAG, "pressUpdate");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d(TAG, "onStop");
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            Log.d(TAG, "onAttach");

            mMyAccountTask = new GetMyAccountTask();
            mMyAccountTask.execute();
        }


        /**
         * Represents an asynchronous GetMyAccountTask used to get the account data
         */
        public class GetMyAccountTask extends AsyncTask<String, Void, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {

                try {
                    account = RestConsumerSingleton.getInstance().getMyAccount();
                } catch (RestConsumerException e) {
                    e.printStackTrace();
                }

                return account != null;
            }

            @Override
            protected void onPostExecute(final Boolean success) {
                mMyAccountTask = null;

                if (success) {
                    Log.d("onPostExecute", "success");

                    if (account != null) {
                        accountUsername.setText(account.getUsername());
                        accountFirstname.setText(account.getFirstName());
                        accountLastname.setText(account.getLastName());
                        accountMail.setText(account.getEmail());
                    }
                }
            }
        }


        /**
         * Represents an asynchronous AccountEditTask used to set the account data
         */
        public class AccountEditTask extends AsyncTask<String, Void, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {

                try {

                    AccountDTO editedAccount = new AccountDTO();
//                    editedAccount.setEmail(accountMail.getText());
//                    editedAccount.setFirstName();
//                    editedAccount.setLastName();
//                    editedAccount.setUsername(accountUsername.getText());

                    account = RestConsumerSingleton.getInstance().editAccount(editedAccount);
                } catch (RestConsumerException e) {
                    e.printStackTrace();
                    Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                            .show();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                if (account != null) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(final Boolean success) {
                mMyAccountEditTask = null;

                if (success) {
                    Log.d("onPostExecute", "success");

//                        if (account != null) {
//                            accountUsername.setText(account.getUsername());
//                            accountFirstname.setText(account.getFirstName());
//                            accountLastname.setText(account.getLastName());
//                            accountMail.setText(account.getEmail());
//                        }
                }

            }
        }
    }

}
