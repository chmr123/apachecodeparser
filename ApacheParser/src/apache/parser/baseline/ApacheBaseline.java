package apache.parser.baseline;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApacheBaseline {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int reqIDIndex = 0;
		int titleIndex = 1;
		int methodIndex = 0;
		int classIndex = 0;
		
		
		Map<String, String> req = new LinkedHashMap<String, String>();
		Map<String, ArrayList<String>> classes = new LinkedHashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> req_class_map = new LinkedHashMap<String, ArrayList<String>>());
		
		CSVReader reader = new CSVReader(new FileReader("req.csv"));
	    String [] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	    	String reqID = nextLine[reqIDIndex];
	        String summary = nextLine[titleIndex];
	        req.put(reqID, summary);
	    }
	    
	    reader = new CSVReader(new FileReader("classes.csv"));
	    ArrayList<String> sameClassMethods = new ArrayList<String>();
	    while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	    	String className = nextLine[classIndex];
	        String methodName = nextLine[methodIndex];
	        ArrayList<String> methodTerms = new ArrayList<String>();
	        methodTerms = parseMethod(methodName);
	        if(classes.keySet().contains(className)){
	        	ArrayList<String> update = classes.get(className);
	        	update.addAll(methodTerms);
	        	classes.put(className, update);
	        }else{
	        	classes.put(className, methodTerms);
	        }
	    }
	    
	    reader = new CSVReader(new FileReader("req_class.csv"));
	    while ((nextLine = reader.readNext()) != null) {
	    	String reqID = nextLine[0];
	    	String classesPath = nextLine[1];
	    }
		//parseMethod("setDateAndYear");
	}
	
	
	private static ArrayList<String> parseMethod(String method){
		ArrayList<String> methodterms = new ArrayList<String>();
		boolean capitalFound = false;
		int startIndex = 0;
		int endIndex = 0;
		for(int index = 0; index < method.length(); index++){
			if(Character.isUpperCase(method.charAt(index)) ){
				startIndex = endIndex;
				endIndex = index;
				String term = method.substring(startIndex, endIndex);
				System.out.println(term);
				methodterms.add(term);
			}
			if(index == method.length() - 1){
				startIndex = endIndex;
				endIndex = index;
				String term = method.substring(startIndex, endIndex+1);
				System.out.println(term);
				methodterms.add(term);
			}
		}
		
		return methodterms;
	}

}
