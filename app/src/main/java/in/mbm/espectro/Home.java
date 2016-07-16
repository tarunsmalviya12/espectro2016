package in.mbm.espectro;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.splunk.mint.Mint;

import in.mbm.espectro.adapter.HomeAdapter;
import in.mbm.espectro.gcm.RegistrationService;
import in.mbm.espectro.utils.CommonMethod;
import in.mbm.espectro.utils.Constants;
import in.mbm.espectro.utils.SharedPreferenceKey;

/**
 * Created by tarunsmalviya12 on 13/4/16.
 */
public class Home extends AppCompatActivity implements View.OnClickListener {

    /**
     * Home elements.
     */
    LinearLayout containerLayout;
    TextView titleTxt;
    ViewPager viewPager;
    InkPageIndicator viewPagerIndicator;

    /**
     * Variables.
     */
    private Activity activity;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private HomeAdapter homeAdapter;
    private ValueAnimator mColorAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Mint.initAndStartSession(Home.this, Constants.BUGSENSE);

        initializeVariables();
        CommonMethod.initializeImageLoader(activity);
        retrieveElements();

        // Setting up background animation.
        mColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                containerLayout.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        mColorAnimation.setDuration((5 - 1) * 10000000000l);

        // Setting up view pager adapter.
        viewPager.setAdapter(homeAdapter);

        // Setting up view pager to page indicator.
        viewPagerIndicator.setViewPager(viewPager);
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

    }

    public void initializeVariables() {
        activity = this;

        sharedPref = getSharedPreferences(SharedPreferenceKey.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        homeAdapter = new HomeAdapter(getSupportFragmentManager());

        mColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor("#ff5c52"), Color.parseColor("#856088"), Color.parseColor("#5AC366"), Color.parseColor("#d74c32"), Color.parseColor("#0098d8"));
    }

    public void retrieveElements() {
        // Retrieving Home elements from activity_home.xml.
        containerLayout = (LinearLayout) findViewById(R.id.containerLayout);

        titleTxt = (TextView) findViewById(R.id.titleTxt);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPagerIndicator = (InkPageIndicator) findViewById(R.id.viewPagerIndicator);

        // Declaring Home elements listener.
        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                page.setAlpha(1 - Math.abs(position));
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mColorAnimation.setCurrentPlayTime((long) ((positionOffset + position) * 10000000000l));
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    titleTxt.setText("Profile");
                else if (position == 1)
                    titleTxt.setText("Events");
                else if (position == 2)
                    titleTxt.setText("Notifications");
                else if (position == 3)
                    titleTxt.setText("Sponsors");
                else if (position == 4)
                    titleTxt.setText("Contact Us");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
