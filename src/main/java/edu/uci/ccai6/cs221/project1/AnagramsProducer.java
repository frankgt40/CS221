package edu.uci.ccai6.cs221.project1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnagramsProducer {
	public Map<String, Collection<String>> detectAnagrams(List<String> tokenList) {
		Map<String, Collection<String>> anagramList = new HashMap<String, Collection<String>>();
		
		try {
			//int count = 0;

			for (String token : tokenList) {
				char[] charList = token.toLowerCase().toCharArray();
				Arrays.sort(charList);
				String key = new String(charList);
				if (!anagramList.containsKey(key))
					anagramList.put(key, new ArrayList<String>());
				
				boolean trigger = false;
				// Find whether there is a existing word
				for (String item : anagramList.get(key)) {
					if (item.equals(token.toLowerCase())) 
						trigger = true;
				}
				if (!trigger)
					anagramList.get(key).add(token.toLowerCase());
			//	count = Math.max(count, anagramList.get(key).size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	      
		return anagramList;
	}
	
	
	public void print(Map<String, Collection<String>> anagramList) {
		System.out.println("Part D. The anagrams: ");
		for (Collection<String> anagram : anagramList.values())
			if (anagram.size() >= 2)
				System.out.println(anagram);
	}
	
}
