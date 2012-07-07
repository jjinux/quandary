// Copyright 2012 Google Inc. All Rights Reserved.

package com.examples.youtubeapidemo;

import com.google.android.youtube.api.YouTube;
import com.google.android.youtube.api.YouTubePlayer;
import com.google.android.youtube.api.YouTubePlayerFragment;

import android.app.Activity;
import android.os.Bundle;

/**
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * shows a YouTube Video in a {@link YouTubePlayerFragment} fragment.
 */
public class FragmentDemoActivity extends Activity {

  private YouTubePlayer youTubeFragment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    YouTube.initialize(this, DeveloperKey.DEVELOPER_KEY);
    setContentView(R.layout.fragments_demo);

    youTubeFragment = (YouTubePlayer) getFragmentManager().findFragmentById(R.id.youtube_fragment);
  }

  @Override
  protected void onStart() {
    super.onStart();
    youTubeFragment.loadVideo("nCgQDjiotG0");
  }

}
