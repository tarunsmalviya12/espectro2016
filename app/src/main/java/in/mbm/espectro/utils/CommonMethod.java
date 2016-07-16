package in.mbm.espectro.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by tarunsmalviya12 on 24/9/15.
 */
public class CommonMethod {

    /**
     * Variables.
     */

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public CommonMethod() {
    }

    /**
     * Check whether email address is valid or not.
     *
     * @param email
     * @return true if valid, else false
     */
    public static boolean validateEmailAddress(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
        return email.matches(emailPattern);
    }

    /**
     * Display a dialog box with given title and message.
     *
     * @param context
     * @param title
     * @param message
     */
    public static void showMessageDialog(Context context, String title, String message) {
        if (context == null)
            return;

        try {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display a message in snackbar bar.
     *
     * @param view
     * @param message
     */
    public static void showMessageBar(View view, String message) {
        if (view == null || view.getContext() == null)
            return;

        try {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hide keyboard.
     *
     * @param activity
     * @return true if keyboard is hidden successfully, else false
     */
    public static boolean hideKeyboard(Activity activity) {
        try {
            // Check if no view has focus
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void initializeImageLoader(Context context) {
        if (context != null) {
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new FadeInBitmapDisplayer(300)).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .defaultDisplayImageOptions(defaultOptions)
                    .memoryCache(new WeakMemoryCache())
                    .memoryCacheSize(10 * 1024 * 1024)
                    .build();
            ImageLoader.getInstance().init(config);
        }
    }

    public static ValueAnimator colorAnimation(Object obj, String property, String fromColor, String toColor, int duration) {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(obj, property, Color.parseColor(fromColor), Color.parseColor(toColor));
        colorAnim.setDuration(duration);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setInterpolator(new AccelerateInterpolator());

        return colorAnim;
    }

    public static Animation rotateAnimation(float from, float to, float pivotX, float pivotY, int duration) {
        RotateAnimation rotateAnimation = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(duration);

        return rotateAnimation;
    }

    public static Animation alphaAnimation(float from, float to, int duration) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(from, to);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setFillAfter(true);

        return alphaAnimation;
    }

    public static Animation scaleAnimation(float fromX, float toX, float fromY, float toY, int duration) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(duration);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setFillAfter(true);

        return scaleAnimation;
    }

    public static Animation translateAnimation(float fromX, float toX, float fromY, float toY, int duration, Interpolator interpolator, int relative) {
        TranslateAnimation translateAnimation = new TranslateAnimation(relative, fromX, relative, toX, relative, fromY, relative, toY);
        translateAnimation.setDuration(duration);
        translateAnimation.setInterpolator(interpolator);
        translateAnimation.setFillAfter(true);

        return translateAnimation;
    }
}