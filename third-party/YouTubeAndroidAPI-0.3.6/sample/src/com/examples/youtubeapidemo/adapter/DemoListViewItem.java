// Copyright 2012 Google Inc. All Rights Reserved.

package com.examples.youtubeapidemo.adapter;

/**
 * A single list view item for use with {@link DemoArrayAdapter}.
 */
public interface DemoListViewItem {

  public String getTitle();

  public boolean isEnabled();

  public String getDisabledText();

}
