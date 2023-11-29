package com.holub.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class XMLImporterTest {
    private static final String fileName = "people_xml_test";
    private static final String fileContents = "<peopleTable>\n" +
            "<people>\n" +
            "\t<last>Holub</last>\n" +
            "\t<first>Allen</first>\n" +
            "\t<addrId>1</addrId>\n" +
            "</people>\n" +
            "<people>\n" +
            "\t<last>Flintstone</last>\n" +
            "\t<first>Wilma</first>\n" +
            "\t<addrId>2</addrId>\n" +
            "</people>\n" +
            "<people>\n" +
            "\t<last>2</last>\n" +
            "\t<first>Fred</first>\n" +
            "\t<addrId></addrId>\n" +
            "</people>\n" +
            "</peopleTable>\n";

    private Reader in;
    private Table.Importer importer;

    private Table table;

    @BeforeEach
    void setUp() throws IOException {
        Writer out = new FileWriter(fileName);
        out.write(fileContents);
        out.close();

        in = new FileReader(fileName);
    }

    @AfterEach
    void tearDown() throws IOException {
        in.close();

        System.out.println(table.toString());
    }

    @Test
    void testImport() throws IOException {
        importer = new XMLImporter(in);

        importer.startTable();

        String tableName = importer.loadTableName();
        int width = importer.loadWidth();
        Iterator columns = importer.loadColumnNames();

        String[] columnNames = new String[width];
        for (int i = 0; columns.hasNext(); )
            columnNames[i++] = (String) columns.next();

        table = TableFactory.create(tableName, columnNames);

        while ((columns = importer.loadRow()) != null) {
            Object[] current = new Object[width];
            for (int i = 0; columns.hasNext(); )
                current[i++] = columns.next();
            table.insert(current);
        }
        importer.endTable();
    }
}