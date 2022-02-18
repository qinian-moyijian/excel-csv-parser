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


    public ExcelAbstractParser(String firstRelativeFilePath, String thirdRelativeFilePath) {
        this.relativeFilePath = getPath(firstRelativeFilePath, secondRelativeFilePath, thirdRelativeFilePath);
    }

    /**
     * 校验文件格式是否正确(解析表头)
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

    /**
     * 解析具体内容
     * @param connection
     * @param absolutePath
     * @param tableName
     * @throws Exception
     */
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

    /**
     * 获取excel中的具体数据封装成list
     * @return
     */
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
