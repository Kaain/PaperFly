package de.fhb.mi.paperfly;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends Activity {

    public static final String ARGS_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.action_maps:
                double latitude = 52.411433;
                double longitude = 12.536933;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:" + latitude + "," + longitude + "?z=17&q=" + latitude + "," + longitude));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Only works with google maps or something similar installed", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        View rootView;
        private TextView profileUsername;
        private TextView profileFirstname;
        private TextView profileLastname;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
            initViews(rootView);
            String username = getActivity().getIntent().getExtras().getString(ARGS_USER);
            profileUsername.append(username);
            profileFirstname.append(username);
            profileLastname.append(username);
            return rootView;
        }

        private void initViews(View rootView) {
            profileUsername = (TextView) rootView.findViewById(R.id.profileUsername);
            profileFirstname = (TextView) rootView.findViewById(R.id.profileFirstname);
            profileLastname = (TextView) rootView.findViewById(R.id.profileLastname);
        }
    }


}
