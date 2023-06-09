/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.notification.template;

public class EncounterNotificationTemplate extends AbstractNotificationTemplate {

  public String getMessage(String patientName) {
    StringBuilder sb = new StringBuilder();
    sb.append("Hello ").append(patientName).append(",");
    sb.append("<br>").append("<br>");
    sb.append("Attached with this mail is the document based on your recent encounter with your doctor.");
    sb.append(getFooter());
    return sb.toString();
  }
}
