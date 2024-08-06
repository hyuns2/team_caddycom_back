package com.flash21.caddycom.global.common.fileReader;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ExcelReader {
    /**
     * 엑셀 파일을 읽어 행/열의 이차원 리스트로 반환한다.
     * @return List<List<String>>
     */
    public List<List<String>> readExcelToList(MultipartFile file)  {

        // 엑셀 파일 파싱 후 하우스 캐디 등록
        try {
            InputStream inputStream = file.getInputStream(); // 파일 읽기
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            XSSFSheet sheet = workbook.getSheetAt(0); // 엑셀 파일의 첫번째 시트
            int rows = sheet.getPhysicalNumberOfRows(); // 행 수
            int cols = sheet.getRow(0).getPhysicalNumberOfCells(); // 열 수

            List<List<String>> dataList = new ArrayList<>();


            for (int i = 1; i < rows-2; i++) {
                List<String> data = new ArrayList<>();
                XSSFRow row = sheet.getRow(i); // 엑셀 파일의 i번째 행
                String value = "";

                // row 에 빈 행이 포함된 경우 예외처리
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType().equals(CellType.BLANK)) {
                    break;
                }

                for (int j = 0; j < cols; j++) {
                    Cell cell = row.getCell(j); // i번째 행의 j번째 열의 값
                    // 공백 데이터 예외처리
                    if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
                        value = "";
                    }

                    else { // 타입별로 내용 읽기
                        value = switch (cell.getCellType()) {
                            case STRING -> cell.getStringCellValue();
                            case FORMULA -> cell.getCellFormula();
                            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
                            case BLANK -> "";
                            case ERROR -> cell.getErrorCellValue() + "";
                            default -> value;
                        };
                    }
                    data.add(value);
                }
                dataList.add(data);
            }
            return dataList;



        } catch (IOException e) {
            throw new IllegalStateException("파일을 읽을 수 없습니다.");
        } catch (Throwable e) {
            throw new IllegalStateException("파일의 일부 데이터 형식이 잘못되었습니다. : "+ e.getMessage());
        }
    }
}
