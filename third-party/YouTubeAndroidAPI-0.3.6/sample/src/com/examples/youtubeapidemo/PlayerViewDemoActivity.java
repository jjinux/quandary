package com.examples.youtubeapidemo;

import com.google.android.youtube.api.YouTube;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayer;
import com.google.android.youtube.api.YouTubePlayerView;

import android.os.Bundle;

/**
 *
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * displays a YouTube Video in a {@link YouTubePlayerView} that is registered with a
 * {@link YouTubeBaseActivity}.
 */
public class PlayerViewDemoActivity extends YouTubeBaseActivity {

  private YouTubePlayer youTubePlayer;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    YouTube.initialize(this, DeveloperKey.DEVELOPER_KEY);
    setContentView(R.layout.playerview_demo);

    YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
    registerPlayerView(youTubeView);
    youTubePlayer = youTubeView;
  }

  @Override
  protected void onStart() {
    super.onStart();
    youTubePlayer.loadVideo("nCgQDjiotG0");
  }

}
