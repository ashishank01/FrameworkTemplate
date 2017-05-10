package utility;

import java.awt.*;
import java.io.File;

import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import config.TestConfigurations;
import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;
 
public class VideoReord {
    private ScreenRecorder screenRecorder;
  
    
  
 
       public void startRecording(String DestinationFolder, String FileName) throws Exception
       {    
    	   
    	   try {
    		   
    		   if ((TestConfigurations.VideoLocation).equalsIgnoreCase("Default"))
    				   {
    			   
    			   File default_video_file = new File(System.getProperty("user.dir")+"\\Videoes");
    		        if (!default_video_file.exists()) {
    		            if (default_video_file.mkdir()) {
    		            	Log.info("Method Name: startRecording, Description: Default Videoes directory is created as "+System.getProperty("user.dir")+"\\Videoes"+"!");
    		            } else {
    		            	Log.info("Method Name: startRecording, Description: Failed to create Default Videoes directory!");
    		            }
    		        }
    			   DestinationFolder= System.getProperty("user.dir")+"\\Videoes\\";
    			   
    				   }
    		   
    		   
			
			      File file = new File(DestinationFolder);
  		        if (!file.exists()) {
  		            if (file.mkdirs()) {
  		            	Log.info("Method Name: startRecording, Description: Videoes directory is created as "+DestinationFolder+"!");
  		            } else {
  		            	Log.info("Method Name: startRecording, Description: Failed to create Videoes directory!");
  		            }
  		        }
			          
			      
			      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			      int width = screenSize.width;
			      int height = screenSize.height;
			                     
			      Rectangle captureSize = new Rectangle(0,0, width, height);
			                     
			    GraphicsConfiguration gc = GraphicsEnvironment
			       .getLocalGraphicsEnvironment()
			       .getDefaultScreenDevice()
			       .getDefaultConfiguration();
 
			   this.screenRecorder = new SpecializedScreenRecorder(gc, captureSize,
			       new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
			       new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
			            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
			            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
			            QualityKey, 1.0f,
			            KeyFrameIntervalKey, 15 * 60),
			       new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
			            FrameRateKey, Rational.valueOf(30)),
			       null, file, FileName);
			  this.screenRecorder.start();
			  Log.info("Method Name: startRecording, Description: Video Recording Started");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			  Log.fatal("Method Name: startRecording, Description: "+e.getMessage());
		}
       
       }
 
       public void stopRecording() throws Exception
       {
         try {
			this.screenRecorder.stop();
			Log.info("Method Name: StopRecording, Description: Video Recording Stopped");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.fatal("Method Name: startRecording,Description:"+e.getMessage());
		}
       }
}