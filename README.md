:warning: *Use of this software is subject to important terms and conditions as set forth in the License file* :warning:

# Zendesk Sample Apps for Android

## Description
This repository provides you with an open-source alternative to the "Rate My App" feature of versions
1.x of the Zendesk Support SDK for Android. It is a simple library with no integration with or 
dependency on the Zendesk platform.    
 
## Owners
If you have any questions please email support@zendesk.com.
 
## Getting Started
To include the RateMyApp library in your app, add the Gradle dependency:
```
    compile com.zendesk:ratemyapp:1.0.0
```

Then, in an Activity:

1. Create an instance of `RateMyAppConfig` using its `Builder`, making sure to give it a Google Play
URL and an app version like so:
```java
     RateMyAppConfig config = new RateMyAppConfig.Builder()
                .withAndroidStoreUrl(PLAY_STORE_URL)
                .withVersion(getApplicationContext(), BuildConfig.VERSION_NAME)
                .build();
```

2. Create an instance of `DialogActionListener`, implementing its `onFeedbackButtonClicked` method.

Here's an example that integrates with the Zendesk Support SDK:
``` java
    DialogActionListener actionListener = new DialogActionListener() {
        @Override
        public void onFeedbackButtonClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
            ContactZendeskActivity.startActivity(MainActivity.this, null);
        }
    };
```
And here's an example that doesn't:
``` java
    DialogActionListener actionListener = new DialogActionListener() {
        @Override
        public void onFeedbackButtonClicked(DialogFragment dialogFragment, RateMyAppConfig config) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "The Send Feedback button was clicked!");
            intent.setType("text/plain");
            startActivity(intent);
        }
    };
```

3. Start the `RateMyAppDialog`, passing it the `RateMyAppConfig` and the `DialogActionListener`:
``` java
    RateMyAppDialog.show(MainActivity.this, config, actionListener);
```

## More Advanced Usage
### Customizing button behaviour
`DialogActionListener` has three important methods, corresponding to click events from buttons on 
`RateMyAppDialog`:
- `onStoreButtonClicked`
- `onFeedbackButtonClicked`
- `onDontAskAgainClicked`

For convenience, each of these can be overridden, but `onFeedbackButtonClicked` *needs* to be 
implemented.  

By default, clicks on the buttons will also result in the version name `String` provided in the 
`RateMyAppConfig` being stored and the dialog dismissed. To customize this behaviour, you can 
override these methods, which are called at an earlier stage in the control flow:
- `storeButtonClicked`
- `feedbackButtonClicked`
- `dontAskAgainClicked`

### Customizing when to show the dialog
The `RateMyAppConfig.Builder` allows you to add arbitrary `RateMyAppRule`s, which are used when 
`show` is called on the `RateMyAppDialog`. If any rule returns `false`, the dialog will not show, and
the rule's `denialMessage` will be logged to Logcat. Note that using the `showAlways` method ignores
all rules. 

## Contributions
Pull requests are welcome.
 
## Bugs
Please submit bug reports to [Zendesk](https://support.zendesk.com/requests/new).
 