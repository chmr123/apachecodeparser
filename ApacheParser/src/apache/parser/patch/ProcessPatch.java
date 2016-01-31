package apache.parser.patch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProcessPatch {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File[] patches = new File("lucene_patch").listFiles();
		for (File patch : patches) {
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
				System.out.println(flagLine);
				
				if(linenum == (flagLine - 1)){
					
					ArrayList<String> addArray = new ArrayList<String>();
					addArray.addAll(temp);
					addedLines.add(addArray);
					temp.clear();
					lineIndex++;
				}
				linenum++;
			}
			
			addedLines.remove(0);
			for(String file : indexFiles){
				System.out.println(file);
			}
			
			for(ArrayList<String> array : addedLines){
				System.out.println(array);
			}
				/*if (line.startsWith("Index:")) {
					ArrayList<String> addArray = new ArrayList<String>();
					addArray.addAll(temp);
					addedLines.add(addArray);
					temp.clear();

					String filename = getIndexFile(line);
					indexFiles.add(filename);
				}

				if (line.startsWith("+") && !line.startsWith("+++")) {
					temp.add(line.replaceFirst("\\+", "").trim());
				}
			}	
			
			*/
		}
		
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

}
