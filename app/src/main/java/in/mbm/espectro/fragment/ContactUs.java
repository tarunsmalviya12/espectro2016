package in.mbm.espectro.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import in.mbm.espectro.R;
import in.mbm.espectro.utils.SharedPreferenceKey;

/**
 * Created by tarunsmalviya12 on 17/4/16.
 */
public class ContactUs extends Fragment implements View.OnClickListener {

    /**
     * ContactUs fragment elements.
     */
    LinearLayout emailLayout, phoneNoLayout, facebookLayout, instagramLayout;

    /*
     * Variables.
     */
    private static final String ARG_SECTION_ID = "section_id";
    private SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        rootView.setOnClickListener(this);

        initializeVariables();
        retrieveElements(rootView);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.emailLayout: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"espectro@espectro2016.in"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion and Feedback");
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
            break;
            case R.id.phoneNoLayout: {
                final PackageManager packageManager = getActivity().getPackageManager();

                // Check whether user have granted us permission for reading contact.
                if (packageManager.checkPermission(Manifest.permission.CALL_PHONE, getActivity().getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+919784845100"));
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Permission Denied")
                            .setMessage("Kindly grant 'Phone' permission in app settings.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
            break;
            case R.id.facebookLayout: {
                try {
                    getActivity().getPackageManager()
                            .getPackageInfo("com.facebook.katana", 0);
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("fb://page/1455747224717031"));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.facebook.com/Espectro-1455747224717031")));
                }
            }
            break;
            case R.id.instagramLayout: {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/_u/mbm.espectro.2k16"));
                    intent.setPackage("com.instagram.android");
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.instagram.com/mbm.espectro.2k16")));
                }
            }
            break;
        }
    }

    public void initializeVariables() {
        sharedPref = getActivity().getSharedPreferences(SharedPreferenceKey.SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public void retrieveElements(View rootview) {
        // Retrieving ContactUs elements from fragment_contact_us.xml.
        emailLayout = (LinearLayout) rootview.findViewById(R.id.emailLayout);
        phoneNoLayout = (LinearLayout) rootview.findViewById(R.id.phoneNoLayout);
        facebookLayout = (LinearLayout) rootview.findViewById(R.id.facebookLayout);
        instagramLayout = (LinearLayout) rootview.findViewById(R.id.instagramLayout);

        // Declaring ContactUs elements listener.
        emailLayout.setOnClickListener(this);
        phoneNoLayout.setOnClickListener(this);
        facebookLayout.setOnClickListener(this);
        instagramLayout.setOnClickListener(this);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContactUs newInstance(String sectionID) {
        ContactUs fragment = new ContactUs();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_ID, sectionID);
        fragment.setArguments(args);

        return fragment;
    }
}