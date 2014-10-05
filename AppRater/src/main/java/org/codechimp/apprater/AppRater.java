package org.codechimp.apprater;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class AppRater {
    // Preference Constants
    private final static String PREF_NAME = "apprater";
    private final static String PREF_LAUNCH_COUNT = "launch_count";
    private final static String PREF_FIRST_LAUNCHED = "date_firstlaunch";
    private final static String PREF_DONT_SHOW_AGAIN = "dontshowagain";
    private final static String PREF_REMIND_LATER = "remindmelater";
    private final static String PREF_APP_VERSION_NAME = "app_version_name";
    private final static String PREF_APP_VERSION_CODE = "app_version_code";

    // Required
    private Context mContext;

    // Options
    private int daysUntilPromptForRemindLater = 3;
    private int launchesUntilPromptForRemindLater = 7;
    private boolean isDark;
    private boolean themeSet;
    private boolean hideNoButton;
    private boolean isVersionNameCheckEnabled;
    private boolean isVersionCodeCheckEnabled;
    private Market market = new GoogleMarket();

    public static class Builder {
        // Required
        private Context mContext;

        // Options
        private int daysUntilPromptForRemindLater = 3;
        private int launchesUntilPromptForRemindLater = 7;
        private boolean isDark;
        private boolean themeSet;
        private boolean hideNoButton;
        private boolean isVersionNameCheckEnabled;
        private boolean isVersionCodeCheckEnabled;
        private Market market = new GoogleMarket();

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder daysUntilPromptForRemindLater(int days) {
            this.daysUntilPromptForRemindLater = days;
            return this;
        }

        public Builder launchesUntilPromptForRemindLater(int launches) {
            this.launchesUntilPromptForRemindLater = launches;
            return this;
        }

        public Builder isDark(boolean isDark) {
            this.isDark = isDark;
            this.themeSet = true;
            return this;
        }

        public Builder hideNoButton(boolean hideNoButton) {
            this.hideNoButton = hideNoButton;
            return this;
        }

        public Builder isVersionNameCheckEnabled(boolean enabled) {
            this.isVersionNameCheckEnabled = enabled;
            return this;
        }

        public Builder isVersionCodeCheckEnabled(boolean enabled) {
            this.isVersionCodeCheckEnabled = enabled;
            return this;
        }

        public Builder market(Market market) {
            this.market = market;
            return this;
        }

        public AppRater build() {
            return new AppRater(this);
        }
    }

    public AppRater(Context context) {
        mContext = context;

        // init dialog
        initDialog();
    }

    private AppRater(Builder builder) {
        // init options
        initOptions(builder);

        // init dialog
        initDialog();
    }

    private void initOptions(Builder builder) {
        mContext = builder.mContext;
        this.daysUntilPromptForRemindLater = builder.daysUntilPromptForRemindLater;
        this.launchesUntilPromptForRemindLater = builder.launchesUntilPromptForRemindLater;
        this.isDark = builder.isDark;
        this.themeSet = builder.themeSet;
        this.hideNoButton = builder.hideNoButton;
        this.isVersionNameCheckEnabled = builder.isVersionNameCheckEnabled;
        this.isVersionCodeCheckEnabled = builder.isVersionCodeCheckEnabled;
        this.market = builder.market;
    }

    private void initDialog() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        ApplicationRatingInfo ratingInfo = ApplicationRatingInfo.createApplicationInfo(mContext);
        int days;
        int launches;
        if (isVersionNameCheckEnabled) {
            if (!ratingInfo.getApplicationVersionName().equals(prefs.getString(PREF_APP_VERSION_NAME, "none"))) {
                editor.putString(PREF_APP_VERSION_NAME, ratingInfo.getApplicationVersionName());
                resetData();
                commitOrApply(editor);
            }
        }
        if (isVersionCodeCheckEnabled) {
            if (ratingInfo.getApplicationVersionCode() != (prefs.getInt(PREF_APP_VERSION_CODE, -1))) {
                editor.putInt(PREF_APP_VERSION_CODE, ratingInfo.getApplicationVersionCode());
                resetData();
                commitOrApply(editor);
            }
        }
        if (prefs.getBoolean(PREF_DONT_SHOW_AGAIN, false)) {
            return;
        } else if (prefs.getBoolean(PREF_REMIND_LATER, false)) {
            days = daysUntilPromptForRemindLater;
            launches = launchesUntilPromptForRemindLater;
        } else {
            days = daysUntilPromptForRemindLater;
            launches = launchesUntilPromptForRemindLater;
        }

        // Increment launch counter
        long launch_count = prefs.getLong(PREF_LAUNCH_COUNT, 0) + 1;
        editor.putLong(PREF_LAUNCH_COUNT, launch_count);
        // Get date of first launch
        Long date_firstLaunch = prefs.getLong(PREF_FIRST_LAUNCHED, 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(PREF_FIRST_LAUNCHED, date_firstLaunch);
        }
        // Wait for at least the number of launches or the number of days used
        // until prompt
        if (launch_count >= launches || (System.currentTimeMillis() >= date_firstLaunch + (days * 24 * 60 * 60 * 1000))) {
            showRateAlertDialog(editor);
        }
        commitOrApply(editor);
    }

    /**
     * Call this method directly if you want to force a rate prompt, useful for
     * testing purposes
     */
    public void showRateDialog() {
        showRateAlertDialog(null);
    }

    /**
     * Call this method directly to go straight to play store listing for rating
     */
    public void rateNow() {
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, market.getMarketURI(mContext)));
        } catch (ActivityNotFoundException activityNotFoundException1) {
            Log.e(AppRater.class.getSimpleName(), "Market Intent not found");
        }
    }

    /**
     * The meat of the library, actually shows the rate prompt dialog
     */
    @SuppressLint("NewApi")
    private void showRateAlertDialog(final SharedPreferences.Editor editor) {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= 11 && themeSet) {
            builder = new AlertDialog.Builder(mContext, (isDark ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT));
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        ApplicationRatingInfo ratingInfo = ApplicationRatingInfo.createApplicationInfo(mContext);
        builder.setTitle(String.format(mContext.getString(R.string.dialog_title), ratingInfo.getApplicationName()));

        builder.setMessage(mContext.getString(R.string.rate_message));

        builder.setPositiveButton(mContext.getString(R.string.rate),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rateNow();
                        if (editor != null) {
                            editor.putBoolean(PREF_DONT_SHOW_AGAIN, true);
                            commitOrApply(editor);
                        }
                        dialog.dismiss();
                    }
                });

        builder.setNeutralButton(mContext.getString(R.string.later),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (editor != null) {
                            Long date_firstLaunch = System.currentTimeMillis();
                            editor.putLong(PREF_FIRST_LAUNCHED, date_firstLaunch);
                            editor.putLong(PREF_LAUNCH_COUNT, 0);
                            editor.putBoolean(PREF_REMIND_LATER, true);
                            editor.putBoolean(PREF_DONT_SHOW_AGAIN, false);
                            commitOrApply(editor);
                        }
                        dialog.dismiss();
                    }
                });
        if (!hideNoButton) {
            builder.setNegativeButton(mContext.getString(R.string.no_thanks),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (editor != null) {
                                editor.putBoolean(PREF_DONT_SHOW_AGAIN, true);
                                editor.putBoolean(PREF_REMIND_LATER, false);
                                long date_firstLaunch = System.currentTimeMillis();
                                editor.putLong(PREF_FIRST_LAUNCHED, date_firstLaunch);
                                editor.putLong(PREF_LAUNCH_COUNT, 0);
                                commitOrApply(editor);
                            }
                            dialog.dismiss();
                        }
                    });
        }
        builder.show();
    }

    @SuppressLint("NewApi")
    private void commitOrApply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT > 8) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public void resetData() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_DONT_SHOW_AGAIN, false);
        editor.putBoolean(PREF_REMIND_LATER, false);
        editor.putLong(PREF_LAUNCH_COUNT, 0);
        long date_firstLaunch = System.currentTimeMillis();
        editor.putLong(PREF_FIRST_LAUNCHED, date_firstLaunch);
        commitOrApply(editor);
    }
}
