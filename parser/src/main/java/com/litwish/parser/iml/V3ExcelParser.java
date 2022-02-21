package com.litwish.parser.iml;

import com.litwish.parser.ExcelAbstractParser;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:19
 * @Authror: Xiaoming Zhang
 */
public class V3ExcelParser extends ExcelAbstractParser {
    /**
     * 此文件解析对应的最终的数据库名称
     */
    private static final String TABLE_NAME = "test_cycle_stat_v3";

    public V3ExcelParser(String firstRelativeFilePath, String thirdRelativeFilePath) {
        super(firstRelativeFilePath, thirdRelativeFilePath, TABLE_NAME);
    }
}
