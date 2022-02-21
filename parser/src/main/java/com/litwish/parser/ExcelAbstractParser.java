package com.litwish.parser;

import com.litwish.connect.ConnectUtils;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:34
 * @Authror: Xiaoming Zhang
 */
public abstract class ExcelAbstractParser extends AbstractParser {

    /**
     * 文件的相对路径
     */
    protected String relativeFilePath;
    /**
     * excel的二级路径名
     */
    private String secondRelativeFilePath = "excel";
    /**
     * 具体需要解析的sheet
     */
    private Sheet dataSheet;

    /**
     * 表名
     */
    private String tableName;

    protected ExcelAbstractParser(String firstRelativeFilePath, String thirdRelativeFilePath, String tableName) {
        this.relativeFilePath = getPath(firstRelativeFilePath, secondRelativeFilePath, thirdRelativeFilePath);
        this.tableName = tableName;
    }

    /**
     * 校验文件格式是否正确(解析表头)
     *
     * @param workbook
     * @param absolutePath
     * @return
     * @throws Exception
     */
    public boolean check(Workbook workbook, String absolutePath) throws Exception {
        boolean hasSheet = false;
        for (Sheet sheet : workbook) {
            String sheetName = sheet.getSheetName();
            if ("循环".equals(sheetName)) {
                hasSheet = true;
                dataSheet = sheet;
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellV = cell.getStringCellValue().trim();
                        String dbColumn = headMapping.get(cellV);
                        if (StringUtils.isBlank(dbColumn)) {
                            logger.error("文件{}获取到未配置的列:{}", absolutePath, cellV);
                            return false;
                        }
                        int columnIndex = cell.getColumnIndex();
                        columnIndexMapping.put(dbColumn,columnIndex);
                    }
                    if (headMapping.size() != columnIndexMapping.size()) {
                        logger.error("文件{}中列与配置列个数不匹配;{}", absolutePath, columnIndexMapping.toString());
                        return false;
                    }
                    break;
                }
            }
        }
        if (!hasSheet) {
            logger.error("文件{}未获取到循环sheet", absolutePath);
            return false;
        }
        return true;
    }


    /**
     * 获取excel中的具体数据封装成list
     *
     * @return
     */
    public ArrayList<ArrayList<String>> getDatas() {
        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        for (Row row : dataSheet) {
            ArrayList<String> oneData = new ArrayList<String>();
            for (Integer index : columnIndexMapping.values()) {
                Cell cell = row.getCell(index);
                String value = cell == null ? "" : cell.getStringCellValue().trim();
                oneData.add(value);
            }
            dataList.add(oneData);
        }
        return dataList;
    }

    @Override
    /**
     * 具体的解析数据入库的过程
     */
    public void process() throws Exception {
        logger.info("路径:{}", relativeFilePath);
        File dirFile = new File(relativeFilePath);
        File[] files = dirFile.listFiles();
        try (Connection connection = ConnectUtils.getConnection()) {
            intHead(connection);
            for (File file : files) {
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
                    logger.info("文件开始解析进入临时表...{}", absolutePath);
                    parse(connection,file);
                    logger.info("解析文件进入临时表成功{}", absolutePath);
                }
            }
            logger.info("路径{}数据开始进入最终表{}", relativeFilePath, tableName);
            copyDataToFinalTable(connection,tableName);
            logger.info("路径{}解析成功", relativeFilePath);
        }
    }

    /**
     * 解析具体内容
     *
     * @param connection
     * @throws Exception
     */
    public void parse(Connection connection,File... files) throws Exception {
        ArrayList<ArrayList<String>> datas = getDatas();
        connection.setAutoCommit(false);
        String prepareSQL = getPrepareSQL(tableName + TEMP_TABLE_SUFFIX);
        try (PreparedStatement ps = connection.prepareStatement(prepareSQL);) {
            for (ArrayList<String> data : datas) {
                oncePs(data, ps);
            }
            ps.executeBatch();
            connection.commit();
        }
    }

}
