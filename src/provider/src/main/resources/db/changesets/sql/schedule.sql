/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
DECLARE
    current_provider_id NUMBER := 1;
    current_slot_start_time TIMESTAMP;
    current_slot_end_time TIMESTAMP;
    slot_end_time TIMESTAMP ;
BEGIN
    while current_provider_id <= 9 LOOP
        current_slot_start_time := TO_TIMESTAMP(TO_CHAR(SYSDATE, 'YYYY-MM-DD'), 'YYYY-MM-DD');
        slot_end_time := current_slot_start_time + interval '10' day;
        insert into schedule(provider_id, start_time, end_time) values (current_provider_id, current_slot_start_time, slot_end_time);
        while current_slot_start_time < slot_end_time LOOP
            current_slot_end_time := current_slot_start_time + interval '30' minute;
            insert into slot(provider_id, start_time, end_time, status) values (current_provider_id, current_slot_start_time, current_slot_end_time, 'AVAILABLE');
            current_slot_start_time := current_slot_end_time;
        END LOOP;
        current_provider_id := current_provider_id + 1;
    END LOOP;
END;