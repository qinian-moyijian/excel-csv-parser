package com.litwish.parser.iml;

import com.litwish.parser.CSVAbstractParser;

import java.util.HashMap;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:59
 * @Authror: Xiaoming Zhang
 */
public class M2CSVParser extends CSVAbstractParser {

    private String thirdRelativeFilePath = "M2";

    private HashMap<String, String> mapping;
    private HashMap<String, Integer> columnIndexMapping;


    @Override
    public void process() throws Exception {

    }
}
