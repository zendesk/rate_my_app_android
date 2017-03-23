package com.zendesk.ratemyapp;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Object representing the configuration to use for a {@link RateMyAppDialog}. Specifically, this
 * class contains a {@link String} {@code storeUrl}, a {@link String} {@code appVersion}, and a
 * {@link List} of {@link RateMyAppRule}s.
 *
 * The {@link RateMyAppRule}s are used by
 * {@link RateMyAppDialog#show(AppCompatActivity, RateMyAppConfig, DialogActionListener)} to determine
 * whether or not to show the dialog.
 *
 * The {@code storeUrl} is used in
 * {@link DialogActionListener#onStoreButtonClicked(DialogFragment, RateMyAppConfig)} to start a
 * {@link android.content.Intent#ACTION_VIEW}.
 *
 * If a {@code appVersion} has been set, a default {@link RateMyAppRule} is added which checks for
 * the existence of the {@code appVersion} in {@link android.content.SharedPreferences}, and doesn't
 * allow the dialog to show if the String is found. The {@code appVersion} is stored when the user clicks
 * any button in the {@link RateMyAppDialog}.
 */
public class RateMyAppConfig {

    private static final String LOG_TAG = "RateMyAppConfig";

    private String storeUrl;

    private List<RateMyAppRule> rules;

    private String appVersion;

    private RateMyAppConfig(Builder builder) {
        this.storeUrl = builder.storeUrl;
        this.rules = builder.rules;
        this.appVersion = builder.appVersion;
    }

    boolean canShow() {
        boolean canShow = true;
        for (RateMyAppRule rule: rules) {
            boolean rulePermits = rule.permitDisplay();
            if (!rulePermits) {
                Log.d(LOG_TAG, rule.denialMessage());
            }
            canShow &= rule.permitDisplay();
        }
        return canShow;
    }

    String getStoreUrl() {
        return storeUrl;
    }

    String getAppVersion() {
        return appVersion;
    }

    /**
     * Builder class used to instantiate a {@link RateMyAppConfig}.
     *
     * All methods are technically optional, but are used to facilitate the behaviour of
     * {@link RateMyAppDialog} and {@link DialogActionListener}.
     *
     * A {@code storeUrl} should be set using {@link Builder#withAndroidStoreUrl(String)}. This
     * enables the Play Store app to be launched at the provided URL.
     *
     * Using {@link Builder#withVersion(Context, String)} enables the "Don't ask again" behaviour,
     * by checking for a stored copy of the version String and preventing display of the dialog if
     * a stored copy is found.
     *
     * The {@link Builder#withRule(RateMyAppRule)} method can be used to add arbitrary rules to the
     * config that can prevent the dialog from being shown.
     *
     */
    public static class Builder {

        private String storeUrl;

        private String appVersion;

        private List<RateMyAppRule> rules = new ArrayList<>();

        /**
         * Sets the URL for the app store listing to which ratings should be directed. This should
         * be the Google Play Store (or other app store) URL for the app.
         *
         * This is used by the default implementation of
         * {@link DialogActionListener#onStoreButtonClicked(DialogFragment, RateMyAppConfig)} to launch
         * an {@link android.content.Intent#ACTION_VIEW}. On a device with the Google Play Store
         * installed, this will automatically open the Play Store app to the app's listing page.
         *
         * @param storeUrl the app store URL for your app
         * @return the builder
         */
        public Builder withAndroidStoreUrl(String storeUrl) {
            this.storeUrl = storeUrl;

            return this;
        }

        /**
         * Sets a version name String which will be used for "Don't ask again" behaviour. The value
         * of this should be {@code BuildConfig.VERSION_NAME}. This method adds a
         * {@link RateMyAppRule} which will return {@code false} if the provided version name
         * String is found in storage.
         *
         * <p>
         *     Clicking any button on the {@link RateMyAppDialog}, not just the "Don't ask again"
         *     button, will store this version name String.
         * </p>
         *
         * Once the version has been stored, the
         * {@link RateMyAppDialog#show(AppCompatActivity, RateMyAppConfig, DialogActionListener)}
         * method will never show the dialog again for the same version String, unless app
         * storage has been cleared. Alternately, a different version String can be used, or the
         * {@link RateMyAppDialog#showAlways(AppCompatActivity, RateMyAppConfig, DialogActionListener)}
         * method.
         *
         * @param context context of your application
         * @param version the version name of your app, usually {@code BuildConfig.VERSION_NAME}
         * @return the builder
         */
        public Builder withVersion(final Context context, final String version) {
            this.appVersion = version;

            this.rules.add(new RateMyAppRule() {
                @Override
                public boolean permitDisplay() {
                    String storedVersion = context.getSharedPreferences(RateMyAppDialog.PREFS_FILE,
                            Context.MODE_PRIVATE).getString(RateMyAppDialog.PREFS_DONT_ASK_VERSION_KEY, "");

                    boolean canShow = !storedVersion.equals(version);

                    if (!canShow) {
                        Log.d(LOG_TAG, "Cannot show RateMyAppDialog, user has selected not to show again for this version");
                    }
                    return canShow;
                }

                @Override
                public String denialMessage() {
                    String storedVersion = context.getSharedPreferences(RateMyAppDialog.PREFS_FILE,
                            Context.MODE_PRIVATE).getString(RateMyAppDialog.PREFS_DONT_ASK_VERSION_KEY, "");

                    return String.format(Locale.US, "Stored version is %s, current version is %s, returning false.",
                            storedVersion, appVersion);
                }
            });

            return this;
        }

        /**
         * Adds an arbitrary {@link RateMyAppRule} which will be used in determining whether or not
         * to show the dialog in
         * {@link RateMyAppDialog#show(AppCompatActivity, RateMyAppConfig, DialogActionListener)}.
         *
         * Any rules added are ignored by
         * {@link RateMyAppDialog#showAlways(AppCompatActivity, RateMyAppConfig, DialogActionListener)}.
         *
         * @param rule the rule to apply
         * @return the Builder
         */
        public Builder withRule(RateMyAppRule rule) {
            this.rules.add(rule);

            return this;
        }

        /**
         * Creates the instance of {@link RateMyAppConfig}
         *
         * @return the configured instance of {@link RateMyAppConfig}
         */
        public RateMyAppConfig build() {
            return new RateMyAppConfig(this);
        }
    }
}
