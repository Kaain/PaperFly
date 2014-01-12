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

import de.fhb.mi.paperfly.PaperFlyApp;
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
        AccountDTO account = ((PaperFlyApp) getActivity().getApplication()).getAccount();
        accountUsername.setText(account.getUsername());
        accountFirstname.setText(account.getFirstName());
        accountLastname.setText(account.getLastName());
        accountMail.setText(account.getEmail());
        return rootView;
    }

    public void pressUpdate() {
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
    }

    /**
     * Represents an asynchronous AccountEditTask used to set the account data
     */
    public class AccountEditTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            AccountDTO account = null;
            try {

                AccountDTO editedAccount = new AccountDTO(((PaperFlyApp) getActivity().getApplication()).getAccount());
                editedAccount.setFirstName(accountFirstname.getText().toString());
                editedAccount.setLastName(accountLastname.getText().toString());

                account = RestConsumerSingleton.getInstance().editAccount(editedAccount);
                ((PaperFlyApp) getActivity().getApplication()).setAccount(account);
            } catch (RestConsumerException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return account != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mMyAccountEditTask = null;

            if (success) {
                Log.d("onPostExecute", "success");
                AccountDTO account = ((PaperFlyApp) getActivity().getApplication()).getAccount();
                if (account != null) {
                    accountUsername.setText(account.getUsername());
                    accountFirstname.setText(account.getFirstName());
                    accountLastname.setText(account.getLastName());
                    accountMail.setText(account.getEmail());
                }
            }

        }
    }
}
