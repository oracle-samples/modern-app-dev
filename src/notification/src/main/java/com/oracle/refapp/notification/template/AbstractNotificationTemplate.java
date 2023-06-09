/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.notification.template;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AbstractNotificationTemplate {

  private final SimpleDateFormat INCOMING_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  private final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");
  private final SimpleDateFormat TIME_ONLY_FORMAT = new SimpleDateFormat("HH:mm");

  protected String getDate(String time) {
    try {
      Date date = INCOMING_DATE_FORMAT.parse(time);
      return DATE_ONLY_FORMAT.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return time;
  }

  protected String getTime(String time) {
    try {
      Date date = INCOMING_DATE_FORMAT.parse(time);
      return TIME_ONLY_FORMAT.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return time;
  }

  protected String getTabularRepresentation(Map<String, String> cellMap) {
    StringBuilder sb = new StringBuilder();
    sb.append("<table role='presentation' border='0' cellspacing='0' width='40%'>");
    for (Map.Entry<String, String> entry : cellMap.entrySet()) {
      sb.append("<tr>");
      sb
        .append("<td><b>")
        .append(entry.getKey())
        .append("</b></td>")
        .append("<td>")
        .append(entry.getValue())
        .append("</td>");
      sb.append("</tr>");
    }
    sb.append("</table>");
    return sb.toString();
  }

  protected String getFooter() {
    StringBuilder sb = new StringBuilder();
    sb.append("<br>").append("<br>").append("<br>");
    sb.append("Regards,");
    sb.append("<br>").append("<br>");
    sb.append("UHO");
    sb.append("<br><br><br><br>");
    sb.append("Note: This is a system generated Email. Please do not reply to this Email.");
    return sb.toString();
  }
}
