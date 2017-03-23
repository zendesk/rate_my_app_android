package com.zendesk.ratemyapp.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zendesk.logger.Logger;
import com.zendesk.ratemyapp.DialogActionListener;
import com.zendesk.ratemyapp.RateMyAppConfig;
import com.zendesk.ratemyapp.RateMyAppDialog;
import com.zendesk.sdk.feedback.BaseZendeskFeedbackConfiguration;
import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.network.impl.ZendeskConfig;

public class MainActivity extends AppCompatActivity {

    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.zendesk.android";
    private static final String ZENDESK_SUBDOMAIN = "https://[subdomain].zendesk.com";
    private static final String ZENDESK_APP_ID = "[appId]";
    private static final String ZENDESK_OAUTH_CLIENT_ID = "[oauthClientId";

    private RateMyAppConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable logging for debug purposes
        Logger.setLoggable(true);
        // and initialise the Zendesk Support SDK
        initialiseZendesk();

        // Instantiate the demo RateMyAppConfig object we'll be using for our buttons.
        config = new RateMyAppConfig.Builder()
                .withAndroidStoreUrl(PLAY_STORE_URL)
                .withVersion(getApplicationContext(), BuildConfig.VERSION_NAME)
                .build();

        // Show dialog that respects the "Don't Ask Again" behaviour
        setupShowButton();
        // Show dialog that doesn't respect the "Don't Ask Again" behaviour, and always shows
        setupShowAlwaysButton();
        // Show dialog that "sends" feedback by Android's ACTION_SEND Intent, instead of via Zendesk.
        setupShowOtherButton();
    }

    private void setupShowButton() {
        Button showBtn = (Button) findViewById(R.id.buttonShow);

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateMyAppDialog.show(MainActivity.this, config, zendeskActionListener);
            }
        });
    }

    private void setupShowAlwaysButton() {
        Button showAlwaysButton = (Button) findViewById(R.id.buttonShowAlways);

        showAlwaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateMyAppDialog.showAlways(MainActivity.this, config, zendeskActionListener);
            }
        });
    }

    private void setupShowOtherButton() {
        Button showOtherBtn = (Button) findViewById(R.id.buttonShowOther);

        showOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateMyAppDialog.show(MainActivity.this, config, otherActionListener);
            }
        });
    }

    private void initialiseZendesk() {
        // Initialise the Support SDK with subdomain, app ID, and OAuth client ID from the Zendesk console
        ZendeskConfig.INSTANCE.init(this, ZENDESK_SUBDOMAIN, ZENDESK_APP_ID, ZENDESK_OAUTH_CLIENT_ID);
        // Set an Identity, using either Anonymous or JWT authentication.
        ZendeskConfig.INSTANCE.setIdentity(new AnonymousIdentity.Builder().build());
        // ZendeskConfig.INSTANCE.setIdentity(new JwtIdentity("jwt token goes here"));
    }

    /**
     * This DialogActionListener is a simple example of how the {@link RateMyAppDialog} can be used
     * without using Zendesk. The {@link DialogActionListener#onFeedbackButtonClicked(DialogFragment,
     * RateMyAppConfig)} method can be used to do whatever you want. In this case, it simply sends
     * a String to {@link Intent#ACTION_SEND}.
     */
    private DialogActionListener otherActionListener = new DialogActionListener() {
        @Override
        public void onFeedbackButtonClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "The Send Feedback button was clicked!");
            intent.setType("text/plain");
            startActivity(intent);
        }
    };

    /**
     * This DialogActionListener is a simple example of using the Zendesk Support SDK's
     * {@link com.zendesk.sdk.feedback.ui.ContactZendeskActivity} to capture a user's feedback.
     */
    private DialogActionListener zendeskActionListener = new DialogActionListener() {
        @Override
        public void onFeedbackButtonClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
            ContactZendeskActivity.startActivity(MainActivity.this, new BaseZendeskFeedbackConfiguration() {
                @Override
                public String getRequestSubject() {
                    return "Sample Open Source RMA integration";
                }
            });
        }
    };
}
