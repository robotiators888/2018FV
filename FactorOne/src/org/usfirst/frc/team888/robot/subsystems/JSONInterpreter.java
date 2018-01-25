package org.usfirst.frc.team888.robot.subsystems;

import java.lang.reflect.Field;

import org.usfirst.frc.team888.robot.RobotMap;

public class JSONInterpreter {

	Field[] vars;
	String[] varNamesJSON, varNames;
	
	public JSONInterpreter() {
		vars = RobotMap.class.getDeclaredFields();
		varNames = new String[vars.length];
		for(int i = 0; i < vars.length; i++) varNames[i] = vars[i].getName();
		for(int i = 0; i < varNames.length; i++) varNamesJSON[i] = camelToLower(varNames[i]);
		
		
	}
	
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
