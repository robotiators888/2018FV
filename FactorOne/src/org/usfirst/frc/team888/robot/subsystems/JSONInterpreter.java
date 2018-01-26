package org.usfirst.frc.team888.robot.subsystems;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Scanner;

import org.json.*;
import org.usfirst.frc.team888.robot.RobotMap;

public class JSONInterpreter {

	//Name of each category in the JSON file to be read from.
	String[] categories = {"drivetrain", "manipulator", "driverstation", "climber", "diagnostics"};
	
	File src = new File("/home/lvuser/RobotData.json");
	String robotDataFile;
	JSONObject data, workingSet;

	Object current;
	
	Field[] vars;
	String[] varNames;
	
	/**
	 * This method should be called inside of a synchronized lock at the beginning of robotInit()
	 * to update all the RobotMap values based on the JSON file.
	 */
	public void interpret() {
		vars = RobotMap.class.getDeclaredFields(); //Gets references to every variable in RobotMap.
		varNames = new String[vars.length];
		for(int i = 0; i < vars.length; i++) varNames[i] = camelToLower(vars[i].getName()); //Reads what the JSON values shoulf be called
		
		//Reads the entire JSON file into memory.
		try {
			Scanner scan = new Scanner(src);
			robotDataFile = scan.useDelimiter("\\Z").next();
			scan.close();
			
			data = new JSONObject(robotDataFile);
		} catch (FileNotFoundException | JSONException e) {
			e.printStackTrace();
		}
		
		//Iterates through every category, copying any values that match a variable into RobotMap.
		for(String cat : categories) {
			String key = "";
			try {
				workingSet = data.getJSONObject(cat);
				Iterator<?> iter = workingSet.keys();
				while(iter.hasNext()) {
					key = (String) iter.next();
					current = workingSet.get(key);
					int i = this.indexOf(varNames, key);
					
					if(i > -1) vars[i].set(RobotMap.class, current);
				}
			} catch (JSONException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * Walks the array of Strings and returns the index of the given String if found in the array
	 * @param names The array of Strings to search.
	 * @param toFind The target String to find.
	 * @return The index of the exact match if one is found, -1 if otherwise.
	 */
	private int indexOf(String[] names, String toFind) {
		for(int i = 0; i < names.length; i++) if(toFind.equals(names[i])) return i;
		
		return -1;
	}
	
	/**
	 * Converts the constant name to the appropriate JSON value name.
	 * @param str Constant name to convert.
	 * @return Converted name in camel case.
	 */
	private String camelToLower(String str) {
		String newStr = str.toLowerCase();
		int i;
		while((i = newStr.indexOf("_")) > -1) {
			newStr = newStr.substring(0, i) + Character.toUpperCase(newStr.charAt(i + 1)) 
				+ newStr.substring(i + 2);
		}
		return newStr;
	}
	
}

