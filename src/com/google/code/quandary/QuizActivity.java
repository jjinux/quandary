// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.code.quandary;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import com.google.android.youtube.api.YouTube;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayer;
import com.google.android.youtube.api.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.api.YouTubePlayerView;
import com.google.code.quandary.quiz.Question;
import com.google.code.quandary.quiz.Quiz;

import java.util.List;

/**
 * Sample activity showing how to properly enable custom fullscreen behavior.
 * <p/>
 * This is the preferred way of handling fullscreen because the default fullscreen implementation
 * will cause re-buffering of the video.
 */
public class QuizActivity extends YouTubeBaseActivity implements OnFullscreenListener,
        YouTubePlayer.PlaybackEventListener {

    private static final int SMALL_AMOUNT_OF_TIME = 250;

    private LinearLayout baseLayout;
    private View otherViews;
    private int windowFlags;
    private boolean fullscreen;
    private Handler mHandler;
    private YouTubePlayerView player;
    private Quiz quiz;
    private Question mCurrentQuestion;
    private RadioGroup mAnswersRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(getMainLooper());
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
        if (getActionBar() != null) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
        Intent intent = getIntent();
        quiz = (Quiz) intent.getSerializableExtra("myquiz");

        YouTube.initialize(this, DeveloperKey.DEVELOPER_KEY);
        registerPlayerView(player);
        player.setPlaybackEventListener(this);

        // Specify that we want handle fullscreen behavior ourselves.
        player.enableCustomFullscreen(this);

        doLayout();
    }

    public void showQuizScoreActivity() {
        Intent intent = new Intent(this, QuizScoreActivity.class);
        intent.putExtra("quiz", quiz);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player!=null && quiz !=null &&quiz.getVideoId()!=null )  {
        player.loadVideo(quiz.getVideoId());
        player.setFullscreen(true);
        }
    }

    /**
     * Figure out what the next question is and how long we should wait before pausing.
     * React gracefully if the user scrubs to a new part of the video.
     */
    public void onPlaying() {
        // Hide the questions. This will happen if the user hit the play button or if he submitted an answer.
        hideQuestions();

        // Figure out the closest question to show next.
        mCurrentQuestion = null;
        List<Question> questions = quiz.getQuestions();
        int currentTime = player.getCurrentTimeMillis();
        for (Question question : questions) {

            // Continue if this question has already passed.
            if (question.getTimeToPause() < currentTime) {
                continue;
            }

            // Don't use this question if we just finished answering it very recently. (Unfortunately, time control
            // isn't totally exact, so we have to avoid answering the same question again right after the user answers
            // it.)
            long timeUntilQuestion = question.getTimeToPause() - currentTime;
            if (timeUntilQuestion < SMALL_AMOUNT_OF_TIME &&
                question.getUserAnswer() != null) {
                continue;
            }

            // Use this question if we don't already have one.
            if (mCurrentQuestion == null) {
                mCurrentQuestion = question;
                continue;
            }

            // Use this question if it is sooner than the current next question.
            long timeUntilMCurrentQuestion = mCurrentQuestion.getTimeToPause() - currentTime;
            if (timeUntilQuestion < timeUntilMCurrentQuestion) {
                mCurrentQuestion = question;
            }
        }

        // Stop the video when it's time for the next question.
        if (mCurrentQuestion != null) {
            long timeUntilMCurrentQuestion = mCurrentQuestion.getTimeToPause() - currentTime;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    player.pause();
                }
            }, timeUntilMCurrentQuestion);
        }
    }

    public void onPaused() {
        showQuestions();
    }

    /**
     * Show the user his score. This will happen whether the user finished the quiz or just decided to hit the stop
     * button.
     */
    public void onStopped() {
        showQuizScoreActivity();
    }

    public void onBuffering(boolean b) {
    }

    public void onSeekTo(int i) {
    }

    private void showQuestions() {
        player.setFullscreen(false);
        LinearLayout questionsLayout = (LinearLayout) findViewById(R.id.quiz_layout);
        questionsLayout.removeAllViews();

        if (mCurrentQuestion == null) {
            return;
        }

        TextView questionTextView = new TextView(this);
        questionTextView.setText(mCurrentQuestion.getQuestionDescription());
        questionsLayout.addView(questionTextView);

        mAnswersRadioGroup = new RadioGroup(this);
        List<String> answers = mCurrentQuestion.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            RadioButton radioButton = new RadioButton(this);

            // The id must be positive but need not be unique.
            radioButton.setId(i + 1);

            radioButton.setText(answers.get(i));
            mAnswersRadioGroup.addView(radioButton);
        }
        questionsLayout.addView(mAnswersRadioGroup);

        Button submitButton = new Button(this);
        submitButton.setText(R.string.submit);
        questionsLayout.addView(submitButton);

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitButtonClicked();
            }
        });
    }

    public void onSubmitButtonClicked() {
        int answerIndex = mAnswersRadioGroup.getCheckedRadioButtonId() - 1;
        mCurrentQuestion.setUserAnswer(answerIndex);
        player.play();
        player.setFullscreen(true);
    }

    private void hideQuestions() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.quiz_layout);
        layout.removeAllViews();
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
        int[] attrs = new int[]{android.R.attr.actionBarSize};
        return (int) getTheme().obtainStyledAttributes(attrs).getDimension(0, 0f);
    }

}