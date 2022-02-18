package com.litwish.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:34
 * @Authror: Xiaoming Zhang
 */
public abstract class ExcelAbstractParser extends AbstractParser {

    protected String relativeFilePath;
    private String secondRelativeFilePath = "excel";
    private Sheet dataSheet;

    public ExcelAbstractParser(String firstRelativeFilePath, String thirdRelativeFilePath) {
        this.relativeFilePath = getPath(firstRelativeFilePath, secondRelativeFilePath, thirdRelativeFilePath);
    }

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
                        columnIndexMapping.put(columnIndex, dbColumn);
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

    public void parse(Connection connection, String absolutePath,String tableName) throws Exception {
        ArrayList<ArrayList<String>> datas = getDatas();
        connection.setAutoCommit(false);
        String prepareSQL = getPrepareSQL(tableName+"_tmp");
        try(PreparedStatement ps = connection.prepareStatement(prepareSQL);) {
            for (ArrayList<String> data : datas) {
                oncePs(data,ps);
            }
            ps.executeBatch();
            connection.commit();
        }
    }

    public ArrayList<ArrayList<String>> getDatas(){
        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        for (Row row : dataSheet) {
            ArrayList<String> list = new ArrayList<>();
            for (Integer index : columnIndexMapping.keySet()) {
                Cell cell = row.getCell(index);
                String value= cell == null ? "":cell.getStringCellValue().trim();
                list.add(value);
            }
            dataList.add(list);
        }
        return dataList;
    }




}
