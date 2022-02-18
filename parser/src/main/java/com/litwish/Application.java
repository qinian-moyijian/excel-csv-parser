package com.litwish;

import com.litwish.parser.iml.V3ExcelParser;

/**
 * @Description: TODO
 * @Date: 2022/2/18 11:28
 * @Authror: Xiaoming Zhang
 */
public class Application {
    private static final String V3Path = "V3";
    private static final String M2Path = "M2";


    public static void main(String[] args) throws Exception {
//        if (ArrayUtils.isEmpty(args)){
//            throw new Exception("未获取到路径");
//        }
//        String relativePath = args[0];

        String firstRelativeFilePath = "C:/Users/七年/Desktop/新建文件夹 (2)";
        V3ExcelParser v3ExcelParser = new V3ExcelParser(firstRelativeFilePath,V3Path);
        v3ExcelParser.process();

    }
}
