package com.litwish;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * @Description: TODO
 * @Date: 2022/2/18 8:46
 * @Authror: Xiaoming Zhang
 */
public class CSVTest {
    public static void main(String[] args) throws Exception {
        String defaultPath = "C:\\Users\\七年\\Desktop\\新建文件夹\\M2\\点数据";
        String fileDir = System.getProperty("file.dir");
        String path = fileDir==null?defaultPath:fileDir;
        File dir = new File(path);
        System.out.println(path);
        File[] files = dir.listFiles();
        for (File file : files) {
            //System.out.println(file.getName());
            final CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();
            try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(file),"gbk")).withCSVParser(parser).build();) {
                Iterator<String[]> iterator = csvReader.iterator();
                int i =0;
                while (iterator.hasNext()) {
                    i++;
                    String[] cols = iterator.next();
                    for (String col : cols) {
                        System.out.print(col+ " ");
                    }
                    System.out.print("\n");
                    break;
                }
            }
        }
    }
}
