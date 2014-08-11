package com.livebos.cordova.library;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DemoActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView text = new TextView(this);
        final LinearLayout view = new LinearLayout(this);
        final Button child = new Button(this);
        child.setText("返回");
        child.setOnClickListener(this);
        view.addView(child);
        view.addView(text);
        setContentView(view);
        Uri url=getIntent().getData();
        if (url == null) {
            text.setText("URL is null.");
        } else {
            text.setText(url.toString());
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
