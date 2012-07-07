// Copyright 2012 Google Inc. All Rights Reserved.

package com.examples.youtubeapidemo;

import com.google.android.youtube.api.YouTube;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * A simple YouTube Android API demo application which shows how to use a
 * {@link YouTubePlayerActivity} intent to start a YouTube player in a dialog above the current
 * Activity.
 */
public class StandalonePlayerDemoActivity extends YouTubeBaseActivity implements OnClickListener {

  private final String videoId = "nCgQDjiotG0";
  private Button dialogPopoutButton;
  private Button dialogPopoutFullscreenButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.standalone_player_demo);

    YouTube.initialize(this, DeveloperKey.DEVELOPER_KEY);

    dialogPopoutButton = (Button) findViewById(R.id.dialog_popout_button);
    dialogPopoutFullscreenButton = (Button) findViewById(R.id.dialog_popout_fullscreen_button);
    dialogPopoutButton.setOnClickListener(this);
    dialogPopoutFullscreenButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (v == dialogPopoutButton) {
      Intent intent = YouTubePlayerActivity.createIntent(this, videoId, true);
      this.startActivity(intent);
    } else if (v == dialogPopoutFullscreenButton) {
      Intent intent = YouTubePlayerActivity.createIntent(this, videoId, false);
      this.startActivity(intent);
    }
  }

}
