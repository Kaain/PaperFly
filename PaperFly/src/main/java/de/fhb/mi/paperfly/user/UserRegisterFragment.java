package de.fhb.mi.paperfly.user;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
 *  @author Andy Klay (klay@fh-brandenburg.de)
 */
public class UserRegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = FriendListFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private UserRegisterTask mRegisterTask = null;
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mUserNameView;
    private EditText mFirstname;
    private EditText mPasswordRepeatView;
    private EditText mLastname;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;



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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public UserRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.fragment_user_register, container, false);

        mEmailView = (EditText) rootView.findViewById(R.id.accountMail);
        mUserNameView = (EditText) rootView.findViewById(R.id.accountUserName);
        mFirstname = (EditText) rootView.findViewById(R.id.accountFirstName);
       mLastname = (EditText) rootView.findViewById(R.id.accountLastName);
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

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        if (checkValues()) {
                mRegisterTask = new UserRegisterTask();
                mRegisterTask.execute(mEmail, mPassword);
        }
    }

    private boolean checkValues() {
        //TODO
        return true;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    /**
     * Represents an asynchronous registration task used to authenticate the user.
     */
    public class UserRegisterTask extends AsyncTask<String, Void, AuthStatus> {
        @Override
        protected AuthStatus doInBackground(String... params) {
            String mail = params[0];
            String pw = params[1];

            RegisterAccountDTO nextUser = new RegisterAccountDTO();
            nextUser.setLastName("lastname");
            nextUser.setFirstName("firstname");
            nextUser.setUsername("neuerUser");
            nextUser.setLastModified(new Date(System.currentTimeMillis()));
            nextUser.setCreated(new Date(System.currentTimeMillis()));
            nextUser.setEmail(mail);
            nextUser.setPassword(pw);
            nextUser.setPasswordRpt(pw);
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
//            showProgress(false);
        }

        @Override
        protected void onPostExecute(final AuthStatus authStatus) {
            mRegisterTask = null;
//            showProgress(false);

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
