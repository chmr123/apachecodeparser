package apache.parser.jaccard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JaccardSimilarity {

	public static void main(String[] args) throws IOException {
		BufferedReader br1 = new BufferedReader(new FileReader("issuesBestWords.txt"));
		BufferedReader br2 = new BufferedReader(new FileReader("commentBestWords.txt"));
		
		ArrayList<String> issues = new ArrayList<String>();
		ArrayList<String> comments = new ArrayList<String>();
		String line;
		while((line = br1.readLine()) != null){
			issues.add(line);
		}
		
		while((line = br2.readLine()) != null){
			comments.add(line);
		}
		
		LineNumberReader  lnr1 = new LineNumberReader(new FileReader(new File("issuesBestWords.txt")));
		lnr1.skip(Long.MAX_VALUE);
		int linenumber1 = lnr1.getLineNumber() + 1;
		lnr1.close();
		
		LineNumberReader  lnr2 = new LineNumberReader(new FileReader(new File("commentBestWords.txt")));
		lnr2.skip(Long.MAX_VALUE);
		int linenumber2 = lnr2.getLineNumber() + 1;
		lnr2.close();
		
		
		
		Map<String, Double> jaccards = new LinkedHashMap<String, Double>();
		
		int c1 = 0;	
		int c2 = 0;
		Double jaccardIndex = 0.0;
		for(String line1 : issues){
			c1++;
			System.out.println("Processing " + c1);			
			String[] terms1 = line1.split(", ");
			List<String> list1 = Arrays.asList(terms1);
			for(String line2 : comments){
				c2++;
				String[] terms2 = line2.split(", ");			
				List<String> list2 = Arrays.asList(terms2);
				
				double intersection = intersection(list1, list2).size();
				double union = union(list1, list2).size();
				
				
				
				if(union == 0){
					continue;
				}
				jaccardIndex = intersection / union;
				String key = String.valueOf(c1) + String.valueOf(c2);
				jaccards.put(key, jaccardIndex);
			}
		}
		
		jaccards = sortByValue(jaccards);
			
		int count = 0;
		for(String key : jaccards.keySet()){
			count++;
			System.out.println(jaccards.get(key));
			if(count == 20) break;
		}
	}
	
	public static List<String> union(List<String> list1, List<String> list2) {
        Set<String> set = new HashSet<String>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<String>(set);
    }

    public static List<String> intersection(List<String> list1, List<String> list2) {
        List<String> list = new ArrayList<String>();

        for (String t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
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
