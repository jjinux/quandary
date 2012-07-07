// Copyright 2012 Google Inc. All Rights Reserved.

package com.examples.youtubeapidemo;

import com.google.android.youtube.api.YouTube;
import com.google.android.youtube.api.YouTubeIntents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.examples.youtubeapidemo.adapter.DemoArrayAdapter;
import com.examples.youtubeapidemo.adapter.DemoListViewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A sample activity which shows how to use the {@link YouTubeIntents} static methods to create
 * Intents that navigate the user to Activities within the main YouTube application.
 */
public final class IntentsDemoActivity extends Activity implements OnItemClickListener {

  private static final String VIDEO_ID = "kcOUWjkGBUY";
  private static final String PLAYLIST_ID = "F3DFB800F05F551A";
  private static final String USER_ID = "Google";
  private static final int SELECT_VIDEO_REQUEST = 1000;

  private List<DemoListViewItem> intentItems;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    YouTube.initialize(this, DeveloperKey.DEVELOPER_KEY);
    setContentView(R.layout.intents_demo);

    intentItems = new ArrayList<DemoListViewItem>();
    intentItems.add(new IntentItem("Play Video", IntentType.PLAY_VIDEO));
    intentItems.add(new IntentItem("Open Playlist", IntentType.OPEN_PLAYLIST));
    intentItems.add(new IntentItem("Play Playlist", IntentType.PLAY_PLAYLIST));
    intentItems.add(new IntentItem("Open User", IntentType.OPEN_USER));
    intentItems.add(new IntentItem("Open Search Results", IntentType.OPEN_SEARCH));
    intentItems.add(new IntentItem("Upload Video", IntentType.UPLOAD_VIDEO));

    ListView listView = (ListView) findViewById(R.id.intent_list);
    DemoArrayAdapter adapter =
        new DemoArrayAdapter(this, R.layout.list_item, intentItems);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);

    TextView youTubeVersionText = (TextView) findViewById(R.id.youtube_version_text);
    String version = YouTubeIntents.getInstalledYouTubeVersionName(this);
    if (version != null) {
      String text = String.format(getString(R.string.youtube_currently_installed), version);
      youTubeVersionText.setText(text);
    } else {
      youTubeVersionText.setText(getString(R.string.youtube_not_installed));
    }
  }

  public boolean isIntentTypeEnabled(IntentType type) {
    switch (type) {
      case PLAY_VIDEO:
        return YouTubeIntents.canResolvePlayVideoIntent(this);
      case OPEN_PLAYLIST:
        return YouTubeIntents.canResolveOpenPlaylistIntent(this);
      case PLAY_PLAYLIST:
        return YouTubeIntents.canResolvePlayPlaylistIntent(this);
      case OPEN_SEARCH:
        return YouTubeIntents.canResolveSearchIntent(this);
      case OPEN_USER:
        return YouTubeIntents.canResolveUserIntent(this);
      case UPLOAD_VIDEO:
        return YouTubeIntents.canResolveUploadIntent(this);
    }

    return false;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    IntentItem clickedIntentItem = (IntentItem) intentItems.get(position);

    Intent intent;
    switch (clickedIntentItem.type) {
      case PLAY_VIDEO:
        intent = YouTubeIntents.createPlayVideoIntentWithOptions(VIDEO_ID, true, false);
        startActivity(intent);
        break;
      case OPEN_PLAYLIST:
        intent = YouTubeIntents.createOpenPlaylistIntent(PLAYLIST_ID);
        startActivity(intent);
        break;
      case PLAY_PLAYLIST:
        intent = YouTubeIntents.createPlayPlaylistIntent(PLAYLIST_ID);
        startActivity(intent);
        break;
      case OPEN_SEARCH:
        intent = YouTubeIntents.createSearchIntent(USER_ID);
        startActivity(intent);
        break;
      case OPEN_USER:
        intent = YouTubeIntents.createUserIntent(USER_ID);
        startActivity(intent);
        break;
      case UPLOAD_VIDEO:
        // This will load a picker view in the users' gallery.
        // The upload activity is started in the function onActivityResult.
        intent = new Intent(Intent.ACTION_PICK, null).setType("video/*");
        startActivityForResult(intent, SELECT_VIDEO_REQUEST);
        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case SELECT_VIDEO_REQUEST:
          Intent intent = YouTubeIntents.createUploadIntent(returnedIntent.getData());
          startActivity(intent);
          break;
      }
    }
    super.onActivityResult(requestCode, resultCode, returnedIntent);
  }

  private enum IntentType {
    PLAY_VIDEO,
    OPEN_PLAYLIST,
    PLAY_PLAYLIST,
    OPEN_USER,
    OPEN_SEARCH,
    UPLOAD_VIDEO;
  }

  private final class IntentItem implements DemoListViewItem {

    public final String title;
    public final IntentType type;

    public IntentItem(String title, IntentType type) {
      this.title = title;
      this.type = type;
    }

    @Override
    public String getTitle() {
      return title;
    }

    @Override
    public boolean isEnabled() {
      return isIntentTypeEnabled(type);
    }

    @Override
    public String getDisabledText() {
      return getString(R.string.intent_disabled);
    }

  }

}
