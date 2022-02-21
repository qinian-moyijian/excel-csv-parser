package com.litwish.parser.iml;

import com.litwish.parser.ExcelAbstractParser;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:18
 * @Authror: Xiaoming Zhang
 */
public class M2ExcelParser extends ExcelAbstractParser {

    /**
     * 此文件解析对应的数据库名称
     */
    private static final String TABLE_NAME = "test_cycle_stat_m2";

    public M2ExcelParser(String relativePath, String thirdRelativeFilePath) {
        super(relativePath, thirdRelativeFilePath,TABLE_NAME);
    }

}
