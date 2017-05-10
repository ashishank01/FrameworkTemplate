package utility;

import java.io.IOException;


//get runtime process
public class SystemProcesses {

	static Runtime runTime =  Runtime.getRuntime();
	
	public static void  startProcess(String processName)
	{
		//Set run time Object
		try {
			Process proc =runTime.exec(processName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
