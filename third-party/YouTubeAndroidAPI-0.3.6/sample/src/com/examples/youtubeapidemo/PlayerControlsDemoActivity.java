// Copyright 2012 Google Inc. All Rights Reserved.

package com.examples.youtubeapidemo;

import com.google.android.youtube.api.YouTube;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayer;
import com.google.android.youtube.api.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.api.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.api.YouTubePlayer.PlaylistEventListener;
import com.google.android.youtube.api.YouTubePlayerView;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * A simple YouTube Android API demo application demonstrating the use of {@link YouTubePlayer}
 * programmatic controls.
 */
public class PlayerControlsDemoActivity extends YouTubeBaseActivity implements
    OnItemSelectedListener,
    OnClickListener,
    OnEditorActionListener {

  private static final ListEntry[] ENTRIES = {
      new ListEntry("Chrome Speed Tests", "nCgQDjiotG0", false),
      new ListEntry("Chrome for Android", "lVjw7n_U37A", false),
      new ListEntry("Galaxy Nexus Prime", "-F_ke3rxopc", false),
      new ListEntry("Dots", "OsnFL-NrYCY", false),
      new ListEntry("Playlist: WebGL demos", "7E952A67F31C58A3", true)};

  private YouTubePlayerView youTubePlayerView;
  private TextView stateText;
  private Spinner videoChooser;
  private Button playButton;
  private Button pauseButton;
  private EditText skipTo;
  private TextView eventLog;
  private StringBuilder logString;

  private MyPlaylistEventListener playlistEventListener;
  private MyPlayerStateChangeListener playerStateChangeListener;
  private MyPlaybackEventListener playbackEventListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    YouTube.initialize(this, DeveloperKey.DEVELOPER_KEY);
    setContentView(R.layout.player_controls_demo);

    youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
    stateText = (TextView) findViewById(R.id.state_text);
    videoChooser = (Spinner) findViewById(R.id.video_chooser);
    playButton = (Button) findViewById(R.id.play_button);
    pauseButton = (Button) findViewById(R.id.pause_button);
    skipTo = (EditText) findViewById(R.id.skip_to_text);
    eventLog = (TextView) findViewById(R.id.event_log);
    logString = new StringBuilder();

    ArrayAdapter<ListEntry> adapter =
        new ArrayAdapter<ListEntry>(this, android.R.layout.simple_spinner_item, ENTRIES);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    videoChooser.setAdapter(adapter);
    videoChooser.setOnItemSelectedListener(this);
    playButton.setOnClickListener(this);
    pauseButton.setOnClickListener(this);
    skipTo.setOnEditorActionListener(this);

    registerPlayerView(youTubePlayerView);

    playlistEventListener = new MyPlaylistEventListener();
    playerStateChangeListener = new MyPlayerStateChangeListener();
    playbackEventListener = new MyPlaybackEventListener();
    youTubePlayerView.setPlaylistEventListener(playlistEventListener);
    youTubePlayerView.setPlayerStateChangeListener(playerStateChangeListener);
    youTubePlayerView.setPlaybackEventListener(playbackEventListener);
  }

  @Override
  protected void onStart() {
    super.onStart();
    videoChooser.setSelection(0);
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    ListEntry selectedVideo = (ListEntry) parent.getItemAtPosition(pos);
    if (selectedVideo.isPlaylist) {
      youTubePlayerView.cuePlaylist(selectedVideo.id);
    } else {
      youTubePlayerView.cueVideo(selectedVideo.id);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onClick(View v) {
    if (v == playButton) {
      youTubePlayerView.play();
    } else if (v == pauseButton) {
      youTubePlayerView.pause();
    }
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (v == skipTo) {
      int skipToSecs = Integer.parseInt(skipTo.getText().toString());
      youTubePlayerView.seekToMillis(skipToSecs * 1000);
      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(skipTo.getWindowToken(), 0);
      return true;
    }
    return false;
  }

  private void updateText() {
    stateText.setText(String.format("Current state: %s %s %s",
        playerStateChangeListener.playerState, playbackEventListener.playbackState,
        playbackEventListener.bufferingState));
  }

  private void log(String message) {
    logString.append(message + "\n");
    eventLog.setText(logString);
  }

  private final class MyPlaylistEventListener implements PlaylistEventListener {
    @Override
    public void onNext() {
      log("NEXT VIDEO");
    }

    @Override
    public void onPrevious() {
      log("PREVIOUS VIDEO");
    }

    @Override
    public void onPlaylistEnded() {
      log("PLAYLIST ENDED");
    }
  }

  private final class MyPlaybackEventListener implements PlaybackEventListener {
    String playbackState = "NOT_PLAYING";
    String bufferingState = "";
    @Override
    public void onPlaying() {
      playbackState = "PLAYING";
      updateText();
      log("\tPLAYING");
    }

    @Override
    public void onBuffering(boolean isBuffering) {
      bufferingState = isBuffering ? "(BUFFERING)" : "";
      updateText();
      log("\t\t" + (isBuffering ? "BUFFERING" : "NOT BUFFERING"));
    }

    @Override
    public void onStopped() {
      playbackState = "STOPPED";
      updateText();
      log("\tSTOPPED");
    }

    @Override
    public void onPaused() {
      playbackState = "PAUSED";
      updateText();
      log("\tPAUSED");
    }

    @Override
    public void onSeekTo(int endPositionMillis) {
      log("\tSEEKTO: " + endPositionMillis);
    }
  }

  private final class MyPlayerStateChangeListener implements PlayerStateChangeListener {
    String playerState = "UNINITIALIZED";

    @Override
    public void onLoading() {
      playerState = "LOADING";
      updateText();
      log(playerState);
    }

    @Override
    public void onLoaded(String videoId) {
      playerState = String.format("LOADED %s", videoId);
      updateText();
      log(playerState);
    }

    @Override
    public void onAdStarted() {
      playerState = "AD_STARTED";
      updateText();
      log(playerState);
    }

    @Override
    public void onVideoStarted() {
      playerState = "VIDEO_STARTED";
      updateText();
      log(playerState);
    }

    @Override
    public void onVideoEnded() {
      playerState = "VIDEO_ENDED";
      updateText();
      log(playerState);
    }

    @Override
    public void onError() {
      playerState = "ERROR";
      updateText();
      log(playerState);
    }
  }

  private static final class ListEntry {

    public final String title;
    public final String id;
    public final boolean isPlaylist;

    public ListEntry(String title, String videoId, boolean isPlaylist) {
      this.title = title;
      this.id = videoId;
      this.isPlaylist = isPlaylist;
    }

    @Override
    public String toString() {
      return title;
    }

  }

}
