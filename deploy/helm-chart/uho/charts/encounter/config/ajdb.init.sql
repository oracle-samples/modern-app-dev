/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

create user &1 identified by "&2";
grant create session to &1;
grant create table to &1;
grant unlimited tablespace to &1;
grant create view, create procedure, create sequence to &1;
grant SODA_APP to &1;