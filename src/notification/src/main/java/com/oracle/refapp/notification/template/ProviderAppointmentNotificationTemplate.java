/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.notification.template;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProviderAppointmentNotificationTemplate extends AbstractNotificationTemplate {

  public String getMessage(String patientName, String doctorName, String status, String startTime, String endTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("Hello Dr. ").append(doctorName).append(",");
    sb.append("<br>").append("<br>");
    sb.append("Here is your appointment detail with patient ").append(patientName).append(";");
    sb.append("<br>").append("<br>");
    Map<String, String> cellMap = new LinkedHashMap<>();
    cellMap.put("Date", getDate(startTime));
    cellMap.put("Slot", getTime(startTime) + " - " + getTime(endTime));
    cellMap.put("Status", status);
    sb.append(getTabularRepresentation(cellMap));
    sb.append(getFooter());
    return sb.toString();
  }
}
