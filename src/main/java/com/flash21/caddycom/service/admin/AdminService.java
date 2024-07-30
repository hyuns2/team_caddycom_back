package com.flash21.caddycom.service.admin;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.entity.golfField.ApprovalStatus;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

/**
 * 전체 시스템 관리자가 골프장의 등록 요청을 승인, 거절
 *
 * @see GolfFieldRepository : 골프장 조회, 상태 변경을 위한 repository
 * @author kwonssshyeon
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    private final GolfFieldRepository golfFieldRepository;

    /**
     * 전체 시스템 관리자가 골프장 등록을 승인
     * @param id 골프장 id, null이 들어갈 수 없다.
     */
    @Transactional
    public void approveRegistration(Long id){
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
        if (golfField.getStatus()!= ApprovalStatus.WAITING){
            throw new IllegalStateException("이미 처리된 골프장입니다.");
        }

        golfField.approve();
    }


    /**
     * 전체 시스템 관리자가 골프장 등록을 거절
     * @param id 골프장 id, null이 들어갈 수 없다.
     */
    @Transactional
    public void rejectRegistration(Long id){
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
        if (golfField.getStatus()!= ApprovalStatus.WAITING){
            throw new IllegalStateException("이미 처리된 골프장입니다.");
        }

        golfField.reject();
    }


    public void uploadCaddy(Long id, MultipartFile file)  {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));


        // 엑셀 파일 파싱 후 하우스 캐디 등록
        List<HouseCaddy> caddyList = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream(); // 파일 읽기
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream); // 엑셀 파일 파싱

            XSSFSheet sheet = workbook.getSheetAt(0); // 엑셀 파일의 첫번째 시트
            int rows = sheet.getPhysicalNumberOfRows(); // 엑셀 파일의 행 수
            int cols = sheet.getRow(0).getPhysicalNumberOfCells(); // 엑셀 파일의 열 수

            System.out.println("rows : " + rows);
            System.out.println("cols : " + cols);

            for (int i = 1; i < rows-2; i++) {
                List<String> data = new ArrayList<>();
                XSSFRow row = sheet.getRow(i); // 엑셀 파일의 i번째 행
                String value = "";
                if (row == null) {
                    System.out.println("row is null");
                    break;
                }

                for (int j = 0; j < cols; j++) {
                    Cell cell = row.getCell(j); // 엑셀 파일의 i번째 행의 j번째 열의 값
                    if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
                        continue;
                    }
                    else { // 타입별로 내용 읽기
                        value = switch (cell.getCellType()) {
                            case FORMULA -> cell.getCellFormula();
                            case NUMERIC -> cell.getNumericCellValue() + "";
                            case STRING -> cell.getStringCellValue();
                            case BLANK -> cell.getBooleanCellValue() + "";
                            case ERROR -> cell.getErrorCellValue() + "";
                            default -> value;
                        };
                        data.add(value);
                    }
                }
                caddyList.add(getCaddy(data).attachGolfField(golfField));
            }
        } catch (IOException e) {
            throw new IllegalStateException("파일을 읽을 수 없습니다.");
        } catch (Throwable e) {
            throw new IllegalStateException("파일 형식이 잘못되었습니다.");
        }
    }


    private HouseCaddy getCaddy(List<String> tableList) {
        String type = tableList.get(0);
        String name = tableList.get(1);
        String phoneNumber = tableList.get(2);
        String team = tableList.get(3);
        TeamRole teamRole = convertTeamRole(tableList.get(4));
        Gender gender = convertGender(tableList.get(5));
        List<Days> holiday = convertHoliday(tableList.get(6));
        LocalDate birth = LocalDate.parse(tableList.get(7));
        String career = tableList.get(8);
        String address = tableList.get(9);
        String addressDetail = tableList.get(10);
        String offPart = tableList.get(11);



        return HouseCaddy.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .team(team)
                .teamRole(teamRole)
                .holiday(holiday)
                .birth(birth)
                .career(career)
                .address(address)
                .addressDetail(addressDetail)
                .offPart(offPart)
                .build();
    }

    private Gender convertGender(String gender) {
        if (gender.equals("남")) {
            return Gender.MALE;
        }
        else return Gender.FEMALE;
    }

    private TeamRole convertTeamRole(String teamRole) {
        if (teamRole.equals("조장")) {
            return TeamRole.LEADER;
        }
        else return TeamRole.MEMBER;
    }

    private List<Days> convertHoliday(String holidayString) {
        List<Days> days = new ArrayList<>();
        for (int i = 0; i < holidayString.length(); i++) {
            String dayKorean = holidayString.substring(i, i + 1);
            Days day = switch (dayKorean) {
                case "월" -> Days.MON;
                case "화" -> Days.TUE;
                case "수" -> Days.WED;
                case "목" -> Days.THU;
                case "금" -> Days.FRI;
                case "토" -> Days.SAT;
                case "일" -> Days.SUN;
                default -> throw new IllegalStateException("요일 값이 잘못되었습니다. " + dayKorean);
            };
            days.add(day);
        }
        return days;
    }
}
