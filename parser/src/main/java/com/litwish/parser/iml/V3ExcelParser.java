package com.litwish.parser.iml;

import com.litwish.connect.ConnectUtils;
import com.litwish.parser.ExcelAbstractParser;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:19
 * @Authror: Xiaoming Zhang
 */
public class V3ExcelParser extends ExcelAbstractParser {

    private static final String tableName = "test_cycle_stat_v3";

    public V3ExcelParser(String firstRelativeFilePath, String thirdRelativeFilePath) {
        super(firstRelativeFilePath, thirdRelativeFilePath);
    }

    @Override
    public void process() throws Exception {
        logger.info("路径:{}", relativeFilePath);
        File dirFile = new File(relativeFilePath);
        try (Connection connection = ConnectUtils.getConnection()) {
            intHead(connection);
            File[] files = dirFile.listFiles();
            if (files.length == 0) {
                logger.warn("路径{}未获取到文件", relativeFilePath);
            }
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String absolutePath = relativeFilePath + file.getName();
                logger.info("获取到文件:{}", absolutePath);
                FileInputStream in = new FileInputStream(file);
                try (Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(1024)
                        .open(in);) {
                    boolean check = check(workbook, absolutePath);
                    if (!check) {
                        logger.error("校验失败{},中止", absolutePath);
                        return;
                    }
                    logger.info("校验成功{},数据库与excel列对应关系:{}", absolutePath, columnIndexMapping.toString());
                    logger.info("文件开始解析...{}", absolutePath);
                    parse(connection,absolutePath,tableName);
                }
            }
        }
    }
}
