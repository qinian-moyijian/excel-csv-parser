package com.litwish;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Description: TODO
 * @Date: 2022/2/17 16:30
 * @Authror: Xiaoming Zhang
 */
public class EXCLTest {
    public static void main(String[] args) throws Exception {
      //  String path = "C:\\Users\\七年\\Desktop\\新建文件夹\\V3\\新建文件夹";
       String path = "C:\\Users\\七年\\Desktop\\新建文件夹\\M2\\循环数据";
        File dir = new File(path);
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            System.out.println();
            System.out.println(file.getName());
            FileInputStream in = new FileInputStream(file);
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)//一次读取多少行(默认是10行)
                    .bufferSize(1024)//使用的缓冲大小(默认1024)
                    .open(in);
            for (Sheet sheet : workbook) {
                String sheetName = sheet.getSheetName();
                if ("循环".equals(sheetName)){
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            System.out.print(cell.getStringCellValue()+" ");
                        }
                        System.out.print("\n");
                        break;
                    }
                }
            }
            workbook.close();
        }
    }
}
