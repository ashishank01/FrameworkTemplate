package utility;

import java.util.Date;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import config.TestConfigurations;


public class ReportBuilder {

	
	    ExtentReports extent;
        ExtentTest test; 
        Date date = new Date();
        //Default Report path
        public static String reportPath = System.getProperty("user.dir") + "\\Report\\"+TestConfigurations.ReportName + ".html"; 
        static String screenShotRequired = "Yes";
        
        
	
		@SuppressWarnings("deprecation")
		public ReportBuilder(){
        	
        	
        	if (!(TestConfigurations.ReportLocation).equals("Default"))
            {
            	reportPath = TestConfigurations.ReportLocation +TestConfigurations.ReportName +".html";
            }
        	
        	//String reportPath = System.getProperty("user.dir") + "\\Report\\AssessmentPortal_Report.html";
        	//extent = new ExtentReports(reportPath, true,NetworkMode.ONLINE);
        	extent = new ExtentReports(reportPath, true);
        	  extent.config()
  	        .documentTitle("Automation Report")
  	        .reportName(TestConfigurations.ReportName )
				.reportHeadline("_"+TestConfigurations.Browsers+"_"+date.toString());
        }
        
	
	
//	public static void deleteReport(File folderName) {
//
//		File[] contents = folderName.listFiles();
//		if (contents != null) {
//			for (File f : contents) {
//				deleteReport(f);
//			}
//		}
//		folderName.delete();
//
//	}
	
	 public void StartTestCase(String testCaseName,String description,String category)
     {
       test = extent.startTest(testCaseName, description).assignCategory(category);
      
     }

     public void DisplayResult(String result, String step, String details, String reportPath)
     {  
    	 
    	 if ((TestConfigurations.ScreenshotRequired).equals("Yes"))
    			 {
			    	 if(result.equals("Pass")){
			        	 test.log(LogStatus.PASS, step, details + test.addScreenCapture(reportPath) );
			         }
			         else{
			         if(result.equals("Fail"))
			        	 test.log(LogStatus.FAIL, step, details + test.addScreenCapture(reportPath));
			         } 
    			 }
    	 else if ((TestConfigurations.ScreenshotRequired).equals("No"))
		 {
	    	 if(result.equals("Pass")){
	        	 test.log(LogStatus.PASS,step,details);
	         }
	         else{
	         if(result.equals("Fail"))
	        	 test.log(LogStatus.FAIL, details );
	         } 
		 }
    	 
         
     }
     
     
     public void DisplayResultScreenRecording(String result, String TestCaseId)
     {  
    	 			//Attached report
    	 
    	    if (TestConfigurations.VideoLocation.equalsIgnoreCase("Default"))
    	    {
			    	 if(result.equals("Pass")){
			        	 test.log(LogStatus.PASS, "Test Case Video recording is stored at "+ System.getProperty("user.dir")+"\\Videoes\\"+TestCaseId+".avi",  test.addScreencast(System.getProperty("user.dir")+"\\Videoes\\"+TestCaseId+".avi") );
			         }
			         else{
			         if(result.equals("Fail"))
			        	 test.log(LogStatus.FAIL, "Test Case Video recording is stored at "+ System.getProperty("user.dir")+"\\Videoes\\"+TestCaseId+".avi",  test.addScreencast(System.getProperty("user.dir")+"\\Videoes\\"+TestCaseId+".avi") );
			         } 
    	    }
    	    else
    	    {
    	    if(result.equals("Pass")){
	        	 test.log(LogStatus.PASS, "Test Case Video recording is stored at "+ TestConfigurations.VideoLocation+"\\"+TestCaseId+".avi",  test.addScreencast(TestConfigurations.VideoLocation+"\\"+TestCaseId+".avi") );
	         }
	         else{
	         if(result.equals("Fail"))
	        	 test.log(LogStatus.FAIL, "Test Case Video recording is stored at "+ TestConfigurations.VideoLocation+"\\"+TestCaseId+".avi",  test.addScreencast(TestConfigurations.VideoLocation+"\\"+TestCaseId+".avi") );
	         } 
    	    }
    			
         }
     
     
     public void DisplayResult(String result, String step, String details)
     {  
    	 if(result.equals("Pass")){
        	 test.log(LogStatus.PASS, step, details );
         }
         else{
         if(result.equals("Fail"))
        	 test.log(LogStatus.FAIL, step, details );
         } 
    	 
    	 
         
     }
      
     public void endTest()
     {
         extent.endTest(test);
     }

     public void CloseReport()
     {
         extent.flush();
     }
}
