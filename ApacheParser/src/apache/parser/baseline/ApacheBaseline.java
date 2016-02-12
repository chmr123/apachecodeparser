package apache.parser.baseline;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;





import com.opencsv.CSVReader;

public class ApacheBaseline {
	static Set<String> classesInTrueLinks = new HashSet<String>();
	static Set<String> classesInProjects = new HashSet<String>();
	static Set<String> classesInCommon = new HashSet<String>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int reqIDIndex = 0;
		int titleIndex = 3;
		int methodIndex = 1;
		int classIndex = 2;
		
		int reqSize = 0;
		int classesSize = 0;
		Map<String, String> req = new LinkedHashMap<String, String>();
		Map<String, Set<String>> classes = new LinkedHashMap<String, Set<String>>();
		Map<String, Set<String>> req_class_map = new LinkedHashMap<String, Set<String>>();
		
		
		
		//generate a map storing reqID and terms
		CSVReader reader = new CSVReader(new FileReader("req.csv"));
	    String [] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	    	String reqID = nextLine[reqIDIndex];
	        String summary = nextLine[titleIndex];
	        req.put(reqID, summary);
	        
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
	    reader = new CSVReader(new FileReader("classes.csv"));
	    while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	    	String className = nextLine[classIndex];
	        String methodName = nextLine[methodIndex];
	        ArrayList<String> methodTerms = new ArrayList<String>();
	        methodTerms = parseMethod(methodName);
	        if(classes.keySet().contains(className)){
	        	Set<String> update = classes.get(className);
	        	update.addAll(methodTerms);
	        	classes.put(className, update);
	        }else{
	        	Set<String> update = new HashSet<String>(methodTerms);
	        	classes.put(className, update);
	        }
	        classesInProjects.add(className);
	    }
	    
	    classesSize = classes.keySet().size();
	    
	    //System.out.println("Project class size: " + classesInProjects.size());
	   // System.out.println("true link class size: " + classesInTrueLinks.size());
	    classesInProjects.retainAll(classesInTrueLinks);
	    classesInCommon = classesInProjects;
	    //System.out.println("Classes in common: " + classesInProjects.size());
	    writeFiles(req, classes, req_class_map);
	    
	    TrueLink truelink = new TrueLink();
		int[][] trueLinkMatrix = truelink.getTrueLink(reqSize, classesSize);
		
	    TFIDF tfidf = new TFIDF(reqSize + classesSize);
		LinkedHashMap<String, ArrayList<Double>> highTFIDF = tfidf.getHighTFIDF(reqSize);
		LinkedHashMap<String, ArrayList<Double>> lowTFIDF = tfidf.getLowTFIDF(classesSize);
		
		//BigramTFIDF bigramtfidf = new BigramTFIDF(highSize + lowSize);
		//LinkedHashMap<String, ArrayList<Double>> bigram_highTFIDF = bigramtfidf.getHighTFIDF(highSize);
		//LinkedHashMap<String, ArrayList<Double>> bigram_lowTFIDF = bigramtfidf.getLowTFIDF(lowSize);
		
		
	
		double[][] tfidfSimMatrix = new double[reqSize][classesSize];
		double[][] tfidfSimMatrixBi = new double[reqSize][classesSize];
		
		int highIndex = 0;
		for(String high_key : highTFIDF.keySet()){
			int lowIndex = 0;
			for(String low_key : lowTFIDF.keySet()){
				System.out.print("Calculating " + highIndex + " / " +  + reqSize + "\r");
				double cosSim = getSimilarity(highTFIDF.get(high_key), lowTFIDF.get(low_key));
				//double cosSimBi = getSimilarity(bigram_highTFIDF.get(high_key), bigram_lowTFIDF.get(low_key));
				//double functionSim = functionMatrix[highIndex][lowIndex];
				//double sim = cosSim > functionSim ? cosSim : functionSim;
				//double sim = Math.sqrt(cosSim * functionSim);
				tfidfSimMatrix[highIndex][lowIndex] = cosSim;
				//tfidfSimMatrixBi[highIndex][lowIndex] = cosSimBi;
				lowIndex++;
			}
			highIndex++;
		}
		
		for(double threshold = 0.1; threshold < 0.9; threshold += 0.1){
			System.out.println("Threshold: " + threshold);
			int[][] predictedLinkMatrix = new int[reqSize][classesSize];
			for(int high = 0; high < highTFIDF.keySet().size(); high++){
				for(int low = 0; low < lowTFIDF.keySet().size(); low++){
					if(tfidfSimMatrix[high][low] >= threshold || tfidfSimMatrixBi[high][low] >= threshold)
						predictedLinkMatrix[high][low] = 1;
				}
			}
			compareLink(predictedLinkMatrix, trueLinkMatrix, reqSize, classesSize);
			//getPairs(predictedLinkMatrix, highTFIDF, lowTFIDF, threshold);
			//DiffAR diffar = new DiffAR(highTFIDF, lowTFIDF);
			//System.out.println("DiffAR: " + diffar.getDiffAR(predictedLinkMatrix, trueLinkMatrix, highSize, lowSize));
			System.out.println();
		}
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
	
	private static void writeFiles(Map<String, String> req, Map<String, Set<String>> classes, Map<String, Set<String>> mapping) throws IOException{
		FileWriter fw1 = new FileWriter("req.txt");
		for(String key : req.keySet()){
			fw1.write(key + ">>>" + req.get(key).toLowerCase() + "\n");
		}
		fw1.flush();
		fw1.close();
		
		FileWriter fw2 = new FileWriter("classes.txt");
		for(String key : classes.keySet()){
			Set<String> methodTerms = classes.get(key);
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
	

	public static double getSimilarity(ArrayList<Double> high, ArrayList<Double> low){
		double up = 0;
		for(int i = 0; i < high.size(); i++){
			up = up + high.get(i) * low.get(i);
		}
		
		double down1 = 0;
		double down2 = 0;
		for(int i = 0; i < high.size(); i++){
			down1 = down1 + Math.pow(high.get(i), 2);
			down2 = down2 + Math.pow(low.get(i), 2);
		}
		
		double sim = up / (Math.sqrt(down1) * Math.sqrt(down2));
		return sim;
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

}