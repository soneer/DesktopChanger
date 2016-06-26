import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;


import org.joda.time.DateTime;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
//import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.win32.W32APIOptions;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

public class WallpaperChanger {   

	private static HashMap<Integer,String> sunriseHashMap = new HashMap<Integer,String>();  
	private static HashMap<Integer,String> daytimeHashMap = new HashMap<Integer,String>(); 
	private static HashMap<Integer,String> sunsetHashMap = new HashMap<Integer,String>();  
	private static HashMap<Integer,String> nightHashMap = new HashMap<Integer,String>();  
	private static HashMap<Integer,String> spaceHashMap = new HashMap<Integer,String>();  

	private static SerialPort serialPort;
	private static CommPortIdentifier portId;
	private static OutputStream outStream;


	public static void main(String[] args) {  

		openSerialConnection(); 
		initalizeHashMaps(); 
		DateTime dt = new DateTime();  // current time
		int currentHour = dt.getHourOfDay(); // gets hour of day

		while(true)
		{
			currentHour = new DateTime().getHourOfDay();
			if(currentHour>=6 && currentHour< 11)
			{
				runProgram("Sunrise",sunriseHashMap);
				currentHour = new DateTime().getHourOfDay();
			}
			if(currentHour>=11 && currentHour< 16)
			{
				runProgram("MidDay",daytimeHashMap);
				currentHour = new DateTime().getHourOfDay();
			}
			if(currentHour>=16 && currentHour< 19)
			{
				runProgram("Sunset",sunsetHashMap);
				currentHour = new DateTime().getHourOfDay();
			}
			if(currentHour>=19 && currentHour< 22)
			{
				runProgram("Night",nightHashMap);
				currentHour = new DateTime().getHourOfDay();
			}
			if(currentHour>=22 || currentHour< 6)
			{
				runProgram("Space",spaceHashMap);
				currentHour = new DateTime().getHourOfDay();
			}
			/*
	 Demo ALL PICTURES
	 runProgram("Sunrise",sunriseHashMap);
	 runProgram("MidDay",daytimeHashMap);
	 runProgram("Sunset",sunsetHashMap);
	 runProgram("Night",nightHashMap);
	 runProgram("Space",spaceHashMap);
			 */
		}
	}


	/**
	 * @param timeOfDay: Determined what set of photos will be used
	 * @param currentTimeOfDayHM Set of photos
	 * Goes through a set of photos and changes background after a certain time
	 */
	public static void runProgram(String timeOfDay, HashMap<Integer, String> currentTimeOfDayHM)
	{

		for(int i = 1; i<=currentTimeOfDayHM.size(); i++)
		{
			try {
				Thread.sleep(30000); //30 second delay between photo changes             
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			User32.INSTANCE.SystemParametersInfo(0x0014, 0, "C:\\BackgroundImages\\"+timeOfDay+"\\"+i+".jpg" , 1);
			try {
				Thread.sleep(500);  //Makes up for a delay in LED light change and Desktop background              
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			try {

				outStream.write(currentTimeOfDayHM.get(i).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Populates all Hashmaps with a hex code associated to a corresponding image.
	 * Windows 10 does not currently allow for user to programmatically obtain current accent theme color hex.
	 */
	public static void initalizeHashMaps()
	{
		sunriseHashMap.put(1,"#cc5b29");  
		sunriseHashMap.put(2,"#ccb029");  
		sunriseHashMap.put(3,"#cc7629"); 
		sunriseHashMap.put(4,"#2986cc"); 
		sunriseHashMap.put(5,"#2966cc"); 
		sunriseHashMap.put(6,"#cc9329"); 
		sunriseHashMap.put(7,"#295bcc"); 
		sunriseHashMap.put(8,"#ccb429"); 

		daytimeHashMap.put(1,"#ccbf29");  
		daytimeHashMap.put(2,"#bccc29");  
		daytimeHashMap.put(3,"#298fcc"); 
		daytimeHashMap.put(4,"#c3cc29"); 
		daytimeHashMap.put(5,"#29b8cc"); 
		daytimeHashMap.put(6,"#2992cc"); 
		daytimeHashMap.put(7,"#cc8f29"); 
		daytimeHashMap.put(8,"#2992cc"); 

		sunsetHashMap.put(1,"#cca029");  
		sunsetHashMap.put(2,"#cc6629");  
		sunsetHashMap.put(3,"#cc3c29"); 
		sunsetHashMap.put(4,"#cc8729"); 
		sunsetHashMap.put(5,"#cc6429"); 
		sunsetHashMap.put(6,"#cc3929"); 

		nightHashMap.put(1,"#2942cc");  
		nightHashMap.put(2,"#b342cc");  
		nightHashMap.put(3,"#cc8a29"); 
		nightHashMap.put(4,"#cc8729"); 
		nightHashMap.put(5,"#cc6629"); 

		spaceHashMap.put(1,"#cc4d29");  
		spaceHashMap.put(2,"#28dbe2");  
		spaceHashMap.put(3,"#e732a7"); 
		spaceHashMap.put(4,"#cc2939"); 
		spaceHashMap.put(5,"#de355f"); 
		spaceHashMap.put(6,"#2987cc"); 


	}


	/**
	 * Opens Serial Connection on COM3
	 * Change COM to COM Arduino is on
	 */
	public static void openSerialConnection(){
		try {
			portId =  CommPortIdentifier.getPortIdentifier("COM3");
			serialPort =  (SerialPort) portId.open("Current Connection:", 5000);
			outStream = serialPort.getOutputStream();
		} 
		catch (NoSuchPortException | PortInUseException | IOException e) {
			e.printStackTrace();
		}

	}
	
	

	/**
	 * @author Soneer Sainion
	 * This portion of code is based on Mark Peters from stackoverflow.com
	 *http://stackoverflow.com/questions/4750372/can-i-change-my-windows-desktop-wallpaper-programmatically-in-java-groovy
	 */
	public static interface User32 extends Library {
		User32 INSTANCE = (User32) Native.loadLibrary("user32",User32.class,W32APIOptions.DEFAULT_OPTIONS);        
		boolean SystemParametersInfo (int one, int two, String s ,int three);         
	}

}