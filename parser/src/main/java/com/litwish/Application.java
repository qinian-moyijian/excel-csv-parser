package com.litwish;

import com.litwish.connect.ConnectUtils;

/**
 * @Description: TODO
 * @Date: 2022/2/18 11:28
 * @Authror: Xiaoming Zhang
 */
public class Application {
    private static final String V3_PATH = "V3";
    private static final String M2_PATH = "M2";

    public static void main(String[] args) throws Exception {
        String firstRelativeFilePath = "C:/Users/七年/Desktop/新建文件夹 (2)";
//        V3ExcelParser v3ExcelParser = new V3ExcelParser(firstRelativeFilePath,V3_PATH);
//        M2ExcelParser m2ExcelParser = new M2ExcelParser(firstRelativeFilePath,M2_PATH);
//        V3CSVParser v3CSVParser = new V3CSVParser(firstRelativeFilePath,V3_PATH);
//        M2CSVParser m2CSVParser = new M2CSVParser(firstRelativeFilePath,M2_PATH);
//        ArrayList<Parser> tasks = new ArrayList<>();
//        tasks.add(v3ExcelParser);
//        tasks.add(m2ExcelParser);
//        tasks.add(v3CSVParser);
//        tasks.add(m2CSVParser);
//        Submitter.submit(tasks);
        ConnectUtils.getConnection();
    }
}
