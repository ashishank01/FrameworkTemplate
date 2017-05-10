package config;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.ss.usermodel.Cell;

//import com.sun.org.apache.xpath.internal.operations.Bool;

import utility.Log;;


public  class TestConfigurations {

	private static String configurationFile = (System.getProperty("user.dir") 
			+	"\\src\\config\\ConfigurationsFile.xlsx");
	private static String configurationSheet = "Configurations";
	private static XSSFWorkbook ExcelWBook;
	private static XSSFSheet ExcelWSheet ;
	private static Cell Cell;
	private static XSSFRow Row;
	private static String PropertyValue="";

	
	//get all Configurations from Configuration File
	
	public static int WindowWait= Integer.parseInt(getPropertyValue("WindowWait"));
	public static int PageWait = Integer.parseInt(getPropertyValue("PageWait"));
	public static int ElementWait =Integer.parseInt(getPropertyValue("ElementWait"));
	public static String ReportLocation =getPropertyValue("ReportLocation");
	public static String ReportName =getPropertyValue("ReportName");
	public static String SuiteName =getPropertyValue("SuiteName");
	public static String Screenshotlocation =getPropertyValue("Screenshotlocation");
	public static String VideoLocation =getPropertyValue("VideoLocation");
	public static String ScreenshotRequired =getPropertyValue("ScreenshotRequired");
	public static String VideoRequired=getPropertyValue("VideoRequired");
	public static String Screenshotformat=getPropertyValue("Screenshotformat");
	public static String VideoFormat=getPropertyValue("VideoFormat");
	public static String Browsers=getPropertyValue("Browsers");
	public static String AppURL=getPropertyValue("AppURL");
	public static String DataEngine=getPropertyValue("DataEngine");
	public static String ObjectRepository=getPropertyValue("ObjectRepository");
	public static String ORSheetName=getPropertyValue("ORSheetName");
	public static String VideoRecording=getPropertyValue("VideoRecording");
	public static String DB=getPropertyValue("DB");
	public static String DBURL=getPropertyValue("DBURL");
	public static String DBName=getPropertyValue("DBName");
	public static String DBUserName=getPropertyValue("DBUserName");
	public static String DBPassword=getPropertyValue("DBPassword");
	
	// Data Engine Excel sheets
	public static final String Sheet_TestSteps = "Test Steps";
	public static final String Sheet_TestCases = "Test Cases";
	
	// Object Repository
	public static final String ObjectRepository_path = System.getProperty("user.dir") + 
			"\\src\\config\\" + TestConfigurations.ObjectRepository;
	
	
	//Data Engine Structure and column numbers
	
	public static final int Col_TestCaseID = 0;	
	public static final int Col_TestDescID = 1;
	public static final int Col_TestScenarioID =1 ;
	public static final int Col_TestStepDescription =2; 
	public static final int Col_RunMode =2 ;//need to identify
	public static final int Col_Result =3 ; //need to identify
	public static final int Col_PageObject =4 ;
	public static final int Col_ActionKeyword =5 ;
	public static final int Col_DataSet =6 ;
	public static final int Col_TestStepResult =7 ;

	
	//Miscellaneous Variables
	
	public static final String KEYWORD_FAIL = "FAIL";
	public static final String KEYWORD_PASS = "PASS";
	
	//Get Configurations
	
	@SuppressWarnings("static-access")
	public static  String getPropertyValue(String configurationName) 
	{
		FileInputStream ExcelFile;
		try {
			
			//declare Variables and object
			ExcelFile = new FileInputStream(configurationFile);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
			ExcelWSheet = ExcelWBook.getSheet(configurationSheet);
			
			//boolean flag = false;
			
			//get cell position
			
			for (int rowNumber = ExcelWSheet.getFirstRowNum();rowNumber<=ExcelWSheet.getLastRowNum(); rowNumber++ )
			{
					//Declare Row 
					Row = ExcelWSheet.getRow(rowNumber);
					
					//Get First And last Row
					int firstCell= Row.getFirstCellNum();
					int lastCell = Row.getLastCellNum();
					//Read next cell Value
					for (int cellNumber = firstCell;cellNumber<lastCell;cellNumber++)
					{
						Cell = Row.getCell(cellNumber);
						//set cell type to String
						Cell.setCellType(Cell.CELL_TYPE_STRING);
						String currentCellValue = Cell.getStringCellValue().toString(); 
						//check whether value is matched
						if (configurationName.equals(currentCellValue) )
							
						{
							//get Value of next column (i.e Configuration Value)
							Cell = Row.getCell(cellNumber+1);
							Cell.setCellType(Cell.CELL_TYPE_STRING);
							PropertyValue = Cell.getStringCellValue().toString();
							//flag = true;
							break;
						}
					}
					
					
			}
			
			
			
				} catch ( IOException e) {
					// TODO Auto-generated catch block
					Log.error("Class Configurations | Method getPropertyValue | Exception desc : " + e.getMessage());
					
				}
		
		//return Value
		return PropertyValue;
				
		
	}

	

}


