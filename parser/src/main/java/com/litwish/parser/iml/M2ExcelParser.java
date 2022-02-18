package com.litwish.parser.iml;

import com.litwish.parser.ExcelAbstractParser;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:18
 * @Authror: Xiaoming Zhang
 */
public class M2ExcelParser extends ExcelAbstractParser {


    private String thirdRelativeFilePath = "M2";

    public M2ExcelParser(String relativePath, String thirdRelativeFilePath) {
        super(relativePath, thirdRelativeFilePath);
    }


    @Override
    public void process() throws Exception {

    }
}
