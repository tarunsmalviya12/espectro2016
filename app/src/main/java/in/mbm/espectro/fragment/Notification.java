package in.mbm.espectro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import in.mbm.espectro.R;
import in.mbm.espectro.adapter.NotificationAdapter;
import in.mbm.espectro.utils.CommonMethod;
import in.mbm.espectro.utils.SharedPreferenceKey;
import in.mbm.espectro.utils.URLS;

/**
 * Created by tarunsmalviya12 on 17/4/16.
 */
public class Notification extends Fragment implements View.OnClickListener {

    /**
     * Notification fragment elements.
     */
    RecyclerView recycleView;
    ProgressBar progressBar;

    /*
     * Variables.
     */
    private static final String ARG_SECTION_ID = "section_id";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private ArrayList<in.mbm.espectro.model.Notification> NOTIFICATION_LIST;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private GetNotificationListTask getNotificationListTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        rootView.setOnClickListener(this);

        initializeVariables();
        retrieveElements(rootView);

        // Setting up recycle view.
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setAdapter(mAdapter);

        // Starting background thread to fetch Notification list.
        getNotificationListTask = new GetNotificationListTask();
        getNotificationListTask.execute();

        return rootView;
    }

    @Override
    public void onClick(View view) {

    }


    public void initializeVariables() {
        sharedPref = getActivity().getSharedPreferences(SharedPreferenceKey.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        NOTIFICATION_LIST = new ArrayList<in.mbm.espectro.model.Notification>();

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new NotificationAdapter(getActivity(), NOTIFICATION_LIST);
    }

    public void retrieveElements(View rootview) {
        // Retrieving Notification elements from fragment_notification.xml.
        recycleView = (RecyclerView) rootview.findViewById(R.id.recycleView);

        progressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);

        // Declaring Notification elements listener.
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Notification newInstance(String sectionID) {
        Notification fragment = new Notification();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_ID, sectionID);
        fragment.setArguments(args);

        return fragment;
    }

    public class GetNotificationListTask extends AsyncTask<Void, Void, String> {

        GetNotificationListTask() {
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
                URL url = new URL(URLS.GET_NOTIFICATION_LIST);

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

            getNotificationListTask = null;

            if (response != null && !TextUtils.isEmpty(response)) {
                try {
                    JSONArray notifications = new JSONArray(response);

                    if (NOTIFICATION_LIST == null)
                        NOTIFICATION_LIST = new ArrayList<in.mbm.espectro.model.Notification>();
                    else
                        NOTIFICATION_LIST.clear();

                    for (int i = 0; i < notifications.length(); i++) {
                        in.mbm.espectro.model.Notification notification = new in.mbm.espectro.model.Notification();

                        notification.setId(notifications.getJSONObject(i).optString("id"));
                        notification.setMessage(notifications.getJSONObject(i).optString("message"));
                        notification.setDate_time(notifications.getJSONObject(i).optString("date_time"));

                        NOTIFICATION_LIST.add(notification);
                    }

                    if (mAdapter != null && isAdded())
                        mAdapter.notifyDataSetChanged();

                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CommonMethod.showMessageBar(progressBar, getResources().getString(R.string.error_common_failure));
        }

        @Override
        protected void onCancelled() {
            getNotificationListTask = null;
            if (progressBar != null && !progressBar.isShown() && isAdded())
                progressBar.setVisibility(View.VISIBLE);
        }
    }
}