package in.mbm.espectro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import in.mbm.espectro.gcm.RegistrationService;
import in.mbm.espectro.model.College;
import in.mbm.espectro.utils.CommonMethod;
import in.mbm.espectro.utils.Constants;
import in.mbm.espectro.utils.SharedPreferenceKey;
import in.mbm.espectro.utils.URLS;

public class Splash extends AppCompatActivity implements View.OnClickListener {

    /**
     * Splash elements.
     */
    RelativeLayout containerLayout;
    ImageView frame1Img, frame2Img, frame3Img, frame4Img;
    TextView espectroTxt, mbmTxt;
    LinearLayout loginLayout, registerLayout;
    ImageButton backBtn;

    // loginLayout elements.
    RelativeLayout emailLayout, passwordLayout;
    EditText emailEdt, passwordEdt;
    View divider;
    Button signInBtn;
    TextView forgotPasswordTxt, registerTxt;

    // registerLayout elements.
    RelativeLayout nameLayout, remailLayout, rpasswordLayout, phoneLayout, collegeLayout;
    EditText nameEdt, remailEdt, rpasswordEdt, phoneEdt;
    AppCompatSpinner collegeSpr;
    View divider1, divider2, divider3, divider4;
    Button registerBtn;
    TextView signInTxt, noteTxt;

    /**
     * Variables.
     */
    private Activity activity;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Typeface rageItalic;
    private Animation shake;
    private ProgressDialog progressDialog;

    private GetCollegeListTask getCollegeListTask;
    private UserForgotPasswordTask userForgotPasswordTask;
    private UserLoginTask userLoginTask;
    private UserRegistrationTask userRegistrationTask;

    private int SPLASH_DURATION = 4000;
    private int DURATION = 100;
    private int DELAY = 50;
    private Boolean status = false;

    private ArrayList<College> COLLEGE_LIST;
    private ArrayList<String> COLLEGE_SLIST;

    String clg_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Mint.initAndStartSession(Splash.this, Constants.BUGSENSE);

        initializeVariables();

        CommonMethod.initializeImageLoader(activity);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        retrieveElements();

        // Changing text font style.
        espectroTxt.setTypeface(rageItalic);

        // Setting up frame images and starting animation.
        startFrameAnimation(frame1Img, R.drawable.frame_1, 600000, 1);
        startFrameAnimation(frame2Img, R.drawable.frame_2, 100000, 1);
        startFrameAnimation(frame3Img, R.drawable.frame_3, 90000, 0);
        startBalloonAnimation();

        // Move to next screen after SPLASH_DURATION sec.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(sharedPref.getString(SharedPreferenceKey.USER_ID, ""))) {
                    status = true;
                    showLoginContent(true);
                } else {
                    Intent i = new Intent(Splash.this, Home.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                    finish();
                }
            }
        }, SPLASH_DURATION);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TextUtils.isEmpty(sharedPref.getString(SharedPreferenceKey.GCM_REGISTRATION_ID, ""))) {
            // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
            Intent intent = new Intent(this, RegistrationService.class);
            startService(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn: {
                CommonMethod.hideKeyboard(activity);

                showRegisterContent(false);
                showLoginContent(true);
            }
            break;
            case R.id.signInBtn: {
                CommonMethod.hideKeyboard(activity);

                String email = emailEdt.getText().toString();
                String password = passwordEdt.getText().toString();

                if (TextUtils.isEmpty(email) || !CommonMethod.validateEmailAddress(email))
                    emailLayout.startAnimation(shake);
                else if (TextUtils.isEmpty(password))
                    passwordLayout.startAnimation(shake);
                else {
                    userLoginTask = new UserLoginTask(email, password);
                    userLoginTask.execute();
                }
            }
            break;
            case R.id.forgotPasswordTxt: {
                CommonMethod.hideKeyboard(activity);

                String email = emailEdt.getText().toString();

                if (TextUtils.isEmpty(email) || !CommonMethod.validateEmailAddress(email))
                    emailLayout.startAnimation(shake);
                else {
                    userForgotPasswordTask = new UserForgotPasswordTask(email);
                    userForgotPasswordTask.execute();
                }
            }
            break;
            case R.id.registerTxt: {
                showLoginContent(false);
                showRegisterContent(true);
            }
            break;
            case R.id.registerBtn: {
                CommonMethod.hideKeyboard(activity);

                String name = nameEdt.getText().toString();
                String email = remailEdt.getText().toString();
                String password = rpasswordEdt.getText().toString();
                String phone_no = phoneEdt.getText().toString();

                if (TextUtils.isEmpty(name))
                    nameLayout.startAnimation(shake);
                else if (TextUtils.isEmpty(email) || !CommonMethod.validateEmailAddress(email))
                    remailLayout.startAnimation(shake);
                else if (TextUtils.isEmpty(password))
                    rpasswordLayout.startAnimation(shake);
                else if (TextUtils.isEmpty(phone_no) || phone_no.length() != 10)
                    phoneLayout.startAnimation(shake);
                else if (TextUtils.isEmpty(clg_id))
                    collegeLayout.startAnimation(shake);
                else {
                    userRegistrationTask = new UserRegistrationTask(name, email, password, phone_no, clg_id);
                    userRegistrationTask.execute();
                }
            }
            break;
            case R.id.signInTxt: {
                showRegisterContent(false);
                showLoginContent(true);
            }
            break;
        }
    }

    public void initializeVariables() {
        activity = this;

        sharedPref = getSharedPreferences(SharedPreferenceKey.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        rageItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "rage_italic.ttf");
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void retrieveElements() {
        // Retrieving Splash elements from activity_splash.xml.
        containerLayout = (RelativeLayout) findViewById(R.id.containerLayout);

        frame1Img = (ImageView) findViewById(R.id.frame1Img);
        frame2Img = (ImageView) findViewById(R.id.frame2Img);
        frame3Img = (ImageView) findViewById(R.id.frame3Img);
        frame4Img = (ImageView) findViewById(R.id.frame4Img);

        espectroTxt = (TextView) findViewById(R.id.espectroTxt);
        mbmTxt = (TextView) findViewById(R.id.mbmTxt);

        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        registerLayout = (LinearLayout) findViewById(R.id.registerLayout);

        backBtn = (ImageButton) findViewById(R.id.backBtn);

        // Retrieving loginLayout elements.
        emailLayout = (RelativeLayout) findViewById(R.id.emailLayout);
        passwordLayout = (RelativeLayout) findViewById(R.id.passwordLayout);

        emailEdt = (EditText) findViewById(R.id.emailEdt);
        passwordEdt = (EditText) findViewById(R.id.passwordEdt);

        divider = (View) findViewById(R.id.divider);

        signInBtn = (Button) findViewById(R.id.signInBtn);

        forgotPasswordTxt = (TextView) findViewById(R.id.forgotPasswordTxt);
        registerTxt = (TextView) findViewById(R.id.registerTxt);

        // Retrieving registerLayout elements.
        nameLayout = (RelativeLayout) findViewById(R.id.nameLayout);
        remailLayout = (RelativeLayout) findViewById(R.id.remailLayout);
        rpasswordLayout = (RelativeLayout) findViewById(R.id.rpasswordLayout);
        phoneLayout = (RelativeLayout) findViewById(R.id.phoneLayout);
        collegeLayout = (RelativeLayout) findViewById(R.id.collegeLayout);

        nameEdt = (EditText) findViewById(R.id.nameEdt);
        remailEdt = (EditText) findViewById(R.id.remailEdt);
        rpasswordEdt = (EditText) findViewById(R.id.rpasswordEdt);
        phoneEdt = (EditText) findViewById(R.id.phoneEdt);

        collegeSpr = (AppCompatSpinner) findViewById(R.id.collegeSpr);

        divider1 = (View) findViewById(R.id.divider1);
        divider2 = (View) findViewById(R.id.divider2);
        divider3 = (View) findViewById(R.id.divider3);
        divider4 = (View) findViewById(R.id.divider4);

        registerBtn = (Button) findViewById(R.id.registerBtn);

        signInTxt = (TextView) findViewById(R.id.signInTxt);
        noteTxt = (TextView) findViewById(R.id.noteTxt);

        // Declaring Splash elements listener.
        backBtn.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
        forgotPasswordTxt.setOnClickListener(this);
        registerTxt.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        signInTxt.setOnClickListener(this);
        collegeSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                clg_id = COLLEGE_LIST.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clg_id = "";
            }
        });
    }

    public void startFrameAnimation(View view, int id, final int duration, final int direction) {
        ImageLoader.getInstance().displayImage("drawable://" + id, (ImageView) view, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Animation animation;
                if (direction == 1)
                    animation = CommonMethod.rotateAnimation(0.0f, 360.0f, 0.5f, 0.5f, duration);
                else
                    animation = CommonMethod.rotateAnimation(360.0f, 0.0f, 0.5f, 0.5f, duration);
                view.startAnimation(animation);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public void startBalloonAnimation() {
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.frame_4, frame4Img, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(CommonMethod.rotateAnimation(0.0f, 360.0f, 0.5f, 0.5f, 50000));
                animationSet.addAnimation(CommonMethod.scaleAnimation(1.0f, 2.0f, 1.0f, 2.0f, 30000));
                animationSet.setFillAfter(true);
                frame4Img.startAnimation(animationSet);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public void showLoginContent(Boolean flag) {
        if (flag) {
            backBtn.setVisibility(View.GONE);

            if (status) {
                // Hiding frames.
                frame1Img.startAnimation(CommonMethod.alphaAnimation(1.0f, 0.0f, DURATION * 2));
                frame2Img.startAnimation(CommonMethod.alphaAnimation(1.0f, 0.0f, DURATION * 2));
                frame3Img.startAnimation(CommonMethod.alphaAnimation(1.0f, 0.0f, DURATION * 2));
                frame4Img.startAnimation(CommonMethod.alphaAnimation(1.0f, 0.0f, DURATION * 2));
                mbmTxt.startAnimation(CommonMethod.alphaAnimation(1.0f, 0.0f, DURATION * 2));

                // Changing color of text and background.
                CommonMethod.colorAnimation(containerLayout, "backgroundColor", "#FFFFFF", "#FF9800", DURATION * 3).start();
                CommonMethod.colorAnimation(espectroTxt, "textColor", "#000000", "#FFFFFF", DURATION * 3).start();

                status = false;
            } else {
                // Changing color of background.
                CommonMethod.colorAnimation(containerLayout, "backgroundColor", "#3E2723", "#FF9800", DURATION * 3).start();
            }

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(CommonMethod.scaleAnimation(1.0f, 1.8f, 1.0f, 1.8f, DURATION * 3));
            animationSet.addAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 0.0f, -3.0f, DURATION * 3, new AccelerateInterpolator(), Animation.RELATIVE_TO_SELF));
            animationSet.setFillAfter(true);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    espectroTxt.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            espectroTxt.startAnimation(animationSet);

            loginLayout.setVisibility(View.VISIBLE);

            emailLayout.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + DELAY, new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            passwordLayout.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 2), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            divider.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 3), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            signInBtn.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 4), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            forgotPasswordTxt.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 5), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            registerTxt.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 6), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
        } else {
            espectroTxt.setAnimation(null);
            espectroTxt.setVisibility(View.GONE);
            loginLayout.setVisibility(View.GONE);
        }
    }

    public void showRegisterContent(Boolean flag) {
        if (flag) {
            backBtn.setVisibility(View.VISIBLE);

            // Changing color of background.
            CommonMethod.colorAnimation(containerLayout, "backgroundColor", "#FF9800", "#3E2723", DURATION * 3).start();

            registerLayout.setVisibility(View.VISIBLE);

            nameLayout.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + DELAY, new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            divider1.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (int) (DELAY * 1.5), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            remailLayout.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 2), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            divider2.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (int) (DELAY * 2.5), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            rpasswordLayout.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 3), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            divider3.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (int) (DELAY * 3.5), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            phoneLayout.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 4), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            divider4.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (int) (DELAY * 4.5), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            collegeLayout.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 5), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            noteTxt.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 5), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            registerBtn.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (int) (DELAY * 5.5), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));
            signInTxt.startAnimation(CommonMethod.translateAnimation(0.0f, 0.0f, 1.0f, 0.0f, (DURATION * 3) + (DELAY * 6), new AccelerateDecelerateInterpolator(), Animation.RELATIVE_TO_PARENT));

            if (COLLEGE_LIST == null || COLLEGE_LIST.size() == 0) {
                getCollegeListTask = new GetCollegeListTask();
                getCollegeListTask.execute();
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, COLLEGE_SLIST);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                collegeSpr.setAdapter(adapter);
            }
        } else {
            registerLayout.setVisibility(View.GONE);
        }
    }

    public class GetCollegeListTask extends AsyncTask<Void, Void, String> {

        GetCollegeListTask() {
        }

        @Override
        protected void onPreExecute() {
            /*if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
            }*/
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                BufferedReader reader = null;

                // URL where data is to be send.
                URL url = new URL(URLS.GET_COLLEGE_LIST);

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
            /*if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();*/

            getCollegeListTask = null;

            if (response != null && !TextUtils.isEmpty(response)) {
                try {
                    JSONArray colleges = new JSONArray(response);

                    if (COLLEGE_LIST == null)
                        COLLEGE_LIST = new ArrayList<College>();
                    else
                        COLLEGE_LIST.clear();

                    if (COLLEGE_SLIST == null)
                        COLLEGE_SLIST = new ArrayList<String>();
                    else
                        COLLEGE_SLIST.clear();

                    for (int i = 0; i < colleges.length(); i++) {
                        College college = new College();

                        college.setId(colleges.getJSONObject(i).optString("id"));
                        college.setName(colleges.getJSONObject(i).optString("name"));

                        COLLEGE_SLIST.add(colleges.getJSONObject(i).optString("name"));
                        COLLEGE_LIST.add(college);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, COLLEGE_SLIST);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    collegeSpr.setAdapter(adapter);

                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CommonMethod.showMessageBar(backBtn, getResources().getString(R.string.error_common_failure));
        }

        @Override
        protected void onCancelled() {
            getCollegeListTask = null;
            /*if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();*/
        }
    }

    public class UserForgotPasswordTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;

        UserForgotPasswordTask(String email) {
            mEmail = email;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("Requesting...");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                BufferedReader reader = null;

                // URL where data is to be send.
                URL url = new URL(URLS.GET_FORGOT_PASSWORD);

                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Accept-Encoding", "gzip");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(3000);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes("email=" + mEmail);
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

            userForgotPasswordTask = null;

            if (response != null && !TextUtils.isEmpty(response)) {
                try {
                    JSONObject main = new JSONObject(response);

                    if (main.optString("result").equals("1")) {
                        CommonMethod.showMessageDialog(activity, "Bingo", main.optString("msg"));

                        return;
                    } else {
                        CommonMethod.showMessageDialog(activity, "Oops!", main.optString("msg"));

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
            userForgotPasswordTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("Validating details...");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                BufferedReader reader = null;

                // URL where data is to be send.
                URL url = new URL(URLS.GET_LOGIN);

                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Accept-Encoding", "gzip");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(3000);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes("email=" + mEmail + "&password=" + mPassword);
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

            userLoginTask = null;

            if (response != null && !TextUtils.isEmpty(response)) {
                try {
                    JSONObject main = new JSONObject(response);

                    if (main.optString("result").equals("1")) {
                        JSONObject data = main.optJSONObject("msg");

                        if (data != null) {
                            editor.putString(SharedPreferenceKey.USER_ID, data.optString("user_id"));
                            editor.putString(SharedPreferenceKey.USER_NAME, data.optString("name"));
                            editor.putString(SharedPreferenceKey.USER_EMAIL, data.optString("email"));
                            editor.putString(SharedPreferenceKey.USER_PHONE_NO, data.optString("phone_no"));
                            editor.putString(SharedPreferenceKey.USER_CLG_ID, data.optString("clg_id"));
                            editor.putString(SharedPreferenceKey.USER_CLG_NAME, data.optString("clg_name"));
                            editor.commit();

                            Intent i = new Intent(Splash.this, Home.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                            finish();

                            return;
                        }
                    } else {
                        CommonMethod.showMessageDialog(activity, "Oops!", main.optString("msg"));

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
            userLoginTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    public class UserRegistrationTask extends AsyncTask<Void, Void, String> {

        private final String mName;
        private final String mEmail;
        private final String mPassword;
        private final String mPhoneNo;
        private final String mClgId;

        UserRegistrationTask(String name, String email, String password, String phone_no, String clg_id) {
            mName = name;
            mEmail = email;
            mPassword = password;
            mPhoneNo = phone_no;
            mClgId = clg_id;
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
                URL url = new URL(URLS.POST_REGISTER);

                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Accept-Encoding", "gzip");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(3000);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes("name=" + mName + "&email=" + mEmail + "&password=" + mPassword + "&phone_no=" + mPhoneNo + "&clg_id=" + mClgId);
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

            userRegistrationTask = null;

            if (response != null && !TextUtils.isEmpty(response)) {
                try {
                    JSONObject main = new JSONObject(response);

                    if (main.optString("result").equals("1")) {
                        CommonMethod.showMessageDialog(activity, "Bingo", main.optString("msg"));

                        showRegisterContent(false);
                        showLoginContent(true);

                        return;
                    } else {
                        CommonMethod.showMessageDialog(activity, "Oops!", main.optString("msg"));

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
            userRegistrationTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (backBtn.isShown()) {
            showRegisterContent(false);
            showLoginContent(true);
        } else
            super.onBackPressed();
    }
}

