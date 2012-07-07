// Copyright 2012 Google Inc. All Rights Reserved.

package com.examples.youtubeapidemo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * A view which flips from one view to another view using a 3D flip animation.
 */
public class FlippingView extends FrameLayout {

  private final View flipOutView;
  private final View flipInView;
  private final AnimatorSet animations;
  private final Listener listener;

  /**
   * Defines an interface to enable listening to flip events.
   */
  public interface Listener {

    /**
     * Called when the FlippingView has completed a flip.
     *
     * @param view The FlippingView which has completed the flip.
     */
    void onFlipped(FlippingView view);

  }

  /**
   * Create a flipping view which performs a 3D flip animation from one view to another.
   *
   * @param context The context associated with this View.
   * @param flipInView The view to flip in when {@link #flip()} is called.
   * @param flipOutView The view to flip out when {@link #flip()} is called.
   */
  public FlippingView(Context context, Listener listener, int width, int height,
      View flipInView, View flipOutView) {
    super(context);

    this.listener = listener;
    this.flipOutView = flipOutView;
    this.flipInView = flipInView;

    addView(flipOutView, width, height);
    addView(flipInView, width, height);

    flipInView.setRotationY(-90);

    ObjectAnimator flipOutAnimator = ObjectAnimator.ofFloat(flipOutView, "rotationY", 0, 90);
    flipOutAnimator.setInterpolator(new AccelerateInterpolator());
    Animator flipInAnimator = ObjectAnimator.ofFloat(flipInView, "rotationY", -90, 0);
    flipInAnimator.setInterpolator(new DecelerateInterpolator());
    animations = new AnimatorSet();
    animations.playSequentially(flipOutAnimator, flipInAnimator);
    animations.addListener(new AnimationListener());
  }

  public void setFlipDuration(int flipDuration) {
    animations.setDuration(flipDuration);
  }

  public void flip() {
    animations.start();
  }

  /**
   * Listens to the end of the flip animation to signal to listeners that the flip is complete
   */
  public final class AnimationListener extends AnimatorListenerAdapter {

    @Override
    public void onAnimationEnd(Animator animation) {
      flipOutView.setRotationY(0);
      flipInView.setRotationY(-90);
      listener.onFlipped(FlippingView.this);
    }
  }

}
