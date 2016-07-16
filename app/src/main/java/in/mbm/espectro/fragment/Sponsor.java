package in.mbm.espectro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import in.mbm.espectro.R;
import in.mbm.espectro.utils.CommonMethod;
import in.mbm.espectro.utils.SharedPreferenceKey;
import in.mbm.espectro.utils.URLS;

/**
 * Created by tarunsmalviya12 on 26/10/15.
 */
public class Sponsor extends Fragment implements View.OnClickListener {

    /**
     * Sponsor fragment elements.
     */
    LinearLayout containerLayout;
    ProgressBar progressBar;

    /*
     * Variables.
     */
    private static final String ARG_SECTION_ID = "section_id";
    private SharedPreferences sharedPref;

    private GetSponsorListTask getSponsorListTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sponsor, container, false);
        rootView.setOnClickListener(this);

        initializeVariables();
        retrieveElements(rootView);

        // Starting background thread to fetch Sponsor list.
        getSponsorListTask = new GetSponsorListTask();
        getSponsorListTask.execute();

        return rootView;
    }

    @Override
    public void onClick(View view) {

    }

    public void initializeVariables() {
        sharedPref = getActivity().getSharedPreferences(SharedPreferenceKey.SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public void retrieveElements(View rootview) {
        // Retrieving Sponsor elements from fragment_sponsor.xml.
        containerLayout = (LinearLayout) rootview.findViewById(R.id.containerLayout);

        progressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);

        // Declaring Sponsor elements listener.
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Sponsor newInstance(String sectionID) {
        Sponsor fragment = new Sponsor();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_ID, sectionID);
        fragment.setArguments(args);

        return fragment;
    }

    public class GetSponsorListTask extends AsyncTask<Void, Void, String> {

        GetSponsorListTask() {
        }

        @Override
        protected void onPreExecute() {
            if (progressBar != null && !progressBar.isShown())
                progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                BufferedReader reader = null;

                // URL where data is to be send.
                URL url = new URL(URLS.GET_SPONSOR_LIST);

                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Accept-Encoding", "gzip");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(3000);
                conn.setDoInput(true);
                conn.setDoOutput(false);

                InputStream inputStream = conn.getInputStream();

                Boolean hasGzipHeader = false;
                Map<String, List<String>> headers = conn.getHeaderFields();
                List<String> contentEncodings = headers.get("Content-Encoding");

                if (contentEncodings != null) {
                    for (String header : contentEncodings) {
                        if (header.equalsIgnoreCase("gzip")) {
                            hasGzipHeader = true;
                            break;
                        }
                    }
                }
                if (hasGzipHeader) {
                    inputStream = new GZIPInputStream(inputStream);
                }

                // Get the server response.
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + "\n");
                }

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "NULL";
        }

        @Override
        protected void onPostExecute(String response) {
            if (progressBar != null && progressBar.isShown() && isAdded())
                progressBar.setVisibility(View.GONE);

            getSponsorListTask = null;

            if (response != null && !TextUtils.isEmpty(response) && isAdded()) {
                try {
                    JSONArray parent_array = new JSONArray(response);

                    for (int i = 0; i < parent_array.length(); i++) {
                        JSONObject parent_obj = parent_array.getJSONObject(i);
                        Iterator iterator = parent_obj.keys();

                        if (i != 0) {
                            View divider = LayoutInflater.from(getActivity()).inflate(R.layout.item_divider, containerLayout, false);
                            divider.setAlpha(0.5f);
                            containerLayout.addView(divider);
                        }

                        // Adding Sponsor title.
                        View title = LayoutInflater.from(getActivity()).inflate(R.layout.item_title_text, containerLayout, false);
                        TextView titleTxt = (TextView) title.findViewById(R.id.titleTxt);

                        titleTxt.setText(iterator.next().toString());
                        containerLayout.addView(title);


                        JSONArray sponsors = parent_obj.getJSONArray(titleTxt.getText().toString().trim());
                        for (int j = 0; j < sponsors.length(); j++) {
                            JSONObject sponsor = sponsors.getJSONObject(j);

                            if (j != 0) {
                                View divider = LayoutInflater.from(getActivity()).inflate(R.layout.item_divider_short, containerLayout, false);
                                divider.setAlpha(0.3f);
                                containerLayout.addView(divider);
                            }

                            // Adding Sponsor.
                            View s_item = LayoutInflater.from(getActivity()).inflate(R.layout.item_sponsor, containerLayout, false);
                            TextView nameTxt = (TextView) s_item.findViewById(R.id.nameTxt);
                            ImageView logoImg = (ImageView) s_item.findViewById(R.id.logoImg);

                            s_item.setTag(sponsor.optString("link"));
                            nameTxt.setTag(sponsor.optString("link"));
                            logoImg.setTag(sponsor.optString("link"));

                            s_item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String tag = (String) v.getTag();
                                    if (!TextUtils.isEmpty(tag))
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tag)));
                                }
                            });

                            nameTxt.setText(sponsor.optString("name"));
                            ImageLoader.getInstance().displayImage(URLS.SPONSOR_IMG_URL + sponsor.optString("id") + ".png", logoImg);

                            containerLayout.addView(s_item);
                        }
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CommonMethod.showMessageBar(progressBar, getResources().getString(R.string.error_common_failure));
        }

        @Override
        protected void onCancelled() {
            getSponsorListTask = null;
            if (progressBar != null && !progressBar.isShown() && isAdded())
                progressBar.setVisibility(View.VISIBLE);
        }
    }
}