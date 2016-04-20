package apache.parser.baseline.supervised;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Feature {
	ArrayList<String> allfeatures = new ArrayList<String>();
	public Feature(Map<String, ArrayList<String>> req, Map<String, LinkedHashSet<String>> api){
		Set<String> terms = new LinkedHashSet<String>();
		Set<String> classes = new LinkedHashSet<String>();
		System.out.println("Generating features...");
		for(String req_key : req.keySet()){
			ArrayList<String> topwords = req.get(req_key);
			for(String term : topwords){
				terms.add(term);
			}		
		}
		
		for(String api_key : api.keySet()){
			Set<String> apis = api.get(api_key);
			for(String a : apis){
				classes.add(a);
			}		
		}
		
		for(String f1 : terms){
			for(String f2 : classes){
				String pair = f1 + "_" + f2;
				allfeatures.add(pair);
			}
		}
		
		System.out.println(allfeatures.size() + " features generated...");
	}
	
	public ArrayList<String> getFeatureVector(){
		return allfeatures;
	}
}
