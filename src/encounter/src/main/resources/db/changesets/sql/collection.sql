/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
DECLARE
collection SODA_COLLECTION_T;
BEGIN
    collection := DBMS_SODA.open_collection('ENCOUNTERS');
    IF collection IS NULL THEN
        collection := DBMS_SODA.create_collection('ENCOUNTERS');
    END IF;
END;
/
