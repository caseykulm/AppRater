package org.codechimp.appraterdemo;

import org.codechimp.apprater.AppRater;
import org.codechimp.apprater.GoogleMarket;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;

public class MainActivity extends Activity {

	private Button buttonTest;
    private AppRater mAppRater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		buttonTest = (Button) findViewById(R.id.button1);
		
		buttonTest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				// This forces display of the rate prompt.
				// It should only be used for testing purposes
				mAppRater.showRateDialog();
			}
		});

        /**
         * AppRater.Builder showing every option to choose
         * from as well as the default value if an option is
         * not set.
          */
        mAppRater = new AppRater.Builder(this)
                .daysUntilPromptForRemindLater(3)
                .launchesUntilPromptForRemindLater(7)
                .isDark(false)
                .hideNoButton(false)
                .isVersionNameCheckEnabled(false)
                .isVersionCodeCheckEnabled(false)
                .market(new GoogleMarket())
                .build();

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.menu_ratenow): {
                mAppRater.rateNow();
                return true;
            }
        }
        return false;
    }
}
