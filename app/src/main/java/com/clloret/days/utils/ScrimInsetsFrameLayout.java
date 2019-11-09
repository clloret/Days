/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clloret.days.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.clloret.days.R;
import com.google.android.material.internal.ThemeEnforcement;

/**
 * A layout that draws something in the insets passed to {@link #fitSystemWindows(Rect)}, i.e. the
 * area above UI chrome
 * (status and navigation bars, overlay action bars).
 */
public class ScrimInsetsFrameLayout extends FrameLayout {

  Drawable insetForeground;

  Rect insets;

  private Rect tempRect = new Rect();

  public ScrimInsetsFrameLayout(Context context) {

    this(context, null);
  }

  public ScrimInsetsFrameLayout(Context context, AttributeSet attrs) {

    this(context, attrs, 0);
  }

  @SuppressLint({"PrivateResource", "RestrictedApi"})
  public ScrimInsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);

    final TypedArray a =
        ThemeEnforcement.obtainStyledAttributes(
            context,
            attrs,
            R.styleable.ScrimInsetsFrameLayout,
            defStyleAttr,
            R.style.Widget_Design_ScrimInsetsFrameLayout);
    insetForeground = a.getDrawable(R.styleable.ScrimInsetsFrameLayout_insetForeground);
    a.recycle();
    setWillNotDraw(true); // No need to draw until the insets are adjusted

    ViewCompat.setOnApplyWindowInsetsListener(
        this,
        (v, insets) -> {
          if (null == this.insets) {
            this.insets = new Rect();
          }
          this.insets.set(
              insets.getSystemWindowInsetLeft(),
              insets.getSystemWindowInsetTop(),
              insets.getSystemWindowInsetRight(),
              insets.getSystemWindowInsetBottom());
          onInsetsChanged(insets);
          setWillNotDraw(!insets.hasSystemWindowInsets() || insetForeground == null);
          ViewCompat.postInvalidateOnAnimation(this);
          return insets.consumeSystemWindowInsets();
        });
  }

  @Override
  public void draw(@NonNull Canvas canvas) {

    super.draw(canvas);

    int width = getWidth();
    int height = getHeight();
    if (insets != null && insetForeground != null) {
      int sc = canvas.save();
      canvas.translate(getScrollX(), getScrollY());

      // Top
      tempRect.set(0, 0, width, insets.top);
      insetForeground.setBounds(tempRect);
      insetForeground.draw(canvas);

      // Bottom
      tempRect.set(0, height - insets.bottom, width, height);
      insetForeground.setBounds(tempRect);
      insetForeground.draw(canvas);

      // Left
      tempRect.set(0, insets.top, insets.left, height - insets.bottom);
      insetForeground.setBounds(tempRect);
      insetForeground.draw(canvas);

      // Right
      tempRect.set(width - insets.right, insets.top, width, height - insets.bottom);
      insetForeground.setBounds(tempRect);
      insetForeground.draw(canvas);

      canvas.restoreToCount(sc);
    }
  }

  @Override
  protected void onAttachedToWindow() {

    super.onAttachedToWindow();
    if (insetForeground != null) {
      insetForeground.setCallback(this);
    }
  }

  @Override
  protected void onDetachedFromWindow() {

    super.onDetachedFromWindow();
    if (insetForeground != null) {
      insetForeground.setCallback(null);
    }
  }

  protected void onInsetsChanged(WindowInsetsCompat insets) {

  }
}
