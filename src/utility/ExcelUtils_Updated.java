package utility;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import executionEngine.DriverScript;

public class ExcelUtils_Updated {

	
	String workBookPath ;
	String workSheet;
	private  XSSFSheet ExcelWSheet;
	private  XSSFWorkbook ExcelWBook;
	private  org.apache.poi.ss.usermodel.Cell Cell;
	private  XSSFRow Row;
	private  String PropertyValue;
	
	
		
	public  void setExcelFile(String Path) throws Exception {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
		} catch (Exception e) {
			Log.error("Class ExcelUtils_Updated | Method setExcelFile | Exception desc : " + e.getMessage());
			DriverScript.bResult = false;
		}
		
	}
	
	@SuppressWarnings("static-access")
	public  String getPropertyValue(String workBookPath,String sheetName,String propertyName) 
	{
		FileInputStream ExcelFile;
		try {
			
			//declare Variables and object
			ExcelFile = new FileInputStream(workBookPath);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
			ExcelWSheet = ExcelWBook.getSheet(sheetName);
			
			//boolean flag = false;
			
			int firstRow =ExcelWSheet.getFirstRowNum();
			int lastRow=ExcelWSheet.getPhysicalNumberOfRows();
			
			//get cell position
			
			for (int rowNumber = firstRow;rowNumber<=lastRow; rowNumber++ )
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
						if (propertyName.equals(currentCellValue) )
							
						{
							//get Value of next column (i.e Configuration Value)
							Cell = Row.getCell(cellNumber+1);
							Cell.setCellType(Cell.CELL_TYPE_STRING);
							PropertyValue = Cell.getStringCellValue().toString();
							//flag = true;
							rowNumber = lastRow;
							break;
						}
					}
					
					
			}
			
			
			
				} catch ( IOException e) {
					// TODO Auto-generated catch block
					Log.error("Class ExcelUtils_Updated | Method getPropertyValue | Exception desc : " + e.getMessage());
					
				}
		
		//return Value
		return PropertyValue;
				
		
	}

	
	@SuppressWarnings("static-access")
	public int[] getCellNumber(String SheetName, String CellText)
	{
		int cellPosition[] = new int [2];
		
		try {
			ExcelWSheet= ExcelWBook.getSheet(SheetName);
			//get first and last row
			int firstRow =ExcelWSheet.getFirstRowNum();
			int lastRow =ExcelWSheet.getLastRowNum();
			//Get cell position
			for (int rowNumber  =firstRow;rowNumber <=lastRow;rowNumber ++)
			{
				Row = ExcelWSheet.getRow(rowNumber);
				//get first cell and last cell
				int firstCell=Row.getFirstCellNum();
				int lastCell=Row.getLastCellNum();
				for (int cellNumber =firstCell ;cellNumber <lastCell;cellNumber++)
					
				{
					Cell = Row.getCell(cellNumber);
					//set cell to string 
					Cell.setCellType(Cell.CELL_TYPE_STRING);
					String currentValue = Cell.getStringCellValue();
					
					
					if ((currentValue).equals(CellText))
					{
						//get row no.  in array
						cellPosition[0] = rowNumber;
						//get cell no. in array
						cellPosition[1]=cellNumber;
						//set row to last row as value is found
						rowNumber =lastRow;
						break;
						
					}
					
				}
				
			}
			
		}
		catch (Exception e)
		{
				
				Log.error("Class ExcelUtils_Updated | Method getCellNumber | Error Description--" + e.getMessage() );
				
		}
		
		//if text not found
		if (cellPosition[0] ==0 & cellPosition[1]==0 )
		{
			Log.error("Class ExcelUtils_Updated | Method getCellNumber | Unable to find the specified text : " + CellText+" in current worksheet"  );
		}
		else
		{

			Log.info("Class ExcelUtils_Updated | Method getCellNumber | The specified text is present at cell=("  +cellPosition[0]+ "," +cellPosition[1]+")" );
		}
		
		
		return cellPosition; 
		
		
	}
		
		@SuppressWarnings("static-access")
		public  String getCellData(int RowNum, int ColNum, String SheetName)
	{
		try {
			String CellData = "";
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			Cell =ExcelWSheet.getRow(RowNum).getCell(ColNum);
			Cell.setCellType(Cell.CELL_TYPE_STRING);
			 CellData = Cell.getStringCellValue().toString(); 
			return CellData;
		} catch (Exception e) {
			
			Log.error("Package utility | Class ExcelUtils_Updated | Method getCellData | Exception desc : " + e.getMessage());
			return "";
		}
		
		
		
	}
	
	

}
