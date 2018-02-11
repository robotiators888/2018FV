package org.usfirst.frc.team888.robot.subsystems;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import org.json.*;
import org.usfirst.frc.team888.robot.RobotMap;

public class JSONInterpreter {
	
	JSONObject scenarios;
	
	public JSONInterpreter() {
		readWaypoints();
		scenarios = readScenarios();
	}
	
	/**
	 * This method should be called inside of a synchronized lock at the beginning of robotInit()
	 * to update all the RobotMap values based on the JSON file.
	 */
	public void interpretRobotData() {
		//Name of each category in the JSON file to be read from.
		String[] categories = {"drivetrain", "manipulator", "driverstation", "climber", "diagnostics"};
		
		File src = new File("/home/lvuser/RobotData.json");
		String robotDataFile;
		JSONObject data = null, workingSet;

		Object current;
		
		Field[] vars;
		String[] varNames;
		
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
					int i = indexOf(varNames, key);
					
					if(i > -1) vars[i].set(RobotMap.class, current);
				}
			} catch (JSONException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * Reads the waypoint map (Java version of a python dictionary) into memory.
	 */
	@SuppressWarnings("static-method")
	private void readWaypoints() {
		File src = new File("/home/lvuser/waypoints.json");
		JSONObject way;
		
		try {
			//Reads the entire JSON file into memory.
			Scanner scan = new Scanner(src);
			way = new JSONObject(scan.useDelimiter("\\Z").next());
			scan.close();
		
			//Initializes the map, then adds each value in the JSON file to the map.
			RobotMap.waypoints = new HashMap<String, double[]>();
			Iterator<?> iter = way.keys();
			while(iter.hasNext()) {
				String key = (String) iter.next();
				double[] value = new double[6];
				JSONArray jValue = new JSONArray(way.get(key));
				for(int i = 0; i < 6; i++) value[i] = jValue.getDouble(i);
				RobotMap.waypoints.put(key, value);
			}
		} catch (FileNotFoundException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the Scenarios file.
	 * @return A JSONObject representation of the data within scenarios.json, or null if it failed.
	 */
	@SuppressWarnings("static-method")
	private JSONObject readScenarios() {
		File src = new File("/home/lvuser/scenarios.json");
		JSONObject scene;
		
		try {
			//Reads the entire JSON file into memory.
			Scanner scan = new Scanner(src);
			scene = new JSONObject(scan.useDelimiter("\\Z").next());
			scan.close();
			
			return scene; //Returns the read JSON file.
		} catch (FileNotFoundException | JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Grabs a specific scenario from the list of scenarios, then destructs the list to free up memory.
	 * @param fieldConfiguration The match's field configuration.
	 * @param startPos The robot's start position.
	 * @return The scenario for the given field configuration and start position.
	 */
	public Queue<String> getScenario(String fieldConfiguration, String startPos) {
		LinkedList<String> scenarioKeys = new LinkedList<String>(); //Make a queue (LinkedList implementation).
		
		try {
			JSONObject o = scenarios.getJSONObject(fieldConfiguration); //Get the JSONObject for the given field configuration.
			JSONArray a = o.getJSONArray(startPos); //Get the waypoint key list (scenario) for the given start position.
			for(int i = 0; i < a.length(); i++) scenarioKeys.add(a.getString(i)); //Add the keys into the queue.
			scenarios = null; //Destruct the scenarios object.
			return scenarioKeys;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Walks the array of Strings and returns the index of the given String if found in the array
	 * @param names The array of Strings to search.
	 * @param toFind The target String to find.
	 * @return The index of the exact match if one is found, -1 if otherwise.
	 */
	private static int indexOf(String[] names, String toFind) {
		for(int i = 0; i < names.length; i++) if(toFind.equals(names[i])) return i;
		
		return -1;
	}
	
	/**
	 * Converts the constant name to the appropriate JSON value name.
	 * @param str Constant name to convert.
	 * @return Converted name in camel case.
	 */
	private static String camelToLower(String str) {
		String newStr = str.toLowerCase();
		int i;
		while((i = newStr.indexOf("_")) > -1) {
			newStr = newStr.substring(0, i) + Character.toUpperCase(newStr.charAt(i + 1)) 
				+ newStr.substring(i + 2);
		}
		return newStr;
	}
	
}

