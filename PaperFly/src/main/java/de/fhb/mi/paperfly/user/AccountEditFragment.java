package de.fhb.mi.paperfly.user;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerSingleton;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccountEditFragment extends Fragment {

    public static final String TAG = AccountEditFragment.class.getSimpleName();

    private View rootView;
    private EditText accountUsername;
    private EditText accountFirstname;
    private EditText accountLastname;
    private EditText accountMail;

    private GetMyAccountTask mMyAccountTask = null;
    private AccountDTO account = null;

    private AccountEditTask mMyAccountEditTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void pressUpdate(View view) {
        Log.d(TAG, "pressUpdate");
        Toast.makeText(rootView.getContext(), "Update pressed TODO", Toast.LENGTH_SHORT)
                .show();
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
