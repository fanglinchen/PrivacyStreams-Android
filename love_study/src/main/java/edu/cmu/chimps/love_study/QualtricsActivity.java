package edu.cmu.chimps.love_study;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.cmu.chimps.love_study.pam.PAMActivity;
import edu.cmu.chimps.love_study.reminders.ReminderManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class QualtricsActivity extends AppCompatActivity {
    private boolean isRandomized = false;
//    private int reminderId = -1;
    private ReminderManager reminderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qualtrics);
        reminderManager = new ReminderManager();
        String surveyUrl = getIntent().getStringExtra(Constants.URL.KEY_SURVEY_URL);
//        reminderId = getIntent().getIntExtra(ReminderManager.KEY_REMINDER_ID,-1);

        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setAccessibilityDelegate(new View.AccessibilityDelegate());
        webView.loadUrl(surveyUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains(Constants.URL.DAILY_EMA_URL)){
                    isRandomized = true;
                }
                view.loadUrl(url);
//                if(reminderId!=-1){
//                    Reminder reminder = reminderManager.getReminder(reminderId);
//                    reminder.answeredToday = false;
//                }

                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(reminderId!=-1){
//            Reminder reminder = reminderManager.getReminder(reminderId);
//            reminder.answeredToday = true;
////            reminderManager.updateReminder(reminder);
//        }

        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isRandomized)
            startActivity(new Intent(this,PAMActivity.class));
    }
}
