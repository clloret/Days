package com.clloret.days.data.local.entities.converters;

import android.text.TextUtils;
import androidx.room.TypeConverter;

public class StringArrayConverter {

  private static final String[] EMPTY_ARRAY = new String[0];

  @TypeConverter
  public static String[] toStringArray(String string) {

    return TextUtils.isEmpty(string) ? EMPTY_ARRAY : string.split(",");
  }

  @TypeConverter
  public static String toString(String[] array) {

    return array == null ? null : TextUtils.join(",", array);
  }
}
