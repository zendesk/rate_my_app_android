package com.zendesk.ratemyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.DialogFragment;

import static com.zendesk.ratemyapp.RateMyAppDialog.PREFS_DONT_ASK_VERSION_KEY;
import static com.zendesk.ratemyapp.RateMyAppDialog.PREFS_FILE;

/**
 * Defines the behaviour for the callback object which will be called when a user clicks on one of
 * buttons in the {@link RateMyAppDialog}, and provides default implementations for the "Yes, rate
 * my app" button and the "Don't ask again" button, <b>but not the "No, send feedback" button</b>.
 *
 * The {@link DialogActionListener#onFeedbackButtonClicked(DialogFragment, RateMyAppConfig)} method
 * must be implemented by the client to gather feedback from the user. A sample integration with the
 * Zendesk Support SDK is available in the sample app.
 *
 * Each action stores the version name (if not null) provided in the {@link RateMyAppConfig},
 * which is used in the default {@link RateMyAppRule} to determine whether or not to show the
 * {@link RateMyAppDialog}. Effectively, this turns every button into a "Don't ask again" button,
 * though the first two perform other actions too.
 *
 * Each action also dismisses the dialog.
 */
public abstract class DialogActionListener {

    /**
     * This method is called when the "No, send feedback" button is clicked, before the version name
     * is stored and the dialog is dismissed.
     *
     * It should be used to gather feedback from the user and handle it as desired. A sample
     * integration with the Zendesk Support SDK is available in our sample app.
     *
     * @param dialogFragment the {@link RateMyAppDialog} which was shown
     * @param config the {@link RateMyAppConfig} which was used to configure the dialog.
     */
    abstract public void onFeedbackButtonClicked(DialogFragment dialogFragment, RateMyAppConfig config);

    /**
     * This method is called when the "Yes, rate our app" button is clicked, before the version name
     * is stored and dialog is dismissed.
     *
     * By default, it uses the value of {@link RateMyAppConfig#getStoreUrl()} with a
     * {@link Intent#ACTION_VIEW} to launch the Google Play Store (or a browser).
     *
     * @param dialogFragment the {@link RateMyAppDialog} which was shown
     * @param config the {@link RateMyAppConfig} which was used to configure the dialog.
     */
    protected void onStoreButtonClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
        final Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(config.getStoreUrl()));

        dialogFragment.getContext().startActivity(storeIntent);

        storeVersion(dialogFragment.getContext(), config.getAppVersion());
        dialogFragment.dismiss();
    }

    /**
     * This method is called when the "Don't ask again" button is clicked. It is intentionally empty,
     * and exists only in case a client wants to override it.
     *
     * After it's called, the version name returned by {@link RateMyAppConfig#getAppVersion()} is
     * stored and the dialog is dismissed.
     *
     * @param dialogFragment the {@link RateMyAppDialog} which was shown
     * @param config the {@link RateMyAppConfig} which was used to configure the dialog.
     */
    protected void onDontAskAgainClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
        // Intentionally empty.
    }

    final void feedbackButtonClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
        onFeedbackButtonClicked(dialogFragment, config);

        storeVersionAndDismiss(dialogFragment, config);
    }

    final void storeButtonClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
        onStoreButtonClicked(dialogFragment, config);

        storeVersionAndDismiss(dialogFragment, config);
    }

    final void dontAskAgainClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
        onDontAskAgainClicked(dialogFragment, config);

        storeVersionAndDismiss(dialogFragment, config);
    }

    private void storeVersionAndDismiss(DialogFragment fragment, RateMyAppConfig config) {
        storeVersion(fragment.getContext(), config.getAppVersion());
        fragment.dismiss();
    }

    private void storeVersion(Context context, String version) {
        if (context != null && version != null) {
            SharedPreferences.Editor sharedPrefsEditor = context
                    .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).edit();
            sharedPrefsEditor.putString(PREFS_DONT_ASK_VERSION_KEY, version);
            sharedPrefsEditor.apply();
        }
    }

}
