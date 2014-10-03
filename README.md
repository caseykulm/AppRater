# AppRater

AppRater is a library for Android designed to facilitate easy prompting of users to rate your app within the Google Play store or Amazon App Store.
It won't prompt until at least 3 days or 7 uses of the app has passed and if the user chooses to rate later the count will start again.

AppRater inherits your themeing so can be used with light or dark variants as seen here;

![Example Image Dark][1] ![Example Image Light][2]

## Quick Setup

### Single Activity Usage

``` java
public class MyActivity extends Activity {
    private AppRater mAppRater;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppRater = AppRater.Builder(this)
                ...
                .build();

        mAppRater.showRateDialog();
    }

    private void letMeRate() {
        mAppRater.rateNow();
    }
}
```

### Application Singleton Usage

``` java
public class MyAppRater extends AppRater {
    private static MyAppRater mInstance;

    public static MyAppRater getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyAppRater(context);
        }

        return mInstance;
    }

    private MyAppRater(Context context) {
        MyAppRater myAppRater = AppRater.Builder(context.getApplicationContext())
                        ...
                        .build();

        return myAppRater;
    }
}
```

With this you can simply call `MyAppRater.getInstance(context)`, to retrieve an instance from
anywhere in your app with all of the customization set once. This is the preferred implementation
if you only intend to have one style.

## Configuration

There are several options you can also use to change the default behavior.

``` java
// An example using every option with default values.
AppRater.Builder(context)
        .daysUntilPrompt(3)
        .launchesUntilPrompt(7)
        .isDark(true)
        .hideNoButton(false)
        .isVersionNameCheckEnabled(false)
        .isVersionCodeCheckEnabled(false)
        .market(new GoogleMarket())
        .build();
```

An AppRater object can be instantiated with these same default values simply with,

``` java
AppRater appRater = new AppRater();
```

**Option Details:**
 * `daysUntilPrompt(int days)`
    Number of day until the user will be prompted (Default 3 days)
 * `launchesUntilPrompt(int numLaunches)`
 Minimum number of launches before prompt is displayed.
    This has higher priority than `daysUntilPrompt(int days)`. (Default 7 launches)
 * `isDark(boolean isDark)`
    If true then `android.app.AlertDialog.THEME_HOLO_DARK` is used,
    if false then `android.app.AlertDialog.THEME_HOLO_LIGHT` is used. (Default to app style)
 * `hideNoButton(boolean hide)`
    If true then no "No thank you" option is available. (Default `false`)
 * `isVersionNameCheckEnabled(boolean checkName)`
    If true then re-enable prompt. (Default `false`)
 * `isVersionCodeCheckEnabled(boolean checkCode)`
    If true then re-enable prompt. (Default `false`)
 * `market(Market market)`
    Class that implements `Market` interface. (Default `GoogleMarket`)

You can implement your own market, implementing the Market interface and parse your URI.

If you want to have a "Rate Now" menu option to go straight to your play store listing call `AppRater.rateNow(this);` within your menu code.

Try out the demo within this repository.

## Gradle

AppRater is now pushed to Maven Central as an AAR, so you just need to add the following dependency to your `build.gradle`.
    
    dependencies {
        compile 'com.github.codechimp-org.apprater:library:1.0.+'
    }

## Translations

If you would like to help localise this library please contribute to the GetLocalization project located here
[http://www.getlocalization.com/AppRater/](http://www.getlocalization.com/AppRater/)

## Contributing Code

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## Developed By

Andrew Jackson <andrew@codechimp.org>

Google+ profile: 
[https://plus.google.com/+AndrewJacksonUK](https://plus.google.com/+AndrewJacksonUK)

Adapted from a snippet originally posted [here](http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater)

## License

    Copyright 2013 Andrew Jackson

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.





 [1]: https://raw.github.com/codechimp-org/AppRater/master/Screenshots/demo-dark.png
 [2]: https://raw.github.com/codechimp-org/AppRater/master/Screenshots/demo-light.png
