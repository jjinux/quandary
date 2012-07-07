// Copyright 2012 Google Inc. All Rights Reserved.

package com.examples.youtubeapidemo.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A view which displays a grid of images.
 */
public class ImageWallView extends ViewGroup {

  private final Context context;
  private final Random random;

  private final int imageHeight;
  private final int imageWidth;
  private final int interImagePadding;

  private ImageView[] images;
  private List<Integer> unInitializedImages;

  private int numberOfColumns;
  private int numberOfRows;

  public ImageWallView(Context context, int imageWidth, int imageHeight, int interImagePadding) {
    super(context);
    this.context = context;
    random = new Random();

    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.interImagePadding = interImagePadding;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    // create enough columns to fill view's width, plus an extra column at either side to allow
    // images to have diagonal offset across the screen.
    numberOfColumns = w / (imageWidth + interImagePadding) + 2;
    // create enough rows to fill the view's height (adding an extra row at bottom if necessary).
    numberOfRows = h / (imageHeight + interImagePadding);
    numberOfRows += (h % (imageHeight + interImagePadding) == 0) ? 0 : 1;

    images = new ImageView[numberOfColumns * numberOfRows];
    unInitializedImages = new ArrayList<Integer>(numberOfColumns * numberOfRows);

    for (int col = 0; col < numberOfColumns; col++) {
      for (int row = 0; row < numberOfRows; row++) {
        int elementIdx = getElementIdx(col, row);
        ImageView thumbnail = new ImageView(context);
        thumbnail.setLayoutParams(new LayoutParams(imageWidth, imageHeight));
        images[elementIdx] = thumbnail;
        unInitializedImages.add(elementIdx);
        addView(thumbnail);
      }
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    int width = getDefaultSize(displayMetrics.widthPixels, widthMeasureSpec);
    int height = getDefaultSize(displayMetrics.heightPixels, heightMeasureSpec);
    setMeasuredDimension(width, height);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    for (int col = 0; col < numberOfColumns; col++) {
      for (int row = 0; row < numberOfRows; row++) {
        int x = (col - 1) * (imageWidth + interImagePadding) + (row * (imageWidth / numberOfRows));
        int y = row * (imageHeight + interImagePadding);
        images[col * numberOfRows + row].layout(x, y, x + imageWidth, y + imageHeight);
      }
    }
  }

  public int getXPosition(int col, int row) {
    return images[getElementIdx(col, row)].getLeft();
  }

  public int getYPosition(int col, int row) {
    return images[getElementIdx(col, row)].getTop();
  }

  private int getElementIdx(int col, int row) {
    return (col * numberOfRows) + row;
  }

  public void hideImage(int col, int row) {
    images[getElementIdx(col, row)].setVisibility(View.INVISIBLE);
  }

  public void showImage(int col, int row) {
    images[getElementIdx(col, row)].setVisibility(View.VISIBLE);
  }

  public void setImageDrawable(int col, int row, Drawable drawable) {
    int elementIdx = getElementIdx(col, row);
    unInitializedImages.remove(new Integer(elementIdx));
    images[elementIdx].setImageDrawable(drawable);
  }

  public Drawable getImageDrawable(int col, int row) {
    int elementIdx = getElementIdx(col, row);
    return images[elementIdx].getDrawable();
  }

  public Pair<Integer, Integer> getNextLoadTarget() {
    int nextElement;
    do {
      if (unInitializedImages.isEmpty()) {
        // Don't choose the first or last columns (since they are partly hidden)
        nextElement = random.nextInt((numberOfColumns - 2) * numberOfRows) + numberOfRows;
      } else {
        nextElement = unInitializedImages.get(random.nextInt(unInitializedImages.size()));
      }
    } while (images[nextElement].getVisibility() != View.VISIBLE);

    int col = nextElement / numberOfRows;
    int row = nextElement % numberOfRows;
    return new Pair<Integer, Integer>(col, row);
  }

  public boolean allImagesLoaded() {
    return unInitializedImages.isEmpty();
  }

}
