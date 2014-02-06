package de.fhb.mi.paperfly.user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Regex;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import de.fhb.mi.paperfly.R;
import de.fhb.mi.paperfly.auth.AuthStatus;
import de.fhb.mi.paperfly.auth.LoginActivity;
import de.fhb.mi.paperfly.dto.RegisterAccountDTO;
import de.fhb.mi.paperfly.service.RestConsumerException;
import de.fhb.mi.paperfly.service.RestConsumerSingleton;

/**
 * This activity is for registration of an user
 *
 * @author Andy Klay  klay@fh-brandenburg.de
 * @author Christoph Ott
 */
public class UserRegisterActivity extends Activity implements Validator.ValidationListener {

    public static final String TAG = UserRegisterActivity.class.getSimpleName();
    private Validator validator;
    private UserRegisterTask mRegisterTask = new UserRegisterTask();
    @Required(order = 1)
    @TextRule(order = 2, minLength = 6, messageResId = R.string.error_field_too_short_6)
    @Regex(order = 3, pattern = "^[a-zA-Z0-9]+$", messageResId = R.string.error_invalid_username_chars_and_nums)
    private EditText mUsernameView;
    @Required(order = 4)
    @Email(order = 5, messageResId = R.string.error_invalid_email)
    private EditText mEmailView;
    @Required(order = 6)
    private EditText mFirstnameView;
    @Required(order = 7)
    private EditText mLastnameView;
    @TextRule(order = 8, minLength = 6, messageResId = R.string.error_field_too_short_6)
    @Password(order = 9)
    private EditText mPasswordView;
    @ConfirmPassword(order = 10)
    private EditText mPasswordRepeatView;

    private Button mRegisterButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_register);

        mEmailView = (EditText) findViewById(R.id.accountMail);
        mUsernameView = (EditText) findViewById(R.id.accountUserName);
        mFirstnameView = (EditText) findViewById(R.id.accountFirstName);
        mLastnameView = (EditText) findViewById(R.id.accountLastName);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordRepeatView = (EditText) findViewById(R.id.password_repeat);
        mRegisterButton = (Button) findViewById(R.id.update_button);

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        }
    }

    @Override
    public void onValidationSucceeded() {
        // Store values at the time of the register action
        String mEmail = mEmailView.getText().toString();
        String mPassword = mPasswordView.getText().toString();
        String mPasswordRepeat = mPasswordRepeatView.getText().toString();
        String mFirstname = mFirstnameView.getText().toString();
        String mLastname = mLastnameView.getText().toString();
        String mUsername = mUsernameView.getText().toString();

        if (mRegisterTask.getStatus() != AsyncTask.Status.RUNNING) {
            mRegisterTask = new UserRegisterTask();
            mRegisterTask.execute(mEmail, mPassword, mPasswordRepeat, mFirstname, mLastname, mUsername);
            mRegisterButton.setEnabled(false);
        }
    }

    /**
     * Attempt register an user depending on which button was clicked.
     *
     * @param v the view which was clicked
     */
    public void register(View v) {
        Log.d(TAG, "register: " + ((Button) v).getText());
        validator.validate();
    }

    /**
     * Represents an asynchronous registration task used to authenticate the user.
     */
    public class UserRegisterTask extends AsyncTask<String, Void, AuthStatus> {
        @Override
        protected AuthStatus doInBackground(String... params) {
            String mail = params[0];
            String pw = params[1];
            String pwRpt = params[2];
            String firstname = params[3];
            String lastname = params[4];
            String username = params[5];

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
            mRegisterButton.setEnabled(true);

            switch (authStatus) {
                case REGISTER_EMAIL_ALREADY_REGISTERED:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_info_error), Toast.LENGTH_LONG).show();
                    break;
                case REGISTER_SUCCESSFUL:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_info_success), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra(LoginActivity.ARGS_REGISTER_EMAIL, mEmailView.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
            mRegisterTask = new UserRegisterTask();
        }
    }
}