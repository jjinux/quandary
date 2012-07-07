// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.code.quandary;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.google.android.youtube.api.YouTube;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.api.YouTubePlayerView;

/**
 * Sample activity showing how to properly enable custom fullscreen behavior.
 * <p>
 * This is the preferred way of handling fullscreen because the default fullscreen implementation
 * will cause re-buffering of the video.
 */
public class QuizActivity extends YouTubeBaseActivity
        implements OnClickListener, OnFullscreenListener {

    private LinearLayout baseLayout;
    private YouTubePlayerView player;
    private View otherViews;
    private int windowFlags;

    private boolean fullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        windowFlags = Window.FEATURE_NO_TITLE;
        if (Build.VERSION.SDK_INT >= 11) {
            // If you would like the action bar to be shown together with the player's controls when
            // in fullscreen, it has to be an OVERLAY or it will be automatically hidden.
            windowFlags |= Window.FEATURE_ACTION_BAR_OVERLAY;
        }
        requestWindowFeature(windowFlags);

        setContentView(R.layout.quiz);
        baseLayout = (LinearLayout) findViewById(R.id.layout);
        player = (YouTubePlayerView) findViewById(R.id.player);
        otherViews = findViewById(R.id.other_views);
        if (Build.VERSION.SDK_INT >= 11) {
            if (getActionBar() != null) {
                getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            }
        }

        YouTube.initialize(this, DeveloperKey.DEVELOPER_KEY);
        registerPlayerView(player);

        // Specify that we want handle fullscreen behavior ourselves.
        player.enableCustomFullscreen(this);

        // You can use your own button to switch to fullscreen too
        findViewById(R.id.fullscreen_button).setOnClickListener(this);

        doLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.loadVideo("9c6W4CCU9M4");
    }

    @Override
    public void onClick(View v) {
        player.setFullscreen(!fullscreen);
    }

    private void doLayout() {
        if (fullscreen) {
            // When in fullscreen, the visibility of all other views than the player should be set to
            // GONE and the player should be laid out across the whole screen.
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) player.getLayoutParams();
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.MATCH_PARENT;

            otherViews.setVisibility(View.GONE);

            baseLayout.setPadding(0, 0, 0, 0);
        } else {
            // This layout is up to you - it's just a very simple demo (vertically stacked boxes in
            // portrait, horizontally stacked in landscape).
            boolean landscape =
                    getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
            setParamsForOrientation(player, landscape);
            setParamsForOrientation(otherViews, landscape);
            otherViews.setVisibility(View.VISIBLE);
            baseLayout.setOrientation(landscape ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
            int topPadding = windowFlags != Window.FEATURE_NO_TITLE ? getActionBarHeightPx() : 0;
            baseLayout.setPadding(0, topPadding, 0, 0);
        }
    }

    private void setParamsForOrientation(View view, boolean landscape) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        // We use a LinearLayout with weights and set the size of the dimension we share with the other
        // box to 0
        params.width = landscape ? 0 : LayoutParams.MATCH_PARENT;
        params.height = landscape ? LayoutParams.MATCH_PARENT : 0;
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        fullscreen = isFullscreen;
        doLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    private int getActionBarHeightPx() {
        int[] attrs = new int[] { android.R.attr.actionBarSize };
        return (int) getTheme().obtainStyledAttributes(attrs).getDimension(0, 0f);
    }

}