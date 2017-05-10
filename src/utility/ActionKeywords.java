package utility;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import config.TestConfigurations;
import executionEngine.DriverScript;

public class ActionKeywords {

	// Variable and objects
	public static WebDriver driver = null;
	static WebDriverWait wait = null;
	Actions action = null;

	public static String path;
	// create Excel utility object for finding elements
	ExcelUtils_Updated objExcelUtilsUpdated = new ExcelUtils_Updated();
	//ExcelUtils objExcelUtils = new ExcelUtils();

	public String highLevelAction = "No";

	// Connection object
	static Connection con = null;
	// Statement object
	static Statement stmt;
	// Db URl

	static String DB_URL = "jdbc:" + TestConfigurations.DB + ":"
			+ TestConfigurations.DBURL + "/" + TestConfigurations.DBName;
	// Constant for Database Username
	public static String DB_USER = TestConfigurations.DBUserName;
	// Constant for Database Password
	public static String DB_PASSWORD = TestConfigurations.DBPassword;
	// DB Name
	// public String Db_Name="student";
	// get object repository

	
	//Object for Java ScriptExecuter
	JavascriptExecutor jse= (JavascriptExecutor) driver;
	
	// WebService Variables
	public String output = "";

	/*
	public void clearBrowserCache() throws InterruptedException
	{
		driver.manage().deleteAllCookies();
		Thread.sleep(5000);
		System.out.println("cache deleted");
		
	}
	*/
	
	public void openBrowser(String object, String data, String sDescription,
			ReportBuilder builder) {
		Log.info("Opening Browser");
		try {
			if ((TestConfigurations.Browsers).equals("Mozilla")) {
				driver = new FirefoxDriver();
				Log.info("Mozilla browser started");
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"Mozilla browser started", path);
			} else if ((TestConfigurations.Browsers).equals("IE")) {
				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir")
								+ "\\Drivers\\IEDriverServer.exe");
				driver = new InternetExplorerDriver();

				Log.info("IE browser started");
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"IE browser started", path);
			} else if ((TestConfigurations.Browsers).equals("Chrome")) {
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir")
								+ "\\Drivers\\chromedriver.exe");
				driver = new ChromeDriver();

				Log.info("Chrome browser started");
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"Chrome browser started", path);
			} else if ((TestConfigurations.Browsers).equals("Headless")) {
				driver = new HtmlUnitDriver();
				Log.info("HTML unit browser is  started");
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"HTML unit browser started", path);

			}

			// int implicitWaitTime = (10);

			// driver.manage().timeouts().implicitlyWait(implicitWaitTime,
			// TimeUnit.SECONDS);
			// Element Wait
			action = new Actions(driver);
			driver.manage()
					.timeouts()
					.implicitlyWait(TestConfigurations.ElementWait,
							TimeUnit.SECONDS);
			// page wait
			driver.manage()
					.timeouts()
					.pageLoadTimeout(TestConfigurations.PageWait,
							TimeUnit.SECONDS);

		} catch (Exception e) {
			Log.info("Not able to open the Browser --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", "Launch browser",
					"Not able to open the Browser --- " + e.getMessage(), path);
			DriverScript.bResult = false;

		}
	}

	public void navigate(String object, String data, String sDescription,
			ReportBuilder builder) throws InterruptedException {
		try {
			//clearBrowserCache();
			Log.info("Navigating to URL");

			// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			driver.get(TestConfigurations.AppURL);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Navigated to URL",
					path);

		} catch (Exception e) {
			//clearBrowserCache();
			Log.info("Not able to navigate --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to navigate --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
			e.printStackTrace();

		}
	}

	public WebElement findElement(String ObjSheetName, String object) {
		int rowNumber = 0;
		WebElement element = null;
		try {

			objExcelUtilsUpdated.setExcelFile(TestConfigurations.ObjectRepository_path);
			int cellPosition[] = objExcelUtilsUpdated.getCellNumber(
					ObjSheetName, object);
			rowNumber = cellPosition[0];
			// get element Xpath
			String Xpath = objExcelUtilsUpdated.getCellData(rowNumber, 2,
					ObjSheetName);

			// skip If value is Null

			if ((Xpath).equals("")) {
				Log.info("Class ActionKeywords | Method findElement | Xpath is Null so Skiping Find Element with Xpath");
				throw new NullPointerException("Xpath is Null");
			} else {
				System.out.println("try Xpath");
				// find element by Xpath
				element = driver.findElement(By.xpath(Xpath));
				Log.info("Class ActionKeywords | Method findElement | Able to find the element with xpath");
			}

		} catch (Exception expath) {
			// try with CSS
			try {

				String css = objExcelUtilsUpdated.getCellData(rowNumber, 3,
						ObjSheetName);
				// skip If value is Null
				if ((css).equals("")) {
					Log.info("Class ActionKeywords | Method findElement | CSS is Null so Skiping Find Element with CSS");
					throw new NullPointerException("CSS is Null");

				} else {
					// get element CSS
					System.out.println("try CSS");
					element = driver.findElement(By.cssSelector(css));
					Log.info("Class ActionKeywords | Method findElement | Able to find the element with CSS");
				}
			} catch (Exception ecss) {

				// try with ID
				try {
					// get element ID
					String id = objExcelUtilsUpdated.getCellData(rowNumber, 4,
							ObjSheetName);
					// skip If value is Null
					if ((id).equals("")) {
						Log.info("Class ActionKeywords | Method findElement | id is Null so Skiping Find Element with id");
						throw new NullPointerException("ID is Null");
					} else {
						System.out.println("try ID");
						element = driver.findElement(By.id(id));
						Log.info("Class ActionKeywords | Method findElement | Able to find the element with ID");
					}
				} catch (Exception eid) {
					// try with Name
					try {
						String name = objExcelUtilsUpdated.getCellData(
								rowNumber, 5, ObjSheetName);
						// skip If value is Null
						if ((name).equals("")) {
							Log.info("Class ActionKeywords | Method findElement | name is Null so Skiping Find Element with name");
							throw new NullPointerException("Name is Null");
						} else {
							// get element Name
							System.out.println("try Name");
							element = driver.findElement(By.name(name));
							Log.info("Class ActionKeywords | Method findElement | Able to find the element with name");
						}
					} catch (Exception ename) {
						// try with Class
						try {
							// get element Class
							String className = objExcelUtilsUpdated
									.getCellData(rowNumber, 5, ObjSheetName);
							// skip If value is Null
							if ((className).equals("")) {
								Log.info("Class ActionKeywords | Method findElement | className is Null so Skiping Find Element with className");
								throw new NullPointerException(
										"ClassName is Null");
							} else {
								System.out.println("try Class");

								element = driver.findElement(By
										.className(className));
								Log.info("Class ActionKeywords | Method findElement | Able to find the element with Class name");
							}
						} catch (Exception eclass) {
							System.out.println("Final Catch");
							Log.info("Class ActionKeywords | Method findElement |  unable to find the element with all the given property check object name specified in Excel or its given properties");

						}

					}

				}

			}

		}

		return element;

	}

	public void verifyElementExist(String object, String data,
			String sDescription, ReportBuilder builder) {

		try {
			Log.info("Wait for object visibility");

			wait = new WebDriverWait(driver, 50);
			int cellPosition[] = objExcelUtilsUpdated.getCellNumber(
					TestConfigurations.ORSheetName, object);
			int rowNumber = cellPosition[0];
			// get element Xpath
			String xpath = objExcelUtilsUpdated.getCellData(rowNumber, 2,
					TestConfigurations.ORSheetName);

			wait.until(ExpectedConditions.visibilityOfElementLocated(By
					.xpath(xpath)));

			if (!(highLevelAction.equals("Yes"))) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"The specified object " + object + " is exist", path);
			}

		} catch (Exception e) {
			Log.error("The element:" + object + "is not present");
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription, "The element:" + object
					+ " is not present", path);
			// driver.close();
			DriverScript.bResult = false;

		}

	}

	public void verifyElementNotExist(String object, String data,
			String sDescription, ReportBuilder builder) {

		try {
			Log.info("Clicking on  " + object);

			int cellPosition[] = objExcelUtilsUpdated.getCellNumber(
					TestConfigurations.ORSheetName, object);
			int rowNumber = cellPosition[0];
			// get element Xpath
			String xpath = objExcelUtilsUpdated.getCellData(rowNumber, 2,
					TestConfigurations.ORSheetName);

			if (driver.findElements(By.xpath(xpath)).size() != 0) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Fail", sDescription, "Element Exist",
						path);
			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("PAss", sDescription,
						"Element Does not Exist", path);
			}

		} catch (Exception e) {
			Log.error("Not able to verify  --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to Verify --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}

	}

	public void doubleClick(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Double Clicking on Weblement " + object);
			action.doubleClick(
					findElement(TestConfigurations.ORSheetName, object))
					.build().perform();
		//	driver.findElement(By.linkText("Forgot Password?")).click();

			if (!(highLevelAction.equals("Yes"))) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"Double Clicked on object", path);
			}

		} catch (Exception e) {
			Log.error("Not able to double click --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to double click --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void javaScriptClick(String object, String data,
			String sDescription, ReportBuilder builder) {
		try {
			Log.info("Sending click event on Element using java script "
					+ object);

			//JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].click();",
					findElement(TestConfigurations.ORSheetName, object));

			if (!(highLevelAction.equals("Yes"))) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"Sending click event on Element using java script",
						path);
			}

		} catch (Exception e) {
			Log.error("Not able to Send click event on Element using java script --- "
					+ e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to Send click event on Element using java script--- "
							+ e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void click(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Clicking on Webelement " + object);
			findElement(TestConfigurations.ORSheetName, object).click();
			if (!(highLevelAction.equals("Yes"))) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"Clicked on object", path);
			}

		} catch (Exception e) {
			Log.error("Not able to click --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to click --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void input(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Entering the text in " + object);

			// WebElement element =
			// driver.findElement(By.xpath(OR.getProperty(object)));
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			element.click();
			element.clear();
			element.sendKeys(data);
			if (!(highLevelAction.equals("Yes"))) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription, "Text entered",
						path);
			}

		} catch (Exception e) {
			DriverScript.bResult = false;
			Log.error("Not able to Enter text --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to Enter text --- " + e.getMessage(), path);
			// driver.close();
			

		}
	}

	public void waitFor(String object, String data, String sDescription,
			ReportBuilder builder) throws Exception {
		try {
			Log.info("Wait for 10 seconds");

			Thread.sleep(10000);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Test step passed",
					path);

		} catch (Exception e) {
			Log.error("Not able to Wait --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription, "Not able to Wait --- "
					+ e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public static void closeBrowser(String object, String data,
			String sDescription, ReportBuilder builder) {
		try {
			Log.info("Closing the browser");

			System.out.println("DebugClosebrowser");
			// driver.wait(2000);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Browser closed", path);
			driver.close();

			// driver.close();
			// driver.quit();

		} catch (Exception e) {
			Log.error("Not able to Close the Browser --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to Close the Browser --- " + e.getMessage(), path);
			DriverScript.bResult = false;
		}
	}

	public void waitforobject(String object, String data, String sDescription,
			ReportBuilder builder) {

		try {
			Log.info("Wait for object");
	
			if (data.equals(null)) {
				data = "60";
			}
			int time = Integer.parseInt(data);
			wait = new WebDriverWait(driver, time);
			int cellPosition[] = objExcelUtilsUpdated.getCellNumber(
					TestConfigurations.ORSheetName, object);
			int rowNumber = cellPosition[0];
			// get element Xpath
			String xpath = objExcelUtilsUpdated.getCellData(rowNumber, 2,
					TestConfigurations.ORSheetName);

			wait.until(ExpectedConditions.visibilityOfElementLocated(By
					.xpath(xpath)));
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Test step passed",
					path);

		} catch (Exception e) {
			Log.error("The element:" + object + "is not present");
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription, "The element:" + object
					+ " is not present", path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void waitForObjectNotVisible(String object, String data,
			String sDescription, ReportBuilder builder) {

		try {
			Log.info("Wait for object to disapear");
			if (data.equals(null)) {
				data = "60";
			}
			int time = Integer.parseInt(data);
			wait = new WebDriverWait(driver, time);
			int cellPosition[] = objExcelUtilsUpdated.getCellNumber(
					TestConfigurations.ORSheetName, object);
			int rowNumber = cellPosition[0];
			// get element Xpath
			String xpath = objExcelUtilsUpdated.getCellData(rowNumber, 2,
					TestConfigurations.ORSheetName);

			wait.until(ExpectedConditions.invisibilityOfElementLocated(By
					.xpath(xpath)));
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Test step passed",
					path);
			System.out.println("Loading Completed");

		} catch (Exception e) {
			Log.error("The element:" + object
					+ "is not disapeared within given time limit");
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription, "The element:" + object
					+ " is not disappeared within given time limit", path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public static String TakeScreenshot(String ImageName) {
		TakesScreenshot oScn = (TakesScreenshot) driver;
		File oScnShot = oScn.getScreenshotAs(OutputType.FILE);
		// String path = "C:\\Screenshot\\"+ImageName+".png";
		// String path =
		// System.getProperty("user.dir")+"//Screenshot//"+ImageName+".png";
		String path = System.getProperty("user.dir") + "//Screenshot//"
				+ ImageName + "." + TestConfigurations.Screenshotformat;
		Log.info("Capturing ScreenShot");
		// if screenshot location is provided in configurations file then it
		// should be used to store screenshots
		if (!(TestConfigurations.Screenshotlocation).equals("Default")) {
			path = TestConfigurations.Screenshotlocation + ImageName + "."
					+ TestConfigurations.Screenshotformat;
		}

		File oDest = new File(path);
		try {
			FileUtils.copyFile(oScnShot, oDest);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			// driver.close();
			Log.error("Unable to capture screenshot:" + e.getMessage());
		}
		return path;
	}

	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void scrollDown(String object, String data, String sDescription,
			ReportBuilder builder) {

		try {
			Log.info("Scroll Down Page");
		//	JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("window.scrollBy(0,250)", "");
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Test step passed",
					path);
		} catch (Exception e) {
			Log.error("Scroll Down error");
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription, "Scroll Down Failed",
					path);
			// driver.close();
			DriverScript.bResult = false;
			e.printStackTrace();
		}
	}

	public void clickEnter(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Clicking on Enter ");

			findElement(TestConfigurations.ORSheetName, object).sendKeys(
					Keys.ENTER);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Enter", path);

		} catch (Exception e) {
			Log.error("Not able to press enter key --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to press enter key --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void dropdownSelection(String object, String data,
			String sDescription, ReportBuilder builder) {
		try {
			Log.info("Selecting " + data + " from dropdown");
			Select dropdown = new Select(findElement(
					TestConfigurations.ORSheetName, object));
			dropdown.selectByValue(data);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription,
					"Value selected from dropdown", path);

		} catch (Exception e) {
			Log.error("Not able to select value from drop down --- "
					+ e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult(
					"Fail",
					sDescription,
					"Not able to select value from drop down ---"
							+ e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void verifyDisabled(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Verify " + object + " disabled");
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			if ("false" == element.getAttribute("readonly")
					|| "false" == element.getAttribute("disabled")) {

				builder.DisplayResult("Fail", sDescription,
						"Element is enabled", path);
			} else {
				builder.DisplayResult("Pass", sDescription, "Disabled", path);
			}

			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);

		} catch (Exception e) {
			Log.error("Error" + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Error" + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void verifyDropdownValue(String object, String data,
			String sDescription, ReportBuilder builder) {
		try {
			Log.info("Selecting " + data + " from dropdown");
			Select dropdown = new Select(findElement(
					TestConfigurations.ORSheetName, object));
			String selectedValue = dropdown.getFirstSelectedOption().getText();
			if (data.equals(selectedValue)) {
				builder.DisplayResult("Pass", sDescription, data
						+ " Value selected from dropdown", path);
			} else {
				builder.DisplayResult("Fail", sDescription,
						"Different value selected in dropdown", path);
			}
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);

		} catch (Exception e) {
			Log.error("Error --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Error ---" + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void verifyEnabled(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Verify " + object + " enabled");
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			if ("true" == element.getAttribute("readonly")
					|| "true" == element.getAttribute("disabled")) {

				builder.DisplayResult("Fail", sDescription,
						"Element is disabled", path);
			} else {
				builder.DisplayResult("Pass", sDescription, "Enabled", path);
			}

			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);

		} catch (Exception e) {
			Log.error("Error" + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Error" + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}

	}

	public void tooltip(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Get Tooltip  Text");
			Actions tooltip = new Actions(driver);
			WebElement toolTipObject = findElement(
					TestConfigurations.ORSheetName, object);
			// toolTipObject.getAttribute("title");
			tooltip.clickAndHold(toolTipObject).perform();
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Test step passed",
					path);

		} catch (Exception e) {
			Log.error("Tooltip text error");
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Tooltip identification failed", path);
			// driver.close();
			DriverScript.bResult = false;
			e.printStackTrace();
		}

	}

	public void Tab(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Tab on Webelement " + object);

			findElement(TestConfigurations.ORSheetName, object).sendKeys(
					Keys.TAB);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Tab", path);

		} catch (Exception e) {
			Log.error("Not able to Tab --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription, "Not able to Tab --- "
					+ e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void Enter(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			Log.info("Enter on Webelement " + object);

			findElement(TestConfigurations.ORSheetName, object).sendKeys(
					Keys.ENTER);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "Tab", path);
			Log.info("Successfully Enter on Webelement " + object);

		} catch (Exception e) {
			Log.error("Not able to Enter --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to Enter --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	public void elementHighlight(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		for (int iCnt = 0; iCnt < 1; iCnt++) {
			// Execute java script
			jse.executeScript(
					"setTimeout(arguments[0].style.border='8px groove red',50000)",
					element);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jse.executeScript("arguments[0].style.border=''", element);
		}
	}

	
	public void scrollToView(String object, String data, String sDescription,ReportBuilder builder)
	{
		try {
			Log.info("Scroll to view the Webelement " + object);

			JavascriptExecutor jse= (JavascriptExecutor) driver;
			WebElement  element = findElement(TestConfigurations.ORSheetName, object);
			jse.executeScript("arguments[0].scrollIntoView(true);",element );
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "scrollToView", path);
			Log.info("Successfully scrolled for element Visibillity " + object);

		} catch (Exception e) {
			Log.error("Not Able to Scroll for Elemnt Visibillity --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to Enter --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
				
	}
	
	
	public void verifyElementText(String object, String data,
			String sDescription, ReportBuilder builder) {
		try {
			Log.info("Verifying text of the given element : " +object);
			
			Thread.sleep(20000);

			WebElement  element = findElement(TestConfigurations.ORSheetName, object);
			
			String elementText = element.getText().trim();
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			if (data.trim().equals(elementText)) {
				builder.DisplayResult("Pass", sDescription, "Actual Value matched the Expected value. Actual : "+elementText+" , Expected : "+data, path);
				Log.info("Actual Value matched the Expected value. Actual : "+elementText+" , Expected : "+data);
			} else {
				builder.DisplayResult("Fail", sDescription, "Actual Value didn't matched the Expected value. Actual : "+elementText+" Expected : "+data, path);
				Log.fatal("Actual Value didn't matched the Expected value. Actual : "+elementText+" , Expected : "+data);
			}
			

		} catch (Exception e) {
			Log.error("Error --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Error ---" + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}

	
	public void pageTitle(String object, String data, String sDescription,ReportBuilder builder)
	{
		try {
			Log.info("Getting the page title " + object);
			driver.getTitle().equalsIgnoreCase(data);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "PageTitle", path);
			Log.info("Successfully able to get the page title " + object);

		} catch (Exception e) {
			Log.error("Not able to get Page Title --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Unable to get Page Title --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
				
	}
	
	public void sourcecodeContains(String object, String data, String sDescription,ReportBuilder builder)
	{
		try {
			Log.info("Getting the Socurce Code " + object);
			driver.getPageSource().contains(data);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "PageSource", path);
			Log.info("Successfully able to get the page source " + object);

		} catch (Exception e) {
			Log.error("Unable to get Page Source --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Unable to get Page Source --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
				
	}	
	
	public static void hover(String object, String data){
		try{
			Log.info("Hovering on Webelement "+ object);
			WebElement element = driver.findElement(By.linkText(data));	
			Actions action=new Actions(driver);
			action.moveToElement(element).build().perform();
		 }catch(Exception e){
 			Log.error("Class ActionKeywords | Method hover | Exception desc : " +"Not able to hover --- " + e.getMessage());
 			DriverScript.bResult = false;
         	}
		}		
	public void hover(String object, String data, String sDescription,ReportBuilder builder)
	{
		try {
			Log.info("Hovering on Webelement "+ object);
			WebElement element = driver.findElement(By.linkText(data));	
			Actions action=new Actions(driver);
			action.moveToElement(element).build().perform();
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Pass", sDescription, "PageSource", path);
			Log.info("Successfully hovering over the element " + object);

		} catch (Exception e) {
			Log.error("Unable to hover over the element --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Unable to hover over the element --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
				
	}		
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// UI Validations Actions

	public void verifyBackgroundColor(String object, String data,
			String sDescription, ReportBuilder builder) {

		// declare variable
		String background;
		try {
			Log.info("Verifying Background color of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			// bagroundColor=driver.findElement(By.xpath(OR.getProperty(object))).getCssValue("background-color");
			background = element.getCssValue("background-color");
			// Not Implemented Completely
			elementHighlight(element);
			if (background.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"Background color for web Element " + object
								+ " is as expected,::Expected:" + data
								+ ",Actual:" + background, path);

				Log.info("Successfully able to verify the Background color on Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"Background color for web Element " + object
								+ " is not as expected,::Expected:" + data
								+ ",Actual:" + background, path);

				Log.error("background color of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + background);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the background color --- "
					+ e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the background color for web Element "
							+ object + " --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyColor(String object, String data, String sDescription,
			ReportBuilder builder) {
		// declare variable
		String color;
		try {
			Log.info("Verifying  color of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			color = element.getCssValue("color");
			elementHighlight(element);
			if (color.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"Color for web Element " + object
								+ " matches  to expected, :: Expected:" + data
								+ ",Actual:" + color, path);

				Log.info("Successfully able to verify the color on Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"Color for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + color, path);

				Log.error("color of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + color);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the color --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the color for web Element " + object
							+ " --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyFontSize(String object, String data, String sDescription,
			ReportBuilder builder) {
		// declare variable
		String fontSize;
		try {
			Log.info("Verifying  fontSize of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			fontSize = element.getCssValue("font-size");
			elementHighlight(element);
			if (fontSize.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"fontSize for web Element " + object
								+ " matches to expected, :: Expected:" + data
								+ ",Actual:" + fontSize, path);

				Log.info("Successfully able to verify the fontSize of Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"fontSize for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + fontSize, path);

				Log.error("fontSize of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + fontSize);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the fontSize --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the fontSize for web Element " + object
							+ " --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyFontWeight(String object, String data,
			String sDescription, ReportBuilder builder) {
		// declare variable
		String fontWeight;
		try {
			Log.info("Verifying  fontWeight of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			fontWeight = element.getCssValue("font-weight");
			elementHighlight(element);
			if (fontWeight.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"fontWeight for web Element " + object
								+ " matches to expected, :: Expected:" + data
								+ ",Actual:" + fontWeight, path);

				Log.info("Successfully able to verify the fontWeight of Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"fontWeight for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + fontWeight, path);

				Log.error("fontWeight of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + fontWeight);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the fontWeight --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the fontWeight for web Element "
							+ object + " --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyDisplay(String object, String data, String sDescription,
			ReportBuilder builder) {
		// declare variable
		String display;
		try {
			Log.info("Verifying  display of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			display = element.getCssValue("display");
			elementHighlight(element);
			if (display.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"display for web Element " + object
								+ " matches to expected, :: Expected:" + data
								+ ",Actual:" + display, path);

				Log.info("Successfully able to verify the display of Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"display for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + display, path);

				Log.error("display of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + display);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the display --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the display for web Element " + object
							+ " --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyPadding(String object, String data, String sDescription,
			ReportBuilder builder) {
		// declare variable
		String padding;
		try {
			Log.info("Verifying  padding of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			padding = element.getCssValue("padding");
			elementHighlight(element);
			if (padding.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"padding for web Element " + object
								+ " matches to expected, :: Expected:" + data
								+ ",Actual:" + padding, path);

				Log.info("Successfully able to verify the padding of Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"padding for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + padding, path);

				Log.error("padding of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + padding);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the padding --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the padding for web Element " + object
							+ "--- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyBorder(String object, String data, String sDescription,
			ReportBuilder builder) {
		// declare variable
		String border;
		try {
			Log.info("Verifying  border of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			border = element.getCssValue("border");
			elementHighlight(element);
			if (border.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"border for web Element " + object
								+ " matches to expected, :: Expected:" + data
								+ ",Actual:" + border, path);

				Log.info("Successfully able to verify the border of Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"border for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + border, path);

				Log.error("border of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + border);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the border --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the border for web Element " + object
							+ "--- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyHeight(String object, String data, String sDescription,
			ReportBuilder builder) {
		// declare variable
		String height;
		try {
			Log.info("Verifying  height of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			height = element.getCssValue("height");
			elementHighlight(element);
			if (height.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"height for web Element " + object
								+ " matches to expected, :: Expected:" + data
								+ ",Actual:" + height, path);

				Log.info("Successfully able to verify the height of Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"height for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + height, path);

				Log.error("height of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + height);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the height --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the height for web Element " + object
							+ " --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyWidth(String object, String data, String sDescription,
			ReportBuilder builder) {
		// declare variable
		String width;
		try {
			Log.info("Verifying  width of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			width = element.getCssValue("width");
			elementHighlight(element);
			if (width.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"width  for web Element " + object
								+ " matches to expected, :: Expected:" + data
								+ ",Actual:" + width, path);

				Log.info("Successfully able to verify the width of Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"width for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + width, path);

				Log.error("width of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + width);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the width --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the width for web Element " + object
							+ " --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	public void verifyTextAlign(String object, String data,
			String sDescription, ReportBuilder builder) {
		// declare variable
		String textAlign;
		try {
			Log.info("Verifying  width of Webelement " + object);
			WebElement element = findElement(TestConfigurations.ORSheetName,
					object);
			textAlign = element.getCssValue("text-align");
			elementHighlight(element);
			if (textAlign.equals(data)) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Pass", sDescription,
						"textAlignment for web Element " + object
								+ " matches to expected, :: Expected:" + data
								+ ",Actual:" + textAlign, path);

				Log.info("Successfully able to verify the textAlignment of Webelement "
						+ object);

			} else {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);

				builder.DisplayResult("Fail", sDescription,
						"textAlignment for web Element " + object
								+ " Doesn't match to expected,:: Expected:"
								+ data + ",Actual:" + textAlign, path);

				Log.error("textAlignment of Webelement " + object
						+ " is not as Expected" + " ::Expected:" + data
						+ ",Actual:" + textAlign);
			}

		} catch (Exception e) {
			Log.error("Not able to verify the textAlignment --- "
					+ e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to verify the textAlignment for web Element "
							+ object + "--- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// DB Actions

	public void setDataBase(String object, String data, String sDescription,
			ReportBuilder builder) throws Exception, IllegalAccessException,
			ClassNotFoundException {
		// path = "Test Path";
		try {
			// make DB connection
			String dbClass = "com.mysql.jdbc.Driver";

			Class.forName(dbClass).newInstance();
			// Get connection to DB
			con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			// Statement object to send the SQL statement to the Database
			stmt = con.createStatement();
			builder.DisplayResult("Pass", sDescription,
					"User is Successfully able to Connect to DB");
			Log.info("Successfully able to set the DB ");

		} catch (Exception e) {

			// path =
			// TakeScreenshot(DriverScript.sTestCaseID+"_StepId_"+DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"User is unable to Connect to DB" + e.getMessage());
			Log.error("Pass--unable to set the DB ----" + e.getMessage());

			DriverScript.bResult = false;
		}
	}

	public void executeQuery(String object, String data, String sDescription,
			ReportBuilder builder) {
		ResultSet res;

		try {

			res = stmt.executeQuery(object);
			int i = 0;
			List<String> result = new ArrayList<String>();
			while (res.next()) {

				result.add(i, res.getString(1));
				String verifyResult = (String) result.get(i);

				if (verifyResult.equals(data)) {
					builder.DisplayResult("Pass", sDescription,
							"User is Successfully able to verify the DB , Expected="
									+ data + ",Actual=" + verifyResult);
					Log.info("Pass--User is Successfully able to verify the DB , Expected="
							+ data + ",Actual=" + verifyResult);

					// System.out.println("Pass"+verifyResult +"="+data);
				} else {
					builder.DisplayResult("Fail", sDescription,
							"User is unable to verify the DB , Expected="
									+ data + ",Actual=" + verifyResult);
					Log.error("User is unable to verify the DB , Expected="
							+ data + ",Actual=" + verifyResult);
				}
				i = i + 1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			builder.DisplayResult("Fail", sDescription,
					"User is unable to verify the DB :" + e.getMessage());
			Log.error("User is unable to verify the DB" + e.getMessage());
		}

	}

	public void closeDBConnection(String object, String data,
			String sDescription, ReportBuilder builder) throws Exception,
			IllegalAccessException, ClassNotFoundException {
		// path = "Test Path";
		try {
			// Close DB connection
			if (con != null) {
				con.close();
			}
			builder.DisplayResult("Pass", sDescription,
					"User is Successfully able to disConnect to DB");
			Log.info("Successfully able to disconnect the Db");

		} catch (Exception e) {
			builder.DisplayResult("Fail", sDescription,
					"User is unable to disConnect to DB" + e.getMessage());
			Log.error("Pass--unable to disconnect the DB ----" + e.getMessage());
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Application Specific Actions

	public void Ac_Login(String object, String data, String sDescription,
			ReportBuilder builder) {

		try {
			// set action to high level action
			highLevelAction = "Yes";
			Log.info("User is trying to login ");

			String userData[] = data.split("\\|");
			// Enter User Name
			// WebElement input_UserName =
			// findElement(TestConfigurations.ORSheetName, "txtbx_EmailID");
			input("txtbx_EmailID", userData[0], "Enter UserName", builder);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId + "EnterUserName");
			builder.DisplayResult("Pass", "Enter User Name",
					"User name Entered Sucessfully", path);
			Log.info("Username entered Successfully ");
			// Enter Password
			input("txtbx_Password", userData[1], "Enter Password", builder);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId + "EnterPassword");
			builder.DisplayResult("Pass", "Enter password",
					"Password Entered Sucessfully", path);
			Log.info("Password entered Successfully ");
			// Click on SignIn
			click("Btn_SignIn", "No Data Required", "Enter UserName", builder);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId + "ClickSignIn");
			builder.DisplayResult("Pass", "Click on on Sign Button",
					"Clicked on SighIn button Sucessfully", path);
			Log.info("Clicked on SignIn Successfully ");
			// Validate User Logged In
			verifyElementExist("Signin_Msg", "No Data Required",
					"Verify User logged in Successfully", builder);
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId + "VerifyLogin");
			builder.DisplayResult("Pass",
					"Element exist on the web page or not",
					"User logged in Successfully", path);
			Log.info("User logged in Successfully ");

		} catch (Exception e) {
			Log.error("Not able Login --- " + e.getMessage());
			// currently not using screenshot for high level action as it is not
			// required
			// path =
			// TakeScreenshot(DriverScript.sTestCaseID+"_StepId_"+DriverScript.sTestStepId);
			// builder.DisplayResult("Fail", sDescription, "Unable to login---"
			// + e.getMessage(),path);
			// driver.close();
			DriverScript.bResult = false;

		} finally {
			// set action to Low level action
			highLevelAction = "No";
		}

	}

public List <WebElement> findElements(String ObjSheetName, String object) {
		int rowNumber = 0;
		List<WebElement> elements = null;
		try {

			objExcelUtilsUpdated.setExcelFile(TestConfigurations.ObjectRepository_path);
			int cellPosition[] = objExcelUtilsUpdated.getCellNumber(
					ObjSheetName, object);
			rowNumber = cellPosition[0];
			// get element Xpath
			String Xpath = objExcelUtilsUpdated.getCellData(rowNumber, 2,
					ObjSheetName);

			// skip If value is Null

			if ((Xpath).equals("")) {
				Log.info("Class ActionKeywords | Method findelements | Xpath is Null so Skiping Find elements with Xpath");
				throw new NullPointerException("Xpath is Null");
			} else {
				System.out.println("try Xpath");
				// find elements by Xpath
				elements= driver.findElements(By.xpath(Xpath));
				
				Log.info("Class ActionKeywords | Method findelements | Able to find the elements with xpath");
			}

		} catch (Exception expath) {
			// try with CSS
			try {

				String css = objExcelUtilsUpdated.getCellData(rowNumber, 3,
						ObjSheetName);
				// skip If value is Null
				if ((css).equals("")) {
					Log.info("Class ActionKeywords | Method findelements | CSS is Null so Skiping Find elements with CSS");
					throw new NullPointerException("CSS is Null");

				} else {
					// get elements CSS
					System.out.println("try CSS");
					elements = driver.findElements(By.cssSelector(css));
					Log.info("Class ActionKeywords | Method findelements | Able to find the elements with CSS");
				}
			} catch (Exception ecss) {

				// try with ID
				try {
					// get elements ID
					String id = objExcelUtilsUpdated.getCellData(rowNumber, 4,
							ObjSheetName);
					// skip If value is Null
					if ((id).equals("")) {
						Log.info("Class ActionKeywords | Method findelements | id is Null so Skiping Find elements with id");
						throw new NullPointerException("ID is Null");
					} else {
						System.out.println("try ID");
						elements = driver.findElements(By.id(id));
						Log.info("Class ActionKeywords | Method findelements | Able to find the elements with ID");
					}
				} catch (Exception eid) {
					// try with Name
					try {
						String name = objExcelUtilsUpdated.getCellData(
								rowNumber, 5, ObjSheetName);
						// skip If value is Null
						if ((name).equals("")) {
							Log.info("Class ActionKeywords | Method findelements | name is Null so Skiping Find elements with name");
							throw new NullPointerException("Name is Null");
						} else {
							// get elements Name
							System.out.println("try Name");
							elements = driver.findElements(By.name(name));
							Log.info("Class ActionKeywords | Method findelements | Able to find the elements with name");
						}
					} catch (Exception ename) {
						// try with Class
						try {
							// get elements Class
							String className = objExcelUtilsUpdated
									.getCellData(rowNumber, 5, ObjSheetName);
							// skip If value is Null
							if ((className).equals("")) {
								Log.info("Class ActionKeywords | Method findelements | className is Null so Skiping Find elements with className");
								throw new NullPointerException(
										"ClassName is Null");
							} else {
								System.out.println("try Class");

								elements = driver.findElements(By
										.className(className));
								Log.info("Class ActionKeywords | Method findelements | Able to find the elements with Class name");
							}
						} catch (Exception eclass) {
							System.out.println("Final Catch");
							Log.info("Class ActionKeywords | Method findelements |  unable to find the elements with all the given property check object name specified in Excel or its given properties");

						}

					}

				}

			}

		}

		return elements;

	}





	public void Ac_SelectListElemntByText(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {
			
			String dynamicXpath= "//li[text()='"+data+"']";
			System.out.println("xpath is" +dynamicXpath);
			Log.info("Selecting Webelement " + "with text "+ data);
			driver.findElement(By.xpath(dynamicXpath)).click();
			
			if (!(highLevelAction.equals("Yes"))) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"Selectd the required object", path);
			}

		} catch (Exception e) {
			Log.error("Not able to Select the object --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to Select --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}
	
	
	public void Ac_SelectCheckbox(String object, String data, String sDescription,
			ReportBuilder builder) {
		try {

			int cellPosition[] = objExcelUtilsUpdated.getCellNumber(
					TestConfigurations.ORSheetName, object);
			int rowNumber = cellPosition[0];
			// get element Xpath
			String xpath = objExcelUtilsUpdated.getCellData(rowNumber, 2,TestConfigurations.ORSheetName);

			List<WebElement> elements = driver.findElements(By.xpath(xpath));
			Log.info("Selecting Webelement " + "with text "+ data);
			for(int i=0; i<elements.size();i++){
			if(elements.get(i).getAttribute("value").equals(data)){
				elements.get(i).click();
			}
			}
			
			if (!(highLevelAction.equals("Yes"))) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
						+ DriverScript.sTestStepId);
				builder.DisplayResult("Pass", sDescription,
						"Selected the required object", path);
			}
	
		} catch (Exception e) {
			Log.error("Not able to Select the object --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
					+ DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription,
					"Not able to Select --- " + e.getMessage(), path);
			// driver.close();
			DriverScript.bResult = false;

		}
	}
	
	public void AC_verifyDataPresent(String object, String data, String sDescription, ReportBuilder builder) {
		try {
			Boolean dataPresent = false;
			List<WebElement> elements = findElements(TestConfigurations.ORSheetName, object);
			Log.info("Verify Text " + data+ "Present");

			for (int i = 0; i < elements.size(); i++) {

				if (elements.get(i).getText().contains(data)) {
					dataPresent = true;
				}
			}

			if (!(highLevelAction.equals("Yes"))) {
				path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_" + DriverScript.sTestStepId);
				if (dataPresent.equals(true)) {
					builder.DisplayResult("Pass", sDescription, "Required text is present", path);
				}
				else {
				builder.DisplayResult("Fail", sDescription, "Required text is not present", path);
				}
			}

		} catch (Exception e) {
			Log.error("Error in verifying text --- " + e.getMessage());
			path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_" + DriverScript.sTestStepId);
			builder.DisplayResult("Fail", sDescription, "Error in verifying text --- " + e.getMessage(), path);

			DriverScript.bResult = false;

		}
		
	}
	
	
	public void Ac_ClickBubble(String object, String data, String sDescription,
			   ReportBuilder builder) {
			  try {
			   String dynamicXpath= "//div[contains(@title,'ZIP3:"+data+"')]";
			   
			   Log.info("Clicking on "+data+ " bubble");
			   
			   JavascriptExecutor jse = (JavascriptExecutor) driver;
			   jse.executeScript("arguments[0].click();",
			     driver.findElement(By.xpath(dynamicXpath)));

			   if (!(highLevelAction.equals("Yes"))) {
			    path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
			      + DriverScript.sTestStepId);
			    builder.DisplayResult("Pass", sDescription,
			      "Sending click event on Element using java script",
			      path);
			   }

			  } catch (Exception e) {
			   Log.error("Not able to Select the object --- " + e.getMessage());
			   path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
			     + DriverScript.sTestStepId);
			   builder.DisplayResult("Fail", sDescription,
			     "Not able to Select --- " + e.getMessage(), path);
			   // driver.close();
			   DriverScript.bResult = false;

			  }
			 }
			 
			 
			 public void Ac_VerifyBubbleExists(String object, String data, String sDescription,
			   ReportBuilder builder) {
			  try {
			   String dynamicXpath= "//div[contains(@title,'ZIP3:"+data+"')]";
			   
			   Log.info("Verify Bubble "+data+ " exist");
			   
			   wait = new WebDriverWait(driver, 50);
			 
			   wait.until(ExpectedConditions.visibilityOfElementLocated(By
			     .xpath(dynamicXpath)));

			   if (!(highLevelAction.equals("Yes"))) {
			    path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
			      + DriverScript.sTestStepId);
			    builder.DisplayResult("Pass", sDescription,
			      "The specified bubble " + data + " exist", path);
			   }

			  } catch (Exception e) {
			   Log.error("The Bubble:" + data + "is not present");
			   path = TakeScreenshot(DriverScript.sTestCaseID + "_StepId_"
			     + DriverScript.sTestStepId);
			   builder.DisplayResult("Fail", sDescription, "The bubble:" + data
			     + " is not present", path);
			   // driver.close();
			   DriverScript.bResult = false;

			  }

			 }
	
	
	
	
	
	
}
