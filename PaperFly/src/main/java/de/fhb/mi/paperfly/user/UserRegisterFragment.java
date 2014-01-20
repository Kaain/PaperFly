package de.fhb.mi.paperfly.user;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.auth.AuthStatus;
import de.fhb.mi.paperfly.dto.RegisterAccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerSingleton;


/**
 * Fragment which displays a offering registration
 *
 * @author Andy Klay (klay@fh-brandenburg.de)
 */
public class UserRegisterFragment extends Fragment {

    public static final String TAG = FriendListFragment.class.getSimpleName();
    private UserRegisterTask mRegisterTask = null;

    private String mEmail;
    private String mPassword;
    private String mPasswordRepeat;
    private String mFirstname;
    private String mLastname;
    private String mUsername;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mUsernameView;
    private EditText mFirstnameView;
    private EditText mPasswordRepeatView;
    private EditText mLastnameView;

    private OnFragmentInteractionListener mListener;
    private View rootView;


    public UserRegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserRegisterFragment newInstance(String param1, String param2) {
        UserRegisterFragment fragment = new UserRegisterFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.fragment_user_register, container, false);

        mEmailView = (EditText) rootView.findViewById(R.id.accountMail);
        mUsernameView = (EditText) rootView.findViewById(R.id.accountUserName);
        mFirstnameView = (EditText) rootView.findViewById(R.id.accountFirstName);
        mLastnameView = (EditText) rootView.findViewById(R.id.accountLastName);
        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mPasswordRepeatView = (EditText) rootView.findViewById(R.id.password_repeat);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_register, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    /**
     * Attempt to login or register an user depending on which button was clicked.
     *
     * @param v the view which was clicked
     */
    public void register(View v) {
        Log.d(TAG, "attemptLoginRegister: " + ((Button) v).getText());
        if (v.getId() == R.id.register_button && mRegisterTask != null) {
            return;
        }

        // Store values at the time of the register action
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mPasswordRepeat = mPasswordRepeatView.getText().toString();
        mFirstname = mFirstnameView.getText().toString();
        mLastname = mLastnameView.getText().toString();
        mUsername = mUsernameView.getText().toString();

        if (checkValues()) {
            mRegisterTask = new UserRegisterTask();
            mRegisterTask.execute(mEmail, mPassword, mPasswordRepeat, mFirstname, mLastname, mUsername);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Checks if the values in the form are valid.
     *
     * @return true if the values are valid, false if not
     */
    private boolean checkValues() {
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(mPasswordRepeat)) {
            mPasswordRepeatView.setError(getString(R.string.error_field_required));
            focusView = mPasswordRepeatView;
            cancel = true;
        } else if (mPasswordRepeatView.length() < 4) {
            mPasswordRepeatView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordRepeatView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid username
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (mUsernameView.length() < 4) {
            mUsernameView.setError(getString(R.string.error_invalid_password));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }



    /**
     * Represents an asynchronous registration task used to authenticate the user.
     */
    public class UserRegisterTask extends AsyncTask<String, Void, AuthStatus> {
        @Override
        protected AuthStatus doInBackground(String... params) {
            String mail = params[0];
            String pw = params[1];
            String pwRpt= params[2];
            String firstname= params[3];
            String lastname= params[4];
            String username= params[5];

            RegisterAccountDTO nextUser = new RegisterAccountDTO();
            nextUser.setLastName(lastname);
            nextUser.setFirstName(firstname);
            nextUser.setUsername(username);
            nextUser.setLastModified(new Date(System.currentTimeMillis()));
            nextUser.setCreated(new Date(System.currentTimeMillis()));
            nextUser.setEmail(mail);
            nextUser.setPassword(pw);
            nextUser.setPasswordRpt(pwRpt);
            nextUser.setEnabled(true);


            try {
                RestConsumerSingleton.getInstance().register(nextUser);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (RestConsumerException e) {
                e.printStackTrace();
                //TODO kann man nicht immer sagen an der Stelle
                return AuthStatus.REGISTER_EMAIL_ALREADY_REGISTERED;
            }

            return AuthStatus.REGISTER_SUCCESSFUL;
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
        }

        @Override
        protected void onPostExecute(final AuthStatus authStatus) {
            mRegisterTask = null;

            switch (authStatus) {
                case REGISTER_EMAIL_ALREADY_REGISTERED:
//                    Toast.makeText(getActivity(), getApplicationContext().getResources().getString(R.string.register_info_error_email), Toast.LENGTH_LONG).show();
                    break;
                case REGISTER_SUCCESSFUL:
//                    ((TextView) findViewById(R.id.register_info)).setText(R.string.register_info_success);
                    break;
            }
        }
    }

}
