/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

package com.oracle.refapp.api.test.model;

public enum ServiceType {
    ENCOUNTER("encounters"),
    PATIENT("patients"),
    PROVIDER("providers"),
    APPOINTMENT("appointments");

    private final String name;
    private ServiceType(String name) {
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
}
