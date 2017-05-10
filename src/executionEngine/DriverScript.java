package executionEngine;


import java.lang.reflect.Method;
import java.util.Properties;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.annotations.*;
import config.Constants;
import config.TestConfigurations;
import utility.ActionKeywords;
import utility.ExcelUtils;
import utility.Log;
import utility.ReportBuilder;
import utility.Retry;
import utility.SendMail;
import utility.VideoReord;
 
@Listeners(utility.Listeners.class)
public class DriverScript {
	
	public static Properties OR;
	public static ActionKeywords actionKeywords;
	public static String sActionKeyword;
	public static String sPageObject;
	public static Method method[];
	public static int sTestStepId;
	public static int sTestLastStep;
	public static String sTestCaseID;
	public static String sRunMode;
	public static String sData;
	public static boolean bResult;
	public static String sTestDescription;
	public static String sDescription;
	public static ReportBuilder builder;
	public static int retryCount;
	public static int Max_retryCount;
	
	//record Video Object
	VideoReord  videoReord = new VideoReord();
	
	@BeforeMethod
	public void  beforeMethod() throws NoSuchMethodException, SecurityException {
		actionKeywords = new ActionKeywords();
		method = actionKeywords.getClass().getMethods();
		builder = new ReportBuilder();
	}
	//
	@Test(retryAnalyzer=Retry.class)
    public static void Test() throws Exception {
		try {
			ExcelUtils.setExcelFile(
					System.getProperty("user.dir") + "\\src\\dataEngine\\" + TestConfigurations.DataEngine);
			DOMConfigurator.configure("log4j.xml");
			/*
			String Path_OR = Constants.Path_OR;
			FileInputStream fs = new FileInputStream(Path_OR);
			OR= new Properties(System.getProperties());
			OR.load(fs);
			*/
			DriverScript startEngine = new DriverScript();
			startEngine.execute_TestCase();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
	private void execute_TestCase() throws Exception {
	    	try {
				int iTotalTestCases = ExcelUtils.getRowCount(Constants.Sheet_TestCases);
				if ((TestConfigurations.VideoRecording).equals("SuiteVideo"))
				{
					//Save video only when required
					if ((TestConfigurations.VideoRequired).equals("Yes"))
					{
					//Add recording
					try {
						videoReord.startRecording(TestConfigurations.VideoLocation,"SuiteRecording");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					}	
				}
				for (int iTestcase = 1; iTestcase < iTotalTestCases; iTestcase++) {
					bResult = true;
					sTestCaseID = ExcelUtils.getCellData(iTestcase, TestConfigurations.Col_TestCaseID, TestConfigurations.Sheet_TestCases);
					sRunMode = ExcelUtils.getCellData(iTestcase, TestConfigurations.Col_RunMode, TestConfigurations.Sheet_TestCases);
					sTestDescription = ExcelUtils.getCellData(iTestcase, TestConfigurations.Col_TestDescID,TestConfigurations.Sheet_TestCases);
					
					if (sRunMode.equals("Yes")) {
						///For Single Recording
						if ((TestConfigurations.VideoRecording).equals("TestCasesVideos"))
						{
						//Save video only when required
						if ((TestConfigurations.VideoRequired).equals("Yes"))
						{
						//Add recording
						try {
							videoReord.startRecording(TestConfigurations.VideoLocation,sTestCaseID);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						}
						}

						Log.startTestCase(sTestCaseID);
						builder.StartTestCase(sTestCaseID, sTestDescription, TestConfigurations.SuiteName);
						sTestStepId = ExcelUtils.getRowContains(sTestCaseID, TestConfigurations.Col_TestCaseID, TestConfigurations.Sheet_TestSteps);
						sTestLastStep = ExcelUtils.getTestStepsCount(TestConfigurations.Sheet_TestSteps, sTestCaseID, sTestStepId);
						
						bResult = true;
						for (; sTestStepId < sTestLastStep; sTestStepId++) {
							sActionKeyword = ExcelUtils.getCellData(sTestStepId, TestConfigurations.Col_ActionKeyword,
									TestConfigurations.Sheet_TestSteps);
							sPageObject = ExcelUtils.getCellData(sTestStepId, TestConfigurations.Col_PageObject,
									TestConfigurations.Sheet_TestSteps);
							sData = ExcelUtils.getCellData(sTestStepId, TestConfigurations.Col_DataSet, TestConfigurations.Sheet_TestSteps);
							sDescription =  ExcelUtils.getCellData(sTestStepId, TestConfigurations.Col_TestStepDescription, TestConfigurations.Sheet_TestSteps);
							//sTestStepId = ExcelUtils.getCellData(sTestStepId, TestConfigurations.Col_TestDescID, TestConfigurations.Sheet_TestSteps);
							
							execute_Actions();
							if (bResult == false) {
								ExcelUtils.setCellData(TestConfigurations.KEYWORD_FAIL, iTestcase, TestConfigurations.Col_Result,
										TestConfigurations.Sheet_TestCases);
								///For Single Recording
								if ((TestConfigurations.VideoRecording).equals("TestCasesVideos"))
								{
								
								//stop video and don't execute the code when  Video is not required
								if ((TestConfigurations.VideoRequired).equals("Yes"))
								{
								 videoReord.stopRecording();
								 builder.DisplayResultScreenRecording("Fail",  sTestCaseID);
								}
								}
								Log.endTestCase(sTestCaseID);
								builder.endTest();
								//Stop recording
								//Close browser
								ActionKeywords.closeBrowser("NA", "Na", "Closing browser as test case is failed", builder);
								break;
							}
						}
						if (bResult == true) {
							ExcelUtils.setCellData(TestConfigurations.KEYWORD_PASS, iTestcase, TestConfigurations.Col_Result,
									TestConfigurations.Sheet_TestCases);
							///For Single Recording
							if ((TestConfigurations.VideoRecording).equals("TestCasesVideos")){
							
							//stop video and don't execute the code when  Video is not required
							if ((TestConfigurations.VideoRequired).equals("Yes"))
							{
							 videoReord.stopRecording();
							 builder.DisplayResultScreenRecording("Pass",  sTestCaseID);
							}
							}
							Log.endTestCase(sTestCaseID);
							builder.endTest();
							//Stop recording
						}
					}
				} 
			} catch (Exception e) {
				Log.error("Unable to execute the TestCAse:"+sTestCaseID+ "Please see the error message--" +e.getMessage());
				builder.DisplayResult("Fail", sDescription, "Unable to execute the Test Case.Please see the error message--" +e.getMessage());
			}
			finally
			{
				System.out.println("debugFinally");
				///For Single Recording
				if ((TestConfigurations.VideoRecording).equals("SuiteVideo")){
				
				//stop video and don't execute the code when  Video is not required
				if ((TestConfigurations.VideoRequired).equals("Yes"))
				{
				 try {
					videoReord.stopRecording();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 builder.DisplayResultScreenRecording("Pass",  "SuiteRecording");
				}
				}
				builder.CloseReport();
				
				//To send report via email
				SendMail.send();
				
				System.out.println("debugFinally2");
				
			}
    		}	
    
     private static void execute_Actions() {
	
		try {
			for (int i = 0; i < method.length; i++) {

				if (method[i].getName().equals(sActionKeyword)) {
					method[i].invoke(actionKeywords, sPageObject, sData, sDescription, builder);
					if (bResult == true) {
						ExcelUtils.setCellData(TestConfigurations.KEYWORD_PASS, sTestStepId, TestConfigurations.Col_TestStepResult, TestConfigurations.Sheet_TestSteps);
						break;
					} else {
						ExcelUtils.setCellData(TestConfigurations.KEYWORD_FAIL, sTestStepId, TestConfigurations.Col_TestStepResult, TestConfigurations.Sheet_TestSteps);
						//utility.ActionKeywords.closeBrowser("", "", builder);
						break;
					}
				}
			} 
		} catch (Exception e) {
			Log.error("Unable to execute the action:"+sActionKeyword+ "Please see the error message--" +e.getMessage());
			builder.DisplayResult("Fail", sDescription, "Unable to execute the action:"+sActionKeyword+ "Please see the error message--" +e.getMessage());
		}
     }
     
}