import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;

public class TestPoi {
    @Test
    public void exportExcel() throws IOException {
        //创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook("D:\\BaiduNetdiskDownload\\sz1128视频代码\\3月28号资料\\资源\\预约设置模板文件\\test.xlsx");
        //获取工作表，既可以根据工作表的顺序获取，也可以根据工作表的名称获取
        XSSFSheet sheetAt = workbook.getSheetAt(0);
//        //遍历工作表获取行对象
//        for (Row row : sheetAt) {
//            //遍历行对象获取单元格对象
//            for (Cell cell : row) {
//                //获取单元格的值
//                String value = cell.getStringCellValue();
//                System.out.println(value);
//            }
//        }

        //获取当前工作表的最后一行的行号，行号从0开始
        int lastRowNum = sheetAt.getLastRowNum();
        for (int i = 0; i <= lastRowNum; i++) {
            //根据行号获取行对象
            XSSFRow row = sheetAt.getRow(i);
            //再获取单元格对象
            short lastCellNum = row.getLastCellNum();
            for (int j = 0; j < lastCellNum; j++) {
                //获取单元格对象的值
                String value = row.getCell(j).getStringCellValue();
                System.out.println(value);

            }
        }
        workbook.close();
    }
}
