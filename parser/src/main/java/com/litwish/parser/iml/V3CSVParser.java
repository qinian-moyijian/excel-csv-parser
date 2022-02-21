package com.litwish.parser.iml;

import com.litwish.parser.CSVAbstractParser;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:59
 * @Authror: Xiaoming Zhang
 */
public class V3CSVParser extends CSVAbstractParser {

    /**
     * 此文件解析对应的数据库名称
     */
    private static final String TABLE_NAME = "test_detail_data_v3";

    public V3CSVParser(String firstRelativeFilePath, String thirdRelativeFilePath) {
        super(firstRelativeFilePath, thirdRelativeFilePath, TABLE_NAME);
    }


}

