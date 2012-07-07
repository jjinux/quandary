package com.google.code.quandary;

import com.google.android.youtube.api.YouTubePlayer;

public class QuizCheckerRunnable implements  Runnable   {
    private YouTubePlayer youTubePlayer;

    public QuizCheckerRunnable(YouTubePlayer youTubePlayer) {
        this.youTubePlayer = youTubePlayer;
    }

    @Override
    public void run() {
        youTubePlayer.pause();
    }
}
