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

    private LinearLayout baseLayout;
    private View otherViews;
    private int windowFlags;
    private boolean fullscreen;
    private Handler mHandler;
    private YouTubePlayerView player;
    private Quiz quiz;
    private Integer mCurrentQuestionIndex = 0;
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

        // You can use your own button to switch to fullscreen too
        findViewById(R.id.fullscreen_button).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        player.setFullscreen(!fullscreen);
                    }
                }
        );

        // Go to the QuizScoreActivity when the user presses "Finish".
        findViewById(R.id.finish_button).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent;
                        intent = new Intent(QuizActivity.this, QuizScoreActivity.class);
                        intent.putExtra("quiz", quiz);
                        startActivity(intent);
                    }
                }
        );

        doLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.loadVideo(quiz.getVideoId());
    }

    public void onPlaying() {
        List<Question> questions = quiz.getQuestions();
        if (mCurrentQuestionIndex < questions.size()) {
            Long timeToPause = mCurrentQuestionIndex == 0 ? questions.get(mCurrentQuestionIndex).getTimeToPause()
                    : questions.get(mCurrentQuestionIndex).getTimeToPause() - questions.get(mCurrentQuestionIndex - 1).getTimeToPause();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    player.pause();
                }
            }, timeToPause
            );
            new QuizCheckerRunnable(player);
        }
        hideQuestions();
    }

    public void onPaused() {
        showQuestions();
    }

    public void onStopped() {
    }

    public void onBuffering(boolean b) {
    }

    public void onSeekTo(int i) {
    }

    private void showQuestions() {
        LinearLayout questionsLayout = (LinearLayout) findViewById(R.id.quiz_layout);
        questionsLayout.removeAllViews();

        // Don't go past the last question.
        if (mCurrentQuestionIndex >= quiz.getQuestions().size()) {
            return;
        }

        TextView questionTextView = new TextView(this);
        mCurrentQuestion = quiz.getQuestions().get(mCurrentQuestionIndex);
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
                QuizActivity.this.onSubmitButtonClicked();
            }
        });
    }

    public void onSubmitButtonClicked() {
        int answerIndex = mAnswersRadioGroup.getCheckedRadioButtonId() - 1;
        mCurrentQuestion.setUserAnswer(answerIndex);
        mCurrentQuestionIndex++;
        player.play();
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