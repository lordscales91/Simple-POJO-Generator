package util.xls;

import extra.utils.StringUtil;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.database.PojoDBGenerator;
import model.PojoObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by AChen on 6/15/2015.
 */
public class PojoXlsGenerator {

    /**
     * usage: [-i <em>the input ".xlsx" file</em>] [-o <em>output directory</em>]
     * input defaults to "sample.xlsx", output directory defaults to an empty string.
     */
    public static void main(String[] args) throws IOException {
        String inputFile = "sample.xlsx";
        String outputDir = "";

        if (args.length >= 2 && args.length % 2 == 0) {

            for (int i = 0; i < args.length; i += 2) {
                if (args[i].startsWith("-")) {
                    String cmd = args[i];
                    String param = args[i + 1];
                    if (cmd.equals("-i")) {
                        inputFile = param;
                    } else if (cmd.equals("-o")) {
                        outputDir = param;
                    }
                }
            }
        }

        File input = new File(inputFile);
        File output = new File(outputDir);
        if(!input.exists()){
            System.out.println("Input file not exists");
        }else{
            generate(input, output);
        }
    }

    /**
     * Generate the pojo source file in the output directory from the input ".xlsx" file
     * @param input input file
     * @param output output directory
     */
    public static void generate(File input, File output) throws IOException {
        FileInputStream fis = new FileInputStream(input);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        Iterator<XSSFSheet> sheetIterator = workbook.iterator();
        List<PojoObject> pojoList = new ArrayList<>();
        while(sheetIterator.hasNext()){
            PojoObject tmp = generatePojo(sheetIterator.next());
            if(tmp != null)
                pojoList.add(tmp);
        }

        PojoDBGenerator.writePojos(output, pojoList);
        System.out.println("FINISHED");
    }

    /**
     * Generate the pojo object from the XSSFSheet
     * @param sheet XSSFSheet object of ".xlsx"
     */
    public static PojoObject generatePojo(XSSFSheet sheet) throws IOException {
        PojoObject pojo = new PojoObject(StringUtil.toClassName(sheet.getSheetName()));
        XSSFRow row = sheet.getRow(0);
        XSSFRow dataRow = sheet.getRow(1);
        Iterator<Cell> cellIterator = row.iterator();
        Iterator<Cell> dataCellIterator = dataRow.iterator();
        while(cellIterator.hasNext()){
            Cell cell = cellIterator.next();
            Cell dataCell= null;
            if(dataCellIterator.hasNext()){
                dataCell = dataCellIterator.next();
            }

            String fieldName = StringUtil.toCamelCase(cell.getStringCellValue());
            String type = null;
            if(dataCell == null){
                System.out.println("First line under " + fieldName + " is empty, set type to default String");
                type = "String";
            }else{
                if(dataCell.getCellType() == Cell.CELL_TYPE_STRING){
                    type = "String";
                }else if(dataCell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
                    type = "boolean";
                }else if(dataCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                    type = "int";
                }
                System.out.println(fieldName + " : " + type);
            }

            if(type == null){
                System.out.println("fileldName: " + fieldName + "type not known");
            }else{
                pojo.addField(type, fieldName);
            }

        }
        return pojo;
    }
}
