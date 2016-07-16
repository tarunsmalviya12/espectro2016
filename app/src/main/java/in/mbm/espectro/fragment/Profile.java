package in.mbm.espectro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import in.mbm.espectro.R;
import in.mbm.espectro.Splash;
import in.mbm.espectro.utils.SharedPreferenceKey;

/**
 * Created by tarunsmalviya12 on 17/4/16.
 */
public class Profile extends Fragment implements View.OnClickListener {

    /**
     * Profile fragment elements.
     */
    TextView nameTxt, clgTxt, sidTxt, emailTxt, phoneNoTxt;
    Button logoutBtn;

    /*
     * Variables.
     */
    private static final String ARG_SECTION_ID = "section_id";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        rootView.setOnClickListener(this);

        initializeVariables();
        retrieveElements(rootView);

        // Setting up user information.
        nameTxt.setText(sharedPref.getString(SharedPreferenceKey.USER_NAME, "").toUpperCase());
        clgTxt.setText(sharedPref.getString(SharedPreferenceKey.USER_CLG_NAME, ""));
        sidTxt.setText("ESP" + String.valueOf(16000 + Integer.parseInt(sharedPref.getString(SharedPreferenceKey.USER_ID, ""))));
        emailTxt.setText(sharedPref.getString(SharedPreferenceKey.USER_EMAIL, "").toLowerCase());
        phoneNoTxt.setText(sharedPref.getString(SharedPreferenceKey.USER_PHONE_NO, ""));

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logoutBtn:
                editor.putString(SharedPreferenceKey.USER_ID, "");
                editor.putString(SharedPreferenceKey.USER_NAME, "");
                editor.putString(SharedPreferenceKey.USER_EMAIL, "");
                editor.putString(SharedPreferenceKey.USER_PHONE_NO, "");
                editor.putString(SharedPreferenceKey.USER_CLG_ID, "");
                editor.putString(SharedPreferenceKey.USER_CLG_NAME, "");
                editor.putString(SharedPreferenceKey.GCM_REGISTRATION_ID, "");
                editor.commit();

                Intent i = new Intent(getActivity(), Splash.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                getActivity().finish();

                break;
        }
    }

    public void initializeVariables() {
        sharedPref = getActivity().getSharedPreferences(SharedPreferenceKey.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void retrieveElements(View rootview) {
        // Retrieving Profile elements from fragment_profile.xml.
        nameTxt = (TextView) rootview.findViewById(R.id.nameTxt);
        clgTxt = (TextView) rootview.findViewById(R.id.clgTxt);
        sidTxt = (TextView) rootview.findViewById(R.id.sidTxt);
        emailTxt = (TextView) rootview.findViewById(R.id.emailTxt);
        phoneNoTxt = (TextView) rootview.findViewById(R.id.phoneNoTxt);

        logoutBtn = (Button) rootview.findViewById(R.id.logoutBtn);

        // Declaring Profile elements listener.
        logoutBtn.setOnClickListener(this);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Profile newInstance(String sectionID) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_ID, sectionID);
        fragment.setArguments(args);

        return fragment;
    }
}