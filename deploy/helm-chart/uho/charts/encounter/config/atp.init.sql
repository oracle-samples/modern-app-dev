/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

CREATE PROFILE ENCOUNTER_SVC_PROFILE
    LIMIT
        FAILED_LOGIN_ATTEMPTS 10
        PASSWORD_REUSE_MAX 4
        PASSWORD_VERIFY_FUNCTION CLOUD_VERIFY_FUNCTION
        PASSWORD_REUSE_TIME 1
        PASSWORD_LOCK_TIME 1
        COMPOSITE_LIMIT UNLIMITED
        SESSIONS_PER_USER UNLIMITED
        CPU_PER_SESSION UNLIMITED
        CPU_PER_CALL UNLIMITED
        LOGICAL_READS_PER_SESSION UNLIMITED
        LOGICAL_READS_PER_CALL UNLIMITED
        IDLE_TIME UNLIMITED
        CONNECT_TIME UNLIMITED
        PRIVATE_SGA UNLIMITED
        PASSWORD_LIFE_TIME 360
        PASSWORD_GRACE_TIME 30
        INACTIVE_ACCOUNT_TIME 180
        PASSWORD_ROLLOVER_TIME 0;
create user &1 identified by "&2" profile ENCOUNTER_SVC_PROFILE;
grant create session to &1;
grant create table to &1;
grant unlimited tablespace to &1;
grant create view, create procedure, create sequence to &1;