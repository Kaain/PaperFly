package de.fhb.mi.paperfly.user;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;

import java.io.UnsupportedEncodingException;

import de.fhb.mi.paperfly.PaperFlyApp;
import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerSingleton;
import lombok.NoArgsConstructor;

/**
 * This fragment is for changing users account data
 *
 * @author Andy Klay  klay@fh-brandenburg.de
 */
@NoArgsConstructor
public class AccountEditFragment extends Fragment implements Validator.ValidationListener {

    public static final String TAG = AccountEditFragment.class.getSimpleName();

    private View rootView;
    private EditText mUsernameView;
    @Required(order = 1)
    private EditText mFirstnameView;
    @Required(order = 2)
    private EditText mLastnameView;
    private EditText mEmailView;
    private Button updateButton;
    private Validator validator;
    private AccountEditTask mMyAccountEditTask = null;

    /**
     * initialize refs to view elements
     *
     * @param rootView
     */
    private void initViews(View rootView) {
        mUsernameView = (EditText) rootView.findViewById(R.id.accountUserName);
        mFirstnameView = (EditText) rootView.findViewById(R.id.accountFirstName);
        mLastnameView = (EditText) rootView.findViewById(R.id.accountLastName);
        mEmailView = (EditText) rootView.findViewById(R.id.accountMail);
        updateButton = (Button) rootView.findViewById(R.id.update_button);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_account_data, container, false);
        initViews(rootView);
        AccountDTO account = ((PaperFlyApp) getActivity().getApplication()).getAccount();
        mUsernameView.setText(account.getUsername());
        mFirstnameView.setText(account.getFirstName());
        mLastnameView.setText(account.getLastName());
        mEmailView.setText(account.getEmail());
        mMyAccountEditTask = new AccountEditTask();

        validator = new Validator(this);
        validator.setValidationListener(this);

        final Button updateButton = (Button) rootView.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validator.validate();
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onValidationSucceeded() {
        if (mMyAccountEditTask.getStatus() != AsyncTask.Status.RUNNING) {
            mMyAccountEditTask.execute();
            updateButton.setEnabled(false);
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        }
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
                editedAccount.setFirstName(mFirstnameView.getText().toString());
                editedAccount.setLastName(mLastnameView.getText().toString());

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

            if (success) {
                Log.d("onPostExecute", "success");
                AccountDTO account = ((PaperFlyApp) getActivity().getApplication()).getAccount();
                if (account != null) {
                    mUsernameView.setText(account.getUsername());
                    mFirstnameView.setText(account.getFirstName());
                    mLastnameView.setText(account.getLastName());
                    mEmailView.setText(account.getEmail());
                    updateButton.setEnabled(true);
                }
                Toast.makeText(rootView.getContext(), "Update successful!", Toast.LENGTH_SHORT).show();
            }
            mMyAccountEditTask = new AccountEditTask();
        }
    }
}
