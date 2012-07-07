package com.examples.youtubeapidemo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import com.google.android.youtube.api.YouTube;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayer;
import com.google.android.youtube.api.YouTubePlayerView;
import com.google.android.youtube.api.YouTubeThumbnailView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.examples.youtubeapidemo.ui.FlippingView;
import com.examples.youtubeapidemo.ui.ImageWallView;

/**
 * A demo application aimed at showing the capabilities of the YouTube Player API.  It shows a video
 * wall of flipping YouTube thumbnails.  Every 5 flips, one of the thumbnails will be replaced with
 * a playing YouTube video.
 */
public class VideoWallDemoActivity extends YouTubeBaseActivity implements FlippingView.Listener {

  private static final String PLAYLIST_ID = "FA594B0BBF1EDFC5";

  private static final int NUMBER_OF_ROWS = 4;
  private static final int INTER_IMAGE_PADDING_DP = 5;

  private static final int INITIAL_FLIP_DURATION_MILLIS = 100;
  private static final int FLIP_DURATION_MILLIS = 500;
  private static final int FLIP_PERIOD_MILLIS = 2000;
  private static final int FLIPS_TILL_NEXT_VIDEO = 5;

  private ImageWallView imageWallView;
  private Handler flipDelayHandler;

  private FlippingView flippingView;
  private YouTubeThumbnailView flipInThumbnail;
  private ImageView flipOutThumbnail;

  private YouTubePlayerView playerView;

  private int flippingCol;
  private int flippingRow;
  private int videoCol;
  private int videoRow;

  private int flipsSinceVideoStarted;
  private boolean nextThumbnailLoaded;
  private boolean allThumbnailsLoaded;
  private boolean videoPlaying;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    YouTube.initialize(this, DeveloperKey.DEVELOPER_KEY);

    ViewGroup viewFrame = new FrameLayout(this);

    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    int interImagePaddingPx = (int) displayMetrics.density * INTER_IMAGE_PADDING_DP;
    int imageHeight = (displayMetrics.heightPixels / NUMBER_OF_ROWS) - interImagePaddingPx;
    int imageWidth = imageHeight * 16 / 9;

    imageWallView = new ImageWallView(this, imageWidth, imageHeight, interImagePaddingPx);
    viewFrame.addView(imageWallView, MATCH_PARENT, MATCH_PARENT);

    flipInThumbnail = new YouTubeThumbnailView(this);
    flipInThumbnail.setOnThumbnailLoadedListener(new ThumbnailListener());

    flipOutThumbnail = new ImageView(this);
    flipOutThumbnail.setLayoutParams(new LayoutParams(imageWidth, imageHeight));

    flippingView =
        new FlippingView(this, this, imageWidth, imageHeight, flipInThumbnail, flipOutThumbnail);
    flippingView.setLayoutParams(new LayoutParams(imageWidth, imageHeight));
    flippingView.setFlipDuration(INITIAL_FLIP_DURATION_MILLIS);
    flipInThumbnail.setPlaylist(PLAYLIST_ID);
    viewFrame.addView(flippingView);

    playerView = new YouTubePlayerView(this);
    registerPlayerView(playerView);
    playerView.setLayoutParams(new LayoutParams(imageWidth, imageHeight));
    playerView.setShowControls(false);
    playerView.setVisibility(View.INVISIBLE);
    playerView.setPlaybackEventListener(new PlayerListener());
    viewFrame.addView(playerView);

    flipDelayHandler = new FlipDelayHandler();

    setContentView(viewFrame);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (allThumbnailsLoaded) {
      flipDelayHandler.sendEmptyMessage(0);
      loadNextVideo();
    }
  }

  @Override
  protected void onPause() {
    flipDelayHandler.removeCallbacksAndMessages(null);
    super.onPause();
  }

  private void flipNext() {
    if (!nextThumbnailLoaded) {
      return;
    }

    if (videoPlaying && (++flipsSinceVideoStarted >= FLIPS_TILL_NEXT_VIDEO)) {
      flippingCol = videoCol;
      flippingRow = videoRow;
      imageWallView.showImage(videoCol, videoRow);
      playerView.setVisibility(View.INVISIBLE);
      loadNextVideo();
    } else {
      Pair<Integer, Integer> nextTarget = imageWallView.getNextLoadTarget();
      flippingCol = nextTarget.first;
      flippingRow = nextTarget.second;
    }

    flippingView.setX(imageWallView.getXPosition(flippingCol, flippingRow));
    flippingView.setY(imageWallView.getYPosition(flippingCol, flippingRow));
    flipOutThumbnail.setImageDrawable(imageWallView.getImageDrawable(flippingCol, flippingRow));
    imageWallView.setImageDrawable(flippingCol, flippingRow, flipInThumbnail.getDrawable());
    imageWallView.hideImage(flippingCol, flippingRow);
    flippingView.setVisibility(View.VISIBLE);
    flippingView.flip();
  }

  @Override
  public void onFlipped(FlippingView view) {
    imageWallView.showImage(flippingCol, flippingRow);
    flippingView.setVisibility(View.INVISIBLE);
    loadNextThumbnail();

    if (videoPlaying && playerView.getVisibility() == View.INVISIBLE) {
      // show the playing video
      videoCol = flippingCol;
      videoRow = flippingRow;
      imageWallView.hideImage(flippingCol, flippingRow);
      playerView.setX(imageWallView.getXPosition(flippingCol, flippingRow));
      playerView.setY(imageWallView.getYPosition(flippingCol, flippingRow));
      playerView.setVisibility(View.VISIBLE);
      flipsSinceVideoStarted = 0;
    }

    if (!allThumbnailsLoaded && imageWallView.allImagesLoaded()) {
      allThumbnailsLoaded = true;
      flippingView.setFlipDuration(FLIP_DURATION_MILLIS);
      flipDelayHandler.sendEmptyMessage(0);
      playerView.loadPlaylist(PLAYLIST_ID);
    }
  }

  private void loadNextThumbnail() {
    nextThumbnailLoaded = false;
    if (flipInThumbnail.hasNext()) {
      flipInThumbnail.next();
    } else {
      flipInThumbnail.first();
    }
  }

  private void loadNextVideo() {
    if (playerView.hasNext()) {
      playerView.next();
    } else {
      playerView.loadPlaylist(PLAYLIST_ID); // restart from beginning of playlist
    }
  }

  /**
   * A handler that periodically flips an element on the video wall.
   */
  private final class FlipDelayHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      if (isFinishing()) {
        return;
      }
      flipNext();
      sendEmptyMessageDelayed(0, FLIP_PERIOD_MILLIS);
    }

  }

  /**
   * An internal listener which listens to thumbnail loading events from the
   * {@link YouTubeThumbnailView}.
   */
  private final class ThumbnailListener implements YouTubeThumbnailView.OnThumbnailLoadedListener {

    @Override
    public void onThumbnailLoaded(YouTubeThumbnailView thumbnail) {
      if (isFinishing()) {
        return;
      }
      nextThumbnailLoaded = true;
      if (!allThumbnailsLoaded) {
        flipNext();
      }
    }

    @Override
    public void onThumbnailError(YouTubeThumbnailView thumbnail, Exception e) {
      if (isFinishing()) {
        return;
      }
      loadNextThumbnail();
    }

  }

  /**
   * An internal listener which listens to playback events from the {@link YouTubePlayer}.
   */
  private final class PlayerListener implements YouTubePlayer.PlaybackEventListener {

    @Override
    public void onBuffering(boolean buffering) {
      videoPlaying = !buffering;
    }

    @Override
    public void onPlaying() {
      videoPlaying = true;
    }

    @Override
    public void onPaused() {
      videoPlaying = false;
    }

    @Override
    public void onStopped() {
      videoPlaying = false;
    }

    @Override
    public void onSeekTo(int endPositionMillis) { }

  }

}
