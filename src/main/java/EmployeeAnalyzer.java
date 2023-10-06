import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmployeeAnalyzer {

    public static void main(String[] args) {
        	
        	 try {
        		 FileInputStream file = new FileInputStream(new File("C:\\Users\\SACHIN BHOSALE\\Downloads\\Assignment_Timecard.xlsx"));
        		 Workbook workbook = new XSSFWorkbook(file);

                 Sheet sheet = workbook.getSheetAt(0);
                 Iterator<Row> rowIterator = sheet.iterator();
            
                 // Skip the header row
                 if (rowIterator.hasNext()) {
                     rowIterator.next();
                 }
                 Map<String, String> employeeDetails = new HashMap<>(); // To store Employee Position ID
                 Map<String, List<Date>> employeeShifts = new HashMap<>();// To store Employee Name
                 SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                 Calendar calendar = Calendar.getInstance();
                 List<Date> shifts1 = new ArrayList<>();
                 while (rowIterator.hasNext()) {
                     Row row = rowIterator.next();
                     String employeeName = row.getCell(7).getStringCellValue();
                     String employeePosition = row.getCell(0).getStringCellValue(); // Assuming Employee Position is in column 0
                     
                     Cell timeInCell = row.getCell(2);
                     Cell timeOutCell = row.getCell(3);

                     Date timeIn = null;
                     Date timeOut = null;
                     DataFormatter dataFormatter = new DataFormatter();
                     // Check if both cells contain non-empty string values
                     if (timeInCell.getCellType() == CellType.NUMERIC && timeOutCell.getCellType() == CellType.NUMERIC) {
                         String timeInStr = dataFormatter.formatCellValue(timeInCell).trim();
                         String timeOutStr = dataFormatter.formatCellValue(timeInCell).trim();
                         if (!timeInStr.isEmpty() && !timeOutStr.isEmpty()) {
                             try {
                                 timeIn = dateFormat.parse(timeInStr);
                                 timeOut = dateFormat.parse(timeOutStr);
                             } catch (ParseException e) {
                                 e.printStackTrace();
                             }
                         }
                     }
                    
                     if (timeIn != null && timeOut != null) {
                         if (!employeeShifts.containsKey(employeeName)) {
                             employeeShifts.put(employeeName, new ArrayList<Date>());
                             employeeDetails.put(employeeName, employeePosition); // Store Employee Position
                         }
                         shifts1 = employeeShifts.get(employeeName);
                         shifts1.add(timeIn);
                         shifts1.add(timeOut);
                     }
                 }
   
            for (Map.Entry<String, List<Date>> entry : employeeShifts.entrySet()) {
                String employeeName = entry.getKey();
                List<Date> shifts2 = entry.getValue();
                Collections.sort(shifts2);

                boolean worked7ConsecutiveDays = false;
                boolean lessThan10HoursBetweenShifts = false;
                boolean workedMoreThan14HoursInShift = false;

                for (int i = 0; i < shifts2.size() - 1; i += 2) {
                    if (i + 2 < shifts2.size()) {
                        Date shift1End = shifts2.get(i + 1);
                        Date shift2Start = shifts2.get(i + 2);

                        calendar.setTime(shift1End);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        Date nextDay = calendar.getTime();

                        long hoursBetween = (shift2Start.getTime() - shift1End.getTime()) / (60 * 60 * 1000);

                        if (nextDay.equals(shift2Start)) {
                            // Check for 7 consecutive days
                            worked7ConsecutiveDays = true;
                        }

                        if (hoursBetween < 10 && hoursBetween > 1) {
                            // Check for less than 10 hours between shifts but greater than 1 hour
                            lessThan10HoursBetweenShifts = true;
                        }

                        if (hoursBetween > 14) {
                            // Check for more than 14 hours in a single shift
                            workedMoreThan14HoursInShift = true;
                        }
                    }
                }

                
                if (worked7ConsecutiveDays) {
                	 System.out.printf("%-20s\t%-10s%n", employeeName, employeeDetails.get(employeeName)+ "  worked for 7 consecutive days.");
                	 System.out.println();
                }

                if (lessThan10HoursBetweenShifts) {
                	// System.out.printf("%-20s\t%-10s%n", employeeName, employeeDetails.get(employeeName));
                	 System.out.printf("%-20s\t%-10s%n", employeeName, employeeDetails.get(employeeName)+ "  has less than 10 hours between shifts but greater than 1 hour.");
                	 System.out.println();
                }

                if (workedMoreThan14HoursInShift) {
                    System.out.printf("%-20s\t%-10s%n", employeeName, employeeDetails.get(employeeName)+"  worked for more than 14 hours in a single shift.");
               	  System.out.println();
                	
                }
            }
         
            workbook.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}