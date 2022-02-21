package com.litwish.parser;

import com.litwish.connect.ConnectUtils;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:57
 * @Authror: Xiaoming Zhang
 */
public abstract class CSVAbstractParser extends AbstractParser {

    /**
     * 文件的相对路径
     */
    protected String relativeFilePath;

    /**
     * 表名
     */
    private String tableName;

    private String secondRelativeFilePath = "csv";

    protected CSVAbstractParser(String firstRelativeFilePath, String thirdRelativeFilePath, String tableName) {
        this.relativeFilePath = getPath(firstRelativeFilePath, secondRelativeFilePath, thirdRelativeFilePath);
        this.tableName = tableName;
    }

    @Override
    public void process() throws Exception {
        try (Connection connection = ConnectUtils.getConnection();) {
            intHead(connection);
            if (!check()) {
                logger.error("路径{}文件校验失败", relativeFilePath);
                return;
            }
            logger.info("路径{}文件校验成功", relativeFilePath);
            File dirFile = new File(relativeFilePath);
            File[] files = dirFile.listFiles();
            parse(connection,files);
        }
    }



    public void parse(Connection connection,File... files) throws Exception {
        CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();
        String prepareSQL = getPrepareSQL(tableName + TEMP_TABLE_SUFFIX);
        connection.setAutoCommit(false);
        try (PreparedStatement ps = connection.prepareStatement(prepareSQL)) {
            for (File file : files) {
                String absolutePath = relativeFilePath + file.getName();
                logger.info("开始解析文件进入临时表{}", absolutePath);
                ArrayList<ArrayList<String>> datas = new ArrayList<ArrayList<String>>();
                try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(file), "gbk")).withCSVParser(parser).build();) {
                    Iterator<String[]> iterator = csvReader.iterator();
                    Integer rowNum = 0;
                    while (iterator.hasNext()) {
                        String[] row = iterator.next();
                        if (rowNum++ == 0 && headMapping.containsKey(row[0])) {
                            continue;
                        }
                        ArrayList<String> oneData = new ArrayList<>();
                        for (int i = 0; i < row.length; i++) {
                            String cellValue = row[i].trim();
                            oneData.add(cellValue);
                        }
                        datas.add(oneData);
                        if (datas.size() == 500) {
                            for (int i = 0; i < datas.size(); i++) {
                                oncePs(datas.get(i), ps);
                            }
                            ps.executeBatch();
                            connection.commit();
                            datas.clear();
                        }
                    }
                }
                for (int i = 0; i < datas.size(); i++) {
                    oncePs(datas.get(i), ps);
                }
                ps.executeBatch();
                connection.commit();
            }
        }
    }

    public boolean check() throws Exception {
        logger.info("路径:{}", relativeFilePath);
        File dirFile = new File(relativeFilePath);
        File[] files = dirFile.listFiles();
        if (files.length == 0) {
            logger.warn("路径{}无文件..", relativeFilePath);
            return false;
        }
        for (File file : files) {
            String absolutePath = relativeFilePath + file.getName();
            logger.info("获取到文件:{}", absolutePath);
            CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();
            try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(file), "gbk")).withCSVParser(parser).build();) {
                Iterator<String[]> iterator = csvReader.iterator();
                while (iterator.hasNext()) {
                    String[] cols = iterator.next();
                    boolean miniCheck = miniCheck(cols);
                    if (miniCheck) {
                        return true;
                    }
                    break;
                }
            }

        }
        return false;
    }

    public boolean miniCheck(String[] columns) {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            String dbColumn = headMapping.get(column);
            if (StringUtils.isBlank(dbColumn)) {
                return false;
            }
            map.put(dbColumn, i);
        }
        if (map.size() != headMapping.size()) {
            return false;
        }
        columnIndexMapping = map;
        return true;
    }




}
