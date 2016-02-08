package apache.parser.patch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVWriter;

public class ProcessPatch {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String root = args[0];
		File[] patches = new File("patches//lucene").listFiles();
		int count = 0;
		 CSVWriter writer = new CSVWriter(new FileWriter("api.csv"), ',');
	     // feed in your array (or convert your data to an array)
	    // String[] entries = "first#second#third".split("#");
	    // writer.writeNext(entries);
		// writer.close();
		 System.out.println("Getting java classes...");
		ArrayList<String> javaClasses = getJavaClasses();
		System.out.println("Getting lucene classes...");
		ArrayList<String> luceneClasses =  getLuceneClasses(root);
		for (File patch : patches) {
			count++;
			System.out.print("Processing " + count + " / " + patches.length + "\r");
			BufferedReader br = new BufferedReader(new FileReader(patch));
			String line;
			boolean indexFile = false;
			ArrayList<String> indexFiles = new ArrayList<String>();
			ArrayList<ArrayList<String>> addedLines = new ArrayList<ArrayList<String>>();
			ArrayList<String> temp = new ArrayList<String>();
			Map<String, String[]> file_modification = new HashMap<String, String[]>();
			boolean foundIndex = false;
			
			ArrayList<Integer> indexLineNumbers = new ArrayList<Integer>();
			int linenum = 0;
			while ((line = br.readLine()) != null) {
				linenum++;
				if(line.startsWith("Index:") || line.startsWith("diff --git")){
					indexLineNumbers.add(linenum);
					
					String filename = getIndexFile(line);
					indexFiles.add(filename);
				}
			}
			indexLineNumbers.add(linenum);
			
			br = new BufferedReader(new FileReader(patch));
			linenum = 0;
			int lineIndex = 0;
			while((line = br.readLine()) != null){
				if (line.startsWith("+") && !line.startsWith("+++")) {
					temp.add(line.replaceFirst("\\+", "").trim());
				}
				
				int flagLine = indexLineNumbers.get(lineIndex);
				//System.out.println(flagLine);
				
				if(linenum == (flagLine - 1)){
					
					ArrayList<String> addArray = new ArrayList<String>();
					addArray.addAll(temp);
					addedLines.add(addArray);
					temp.clear();
					lineIndex++;
				}
				linenum++;
			}
			
			if(addedLines.size() == 0) continue;
			addedLines.remove(0);
			
			int indexSize = indexFiles.size();
			for(int i = 0; i < indexSize; i++){
				String indexFileName = indexFiles.get(i);
				ArrayList<String> addedCode = addedLines.get(i);
				Set<String> usedAPI = new HashSet<String>();
				for(String s : addedCode){
					Set<String> tempAPI = getUsedClasses(s, javaClasses, luceneClasses);
					usedAPI.addAll(tempAPI);
				}
				StringBuilder sb = new StringBuilder();
				for(String s : usedAPI){
					sb.append(s);
					sb.append(";");
				}
				if(sb.length() > 0){
					sb.deleteCharAt(sb.length() - 1);
				}
				String api = sb.toString();
				
				String[] csvrow = new String[3];
				csvrow[0] = patch.getName();
				csvrow[1] = indexFileName;
				csvrow[2] = api;
				writer.writeNext(csvrow);
			}
		}
		writer.close();
	}

	private static String getIndexFile(String line) {
		String filename = "";
		if(line.startsWith("Index:")){
			filename = line.replace("Index: ", "");
		}
		if(line.startsWith("diff --git")){
			filename = line.split(" ")[2];
			if(filename.startsWith("a/")){
				filename = filename.replaceFirst("a/", "");
			}
		}
		return filename;
	}
	
	private static ArrayList<String> getJavaClasses() throws IOException{
		File file = new File("javaAllClasses.txt");
		ArrayList<String> javaclasses = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null){
			javaclasses.add(line);
		}
		return javaclasses;
	}
	
	private static ArrayList<String> getLuceneClasses(String root){
		ArrayList<String> classes = new ArrayList<String>();	
		getAllFiles(root, classes);
		return classes;
	}
	
	private static void getAllFiles(String root, ArrayList<String> files) {
		File[] rootFolder = new File(root).listFiles();

		for (File f : rootFolder) {
			if (f.isDirectory()) {
				getAllFiles(f.getAbsolutePath(), files);
			}
			if (f.isFile() && f.getName().endsWith(".java")) {
				files.add(f.getName().replace(".java", ""));
			}
		}
	}
	
	private static Set<String> getUsedClasses(String line, ArrayList<String> javaClasses, ArrayList<String> appClasses){
		line = line.replaceAll("[^a-zA-Z0-9]", " ");
		String[] split = line.split("\\s+");
		Set<String> usedClasses = new HashSet<String>();
		for(String s : split){
			if(javaClasses.contains(s)){
				usedClasses.add(s);
			}
			if(appClasses.contains(s)){
				usedClasses.add(s);
			}
		}
		
		return usedClasses;
	}

}
