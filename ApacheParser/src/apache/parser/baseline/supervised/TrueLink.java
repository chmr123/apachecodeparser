package apache.parser.baseline.supervised;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrueLink {
	ArrayList<String> allLinks = new ArrayList<String>();
	Set<String> classes = new LinkedHashSet<String>();
	ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
	public int[][] getTrueLink(int highSize, int lowSize) throws IOException{
		
		int[][] truelink = new int[highSize][lowSize];
		ArrayList<String> lowfilenames = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("classes.txt"));
		String line;
		while((line = br.readLine()) != null){
			String name = line.split(">>>")[0];
			lowfilenames.add(name);
			//System.out.println(name);
		}
		int numTrueLink = 0;
		br = new BufferedReader(new FileReader("truelinks.txt"));
		int highIndex = 0;
		while((line = br.readLine()) != null){
			
			if(!line.contains("%")){
				ArrayList<String> onelink = new ArrayList<String>();
				String[] links = line.split("\\s+");
				String reqName = links[0];
				if(links.length > 1){
					for(int i = 1; i < links.length; i++){
						onelink.add(links[i]);
						allLinks.add(reqName + "_" + links[i]);
						classes.add(links[i]);
						for(int lowIndex = 0; lowIndex < lowfilenames.size(); lowIndex++){	
							//System.out.println(links[i].toLowerCase());
							if(links[i].equals(lowfilenames.get(lowIndex))) {
								
								truelink[highIndex][lowIndex] = 1;
								//System.out.println("Add link");
								numTrueLink++;
							}
						}
					}
					lines.add(onelink);
				}
				highIndex++;
			}
		}
		System.out.println("Number of links: " + numTrueLink);
		statistics();
		return truelink;
	}
	
	public ArrayList<String> getLinks(){
		return allLinks;
	}
	
	public Map<String, Integer> statistics() throws IOException{
		Map<String, Integer> linkstat = new LinkedHashMap<String, Integer>();
		FileWriter fw = new FileWriter("link statistics.txt");
		for(String c : classes){
			int count = 0;
			for(ArrayList<String> line : lines){
				if(line.contains(c)) count++;
			}
			linkstat.put(c, count);
			
		}
		linkstat = sortByValue(linkstat);
		for(String key : linkstat.keySet()){
			int count = linkstat.get(key);
			fw.write(key + ": " + count + "\n");
		}
		fw.flush();
		fw.close();
		return linkstat;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}

