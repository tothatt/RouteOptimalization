package hu.emarsys.interview;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main {
	static List<String> route = new ArrayList<>();
	static Map<String, List<String>> destinations = new HashMap<>();

	public static void main(String[] args) {
		try {
			// TODO Auto-generated method stub
			readFromJson(args[0]);
			for (Iterator<String> iterator = destinations.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				if (destinations.get(key).isEmpty()) {
					route.add(key);
					iterator.remove();
				}
			}
			int mapSize = destinations.size();
			do {
				mapSize = destinations.size();
				for (Iterator<String> iterator = destinations.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					List<String> deps = destinations.get(key);
					boolean canBeNext = true;
					for (String dep : deps) {
						if (!route.contains(dep))
							canBeNext = false;
					}
					if (canBeNext) {
						route.add(key);
						iterator.remove();
					}
				}
				if (mapSize == destinations.size() && mapSize != 0)
					throw new SimpleException("There is no possible route");
			} while (mapSize != 0);
			StringBuffer bf = new StringBuffer();
			for (String route : route)
				bf.append(route);
			System.out.println("The route can be:" + bf.toString());
		} catch (SimpleException e) {
			System.out.println("Something went wrong:" + e.getMessage());
		}

	}

	private static void readFromJson(String inputFileName) {
		JSONParser parser = new JSONParser();
		JSONObject inputJson = null;
		try {
			inputJson = (JSONObject) parser.parse(new FileReader(inputFileName));

			JSONArray inputArray = (JSONArray) inputJson.get("input");
			
			if(inputArray == null)
				throw new SimpleException("Invalid json input");
			
			for (Object destinationObject : inputArray) {
				JSONObject destination = (JSONObject) destinationObject;
				String destinationName = (String) destination.get("left");
				if(destinationName == null)
					throw new SimpleException("Invalid json input");
				destinations.put(destinationName, new ArrayList<>());
			}
			for (Object destinationObject : inputArray) {
				JSONObject destination = (JSONObject) destinationObject;
			
				String destinationName = (String) destination.get("left");
				String dependencyName = (String) destination.get("right");
				
				if(destinationName == null || dependencyName == null)
					throw new SimpleException("Invalid json input");
				
				if (!"".equals(dependencyName)) {
					if (!destinations.containsKey(dependencyName))
						throw new SimpleException(
								"There is one destination on the right side which is not on the left");
					else {
						if (!destinations.get(destinationName).contains(dependencyName)) {
							if (destinations.get(dependencyName).contains(destinationName))
								throw new SimpleException("Infinite loop! There is no possible route!");
							destinations.get(destinationName).add(dependencyName);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new SimpleException("File not found!");
		} catch (ParseException e) {
			throw new SimpleException("Invalid json input");
		} catch (JSONException e) {
			throw new SimpleException("Invalid json format");
		}
	}

}
