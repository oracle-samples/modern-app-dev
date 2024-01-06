/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

  private static final SimpleDateFormat INCOMING_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");
  private static final SimpleDateFormat TIME_ONLY_FORMAT = new SimpleDateFormat("HH:mm");

  public static String getDate(String time) {
    try {
      Date date = INCOMING_DATE_FORMAT.parse(time);
      return DATE_ONLY_FORMAT.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return time;
  }

  public static String getTime(String time) {
    try {
      Date date = INCOMING_DATE_FORMAT.parse(time);
      return TIME_ONLY_FORMAT.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return time;
  }
}
