#!/usr/bin/env python3
from csv import reader

SQL_OUTPUT_FILE_PATH = '../src/main/resources/db/sql/data.sql'

# SQL Tables to be pre-populated with dummy data
CODE_TYPES = ['condition_codes','encounter_codes','observation_codes','reason_codes']

with open(SQL_OUTPUT_FILE_PATH,'w') as writer:
    writer.write("/*\n * Auto generated. Do not modify. \n */\n")
    for code_type in CODE_TYPES:
        with open(f'{code_type}.csv', 'r') as read_obj:
            csv_reader = reader(read_obj)
            header = next(csv_reader)
            for code,text in csv_reader:
                text_replaced = text.replace("'","''")
                type_value = (code_type.split("_")[0]).upper()
                writer.write(f"INSERT INTO CODE_CONSTANTS (TYPE,CODE,TEXT) VALUES ('{type_value}','{code}','{text_replaced}');\n")
            writer.write("--------------------------------------------------------------------\n")
