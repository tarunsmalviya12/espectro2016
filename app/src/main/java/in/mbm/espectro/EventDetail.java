package in.mbm.espectro;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.splunk.mint.Mint;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import in.mbm.espectro.adapter.HomeAdapter;
import in.mbm.espectro.utils.CommonMethod;
import in.mbm.espectro.utils.Constants;
import in.mbm.espectro.utils.SharedPreferenceKey;
import in.mbm.espectro.utils.URLS;

/**
 * Created by tarunsmalviya12 on 17/4/16.
 */
public class EventDetail extends AppCompatActivity implements View.OnClickListener {

    /**
     * EventDetail elements.
     */
    ImageView eventImg;
    Button registerBtn;
    TextView nameTxt, infoTxt, timeTxt, venueTxt, priceTxt, descTxt;
    ImageButton backBtn;

    /**
     * Variables.
     */
    private Activity activity;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private HomeAdapter homeAdapter;
    private ValueAnimator mColorAnimation;

    private String id, name, info, time, venue, inter, intra, desc;
    private ProgressDialog progressDialog;
    private EventRegisterTask eventRegisterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Mint.initAndStartSession(EventDetail.this, Constants.BUGSENSE);

        initializeVariables();
        CommonMethod.initializeImageLoader(activity);
        retrieveElements();

        try {
            id = getIntent().getExtras().getString("id");
            name = getIntent().getExtras().getString("name");
            info = getIntent().getExtras().getString("info");
            time = getIntent().getExtras().getString("time");
            venue = getIntent().getExtras().getString("venue");
            inter = getIntent().getExtras().getString("inter");
            intra = getIntent().getExtras().getString("intra");
            desc = getIntent().getExtras().getString("desc");

            // Setting up information.
            ImageLoader.getInstance().displayImage(URLS.EVENT_IMG_URL + id + ".jpg", eventImg);

            nameTxt.setText(name);
            infoTxt.setText(info);

            SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date schedule = fd.parse(time);

            SimpleDateFormat date = new SimpleDateFormat("dd MMMM, yyyy");
            SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
            timeTxt.setText(date.format(schedule));

            venueTxt.setText(venue);

            if (intra.equals("0"))
                priceTxt.setText("Free");
            else
                priceTxt.setText(intra);

            descTxt.setText(desc);
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.registerBtn: {
                eventRegisterTask = new EventRegisterTask(id, name.toUpperCase(), sharedPref.getString(SharedPreferenceKey.USER_ID, ""), sharedPref.getString(SharedPreferenceKey.USER_NAME, ""), sharedPref.getString(SharedPreferenceKey.USER_EMAIL, ""));
                eventRegisterTask.execute();
            }
            break;
        }
    }

    public void initializeVariables() {
        activity = this;

        sharedPref = getSharedPreferences(SharedPreferenceKey.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void retrieveElements() {
        // Retrieving EventDetail elements from activity_event_detail.xml.
        eventImg = (ImageView) findViewById(R.id.eventImg);

        registerBtn = (Button) findViewById(R.id.registerBtn);

        nameTxt = (TextView) findViewById(R.id.nameTxt);
        infoTxt = (TextView) findViewById(R.id.infoTxt);
        timeTxt = (TextView) findViewById(R.id.timeTxt);
        venueTxt = (TextView) findViewById(R.id.venueTxt);
        priceTxt = (TextView) findViewById(R.id.priceTxt);
        descTxt = (TextView) findViewById(R.id.descTxt);

        backBtn = (ImageButton) findViewById(R.id.backBtn);

        // Declaring EventDetail elements listener.
        backBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    public class EventRegisterTask extends AsyncTask<Void, Void, String> {

        private String mEventId;
        private String mEventName;
        private String mUserId;
        private String mUserName;
        private String mUserEmail;

        EventRegisterTask(String event_id, String event_name, String user_id, String user_name, String user_email) {
            mEventId = event_id;
            mEventName = event_name;
            mUserId = user_id;
            mUserName = user_name;
            mUserEmail = user_email;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("Registering...");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                BufferedReader reader = null;

                // URL where data is to be send.
                URL url = new URL(URLS.POST_EVENT_REGISTER);

                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Accept-Encoding", "gzip");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(3000);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes("event_id=" + mEventId + "&event_name=" + mEventName + "&user_id=" + mUserId + "&user_name=" + mUserName + "&user_email=" + mUserEmail);
                dataOutputStream.flush();
                dataOutputStream.close();

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
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            eventRegisterTask = null;

            if (response != null && !TextUtils.isEmpty(response)) {
                try {
                    JSONObject main = new JSONObject(response);

                    if (main.optString("result").equals("1")) {
                        CommonMethod.showMessageDialog(activity, "Bingo", main.optString("msg"));

                        return;
                    } else {
                        CommonMethod.showMessageDialog(activity, "Error", main.optString("msg"));

                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CommonMethod.showMessageBar(backBtn, getResources().getString(R.string.error_common_failure));
        }

        @Override
        protected void onCancelled() {
            eventRegisterTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
}
