package apache.parser.baseline.supervised;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

import java.util.LinkedHashMap;
import java.util.Map;


public class ApacheBaselineSupervised {
	static Set<String> classesInTrueLinks = new HashSet<String>();
	static Set<String> classesInProjects = new HashSet<String>();
	static Set<String> classesInCommon = new HashSet<String>();
	static Stemmer stemmer = new Stemmer();
	static int reqIDIndex = 0;
	static int titleIndex = 3;
	static int descriotionIndex = 4;
	
	static int nodeIndex = 0;
	static int methodIndex = 1;
	static int classIndex = 2;
	
	
	static int reqSize = 0;
	static int classesSize = 0;
	
	static ArrayList<String> stopwords = new ArrayList<String>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		System.out.println("Initialization...");
		Map<String, String> req = new LinkedHashMap<String, String>();
		Map<String, ArrayList<String>> class_methodDesc_map = new LinkedHashMap<String, ArrayList<String>>();
		Map<String, Set<String>> req_class_map = new LinkedHashMap<String, Set<String>>(); // store the mapping between req and classes
		
		BufferedReader br = new BufferedReader(new FileReader(new File("stopwords.txt")));
		String line;
		while((line = br.readLine()) != null){
			stopwords.add(stem(line));
		}
		br.close();
		
		//generate a map storing reqID and terms
		CSVReader reader = new CSVReader(new FileReader("req.csv"));
	    String [] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	    	String reqID = nextLine[reqIDIndex];
	        String summary = nextLine[titleIndex];
	        String description = nextLine[descriotionIndex];
	        
	        //Stem terms in summary
	        summary = stemSentence(summary);  
	        description = stemSentence(description);
	        //req.put(reqID, summary);
	        String content = summary + " " + description;
	        String[] splitContent = content.split("\\s+");
	        String stopWordRemovedContent = "";
	        for(String s : splitContent){
	        	if(stopwords.contains(s)) continue;
	        	if(isAlpha(s) == false) continue;
	        	stopWordRemovedContent = stopWordRemovedContent + s + " ";
	        }
	        req.put(reqID, stopWordRemovedContent);
	        
	        Set<String> modifiedFiles = new HashSet<String>();
	        String classesPath = nextLine[5];
	        String[] split = classesPath.split(";");
	        for(String c : split){
	        	String filename = "";
	        	if(c.lastIndexOf("/") != -1 )
	        		filename = c.substring(c.lastIndexOf("/")+1);
	        	else
	        		filename = c;
	        	if(filename.endsWith(".java")) classesInTrueLinks.add(filename);
	        	modifiedFiles.add(filename);
	        }
	        req_class_map.put(reqID, modifiedFiles);
	    }
	    
	    reqSize = req.keySet().size();
	    
	    //generate a map storing classID and the methods  
	    Map<String, ArrayList<String>> classes_codeNode = getClassFeatureMap("class_codeNode.csv");
	    Map<String, ArrayList<String>> classes_methodDesc = getClassFeatureMap("class_methodDesc.csv");
	    Map<String, ArrayList<String>> final_feature_map = new LinkedHashMap<String, ArrayList<String>>();
	    
	    for(String key : classes_codeNode.keySet()){
	    	if(classes_methodDesc.keySet().contains(key)){
	    		ArrayList<String> combined = new ArrayList<String>();
	    		combined.addAll(classes_codeNode.get(key));
	    		combined.addAll(classes_methodDesc.get(key));
	    		final_feature_map.put(key, combined);
	    	}
	    }
	    classesSize = final_feature_map.keySet().size();
	    
	    //System.out.println("Project class size: " + classesInProjects.size());
	   // System.out.println("true link class size: " + classesInTrueLinks.size());
	    classesInProjects.retainAll(classesInTrueLinks);
	    classesInCommon = classesInProjects;
	    
	    String root = "src";
	    CodeParser codeparser = new CodeParser(classesInCommon, root);
	    System.out.println("Parsing the source code...");
	    Map<String, Set<String>> api = codeparser.getIncludedClasses(); // get API used in this file
	   
	 
	    FileWriter fw1 = new FileWriter("api.txt");
		for(String key : api.keySet()){
			fw1.write(key + ": ");
			Set<String> topwords = api.get(key);
			for(String w : topwords){
				fw1.write(w + ",");
			}
			fw1.write("\n");
		}
		fw1.flush();
		fw1.close();
		
	    //System.out.println("Classes in common: " + classesInProjects.size());
	    writeFiles(req, final_feature_map, req_class_map);
	    
	    System.out.println("Generating word list...");
	    WordList wl = new WordList();
		ArrayList<String> wordlist = wl.getAllTerms();
		
		System.out.println("Generating truelinks...");
	    TrueLink truelink = new TrueLink();
		int[][] trueLinkMatrix = truelink.getTrueLink(reqSize, classesSize);
		
		System.out.println("Generating TFIDF matrix of requirements...");
	    TFIDF tfidf = new TFIDF(reqSize + classesSize, wordlist);
		LinkedHashMap<String, ArrayList<double[]>> highTFIDF = tfidf.getHighTFIDF(reqSize);
		//LinkedHashMap<String, ArrayList<double[]>> lowTFIDF = tfidf.getLowTFIDF(classesSize);
		
		System.out.println("Getting the top terms of requirements...");
		LinkedHashMap<String, ArrayList<String>> reqTopWords = tfidf.getTopWords(5, highTFIDF);
		FileWriter fw = new FileWriter("topwords.txt");
		for(String key : reqTopWords.keySet()){
			fw.write(key + ": ");
			ArrayList<String> topwords = reqTopWords.get(key);
			for(String w : topwords){
				fw.write(w + ",");
			}
			fw.write("\n");
		}
		fw.flush();
		fw.close();
		
		Feature feature = new Feature(reqTopWords, api);
		//LinkedHashMap<String, ArrayList<String>> lowTopWords = tfidf.getTopWords(10, lowTFIDF);
		
		

	}
	
	public static boolean isAlpha(String name) {
	    char[] chars = name.toCharArray();

	    for (char c : chars) {
	        if(!Character.isLetter(c)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	private static Map<String, ArrayList<String>> getClassFeatureMap(String filename) throws IOException{
			CSVReader reader = new CSVReader(new FileReader(filename));
		    Map<String, ArrayList<String>> classes = new LinkedHashMap<String, ArrayList<String>>();
		    String[] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		    	String codeNodes = nextLine[nodeIndex];   	
		    	String className = nextLine[classIndex];
		        String methodName = nextLine[methodIndex];
		       
		        ArrayList<String> nodeTerms = new ArrayList<String>();
		        ArrayList<String> nodeTermsSplitted = new ArrayList<String>();
		        codeNodes = codeNodes.replaceAll("[^a-zA-Z0-9]", " ");
		        //System.out.println(codeNodes);
		        String[] identifiers = codeNodes.split("\\s+");
		        for(String i : identifiers){
		        	nodeTerms.add(i);
		        	//System.out.println(i);
		        }
		        
		        for(String term : nodeTerms){
		        	nodeTermsSplitted.addAll(parseMethod(term));
		        	//System.out.println(term + ": " + parseMethod(term));
		        }
		        
		        
		        for(int i = 0; i < nodeTermsSplitted.size(); i++){
		        	String beforeStem = nodeTermsSplitted.get(i);
		        	String afterStem = stem(beforeStem.toLowerCase());
		        	nodeTermsSplitted.set(i, afterStem);
		        }
		        
		        ArrayList<String> methodTerms = new ArrayList<String>();
		        methodTerms = parseMethod(methodName);
		       
		        //Stem all terms 
		        for(int i = 0; i < methodTerms.size(); i++){
		        	String beforeStem = methodTerms.get(i);
		        	String afterStem = stem(beforeStem);
		        	methodTerms.set(i, afterStem);
		        }
		        
		        if(classes.keySet().contains(className)){
		        	ArrayList<String> update = classes.get(className);
		        	update.addAll(nodeTermsSplitted);
		        	update.addAll(methodTerms);
		        	classes.put(className, update);
		        }else{
		        	methodTerms.addAll(nodeTermsSplitted);
		        	ArrayList<String> update = new ArrayList<String>(methodTerms);
		        	classes.put(className, update);
		        }
		        classesInProjects.add(className);
		    }
		    return classes;
	}
	
	
	private static ArrayList<String> parseMethod(String method){
		ArrayList<String> methodterms = new ArrayList<String>();
		int startIndex = 0;
		int endIndex = 0;
		for(int index = 0; index < method.length(); index++){
			if(Character.isUpperCase(method.charAt(index)) ){
				startIndex = endIndex;
				endIndex = index;
				String term = method.substring(startIndex, endIndex);
				//System.out.println(term);
				methodterms.add(term);
			}
			if(index == method.length() - 1){
				startIndex = endIndex;
				endIndex = index;
				String term = method.substring(startIndex, endIndex+1);
				//System.out.println(term);
				methodterms.add(term);
			}
		}
		
		return methodterms;
	}
	
	private static void writeFiles(Map<String, String> req, Map<String, ArrayList<String>> classes, Map<String, Set<String>> mapping) throws IOException{
		FileWriter fw1 = new FileWriter("req.txt");
		for(String key : req.keySet()){
			fw1.write(key + ">>>" + req.get(key).toLowerCase() + "\n");
		}
		fw1.flush();
		fw1.close();
		
		FileWriter fw2 = new FileWriter("classes.txt");
		for(String key : classes.keySet()){
			ArrayList<String> methodTerms = classes.get(key);
			String terms = "";
			for(String t : methodTerms){
				terms = terms + t + " ";
			}
			if(classesInCommon.contains(key)){
				fw2.write(key + ">>>" + terms.toLowerCase() + "\n");
			}
		}
		fw2.flush();
		fw2.close();
		
		FileWriter fw3 = new FileWriter("truelinks.txt");
		for(String key : mapping.keySet()){
			Set<String> files = mapping.get(key);
			
			fw3.write("%\n");
			fw3.write(key + "   ");
			for(String f : files){
				if(!f.endsWith(".java") || !classesInCommon.contains(f)) continue;
				fw3.write(f + "   ");
			}
			fw3.write("\n");
		}
		fw3.flush();
		fw3.close();
	}
	

	public static void compareLink(int[][] predictedMatrix, int[][] trueMatrix, int highSize, int lowSize){
		double tp = 0;
		double fp = 0;
		double fn = 0;
		//int a = 0;
		for(int i = 0; i < highSize; i++){
			for(int j = 0; j < lowSize; j++){
				//if(trueMatrix[i][j] ==1) System.out.println(a++);
				if(predictedMatrix[i][j] >= 1 && trueMatrix[i][j] == 1) tp++;
				if(predictedMatrix[i][j] >= 1 && trueMatrix[i][j] == 0) fp++;
				if(predictedMatrix[i][j] == 0 && trueMatrix[i][j] == 1) fn++;
			}
		}
		System.out.println(tp + " " + fp + " " + fn);
		//double recall = 0;
		//double precision = 0;
		//double fscore = 0;
		//if(tp != 0){
			double recall = tp / (tp + fn);
			double precision = tp / (tp + fp);
			double fscore = 2*recall*precision / (precision + recall);
		//}
		 
		System.out.printf("Recall: %.2f Precision: %.2f Fscore: %.2f\n", recall, precision, fscore);
		
	}
	
	private static String stem(String term){
		char[] chars = term.toCharArray();
		for (char c : chars)
			stemmer.add(c);
		stemmer.stem();
		String stemmed = stemmer.toString();
		return stemmed;
	}
	
	private static String stemSentence(String sentence){
		String summaryAfterStem = "";
        String[] split = sentence.split("\\s+");
        for(String s : split){
        	String stemmed = stem(s);
        	summaryAfterStem = summaryAfterStem + stemmed + " ";
        }
        return summaryAfterStem;
	}

}
