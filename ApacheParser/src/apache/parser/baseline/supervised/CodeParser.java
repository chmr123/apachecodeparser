package apache.parser.baseline.supervised;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CodeParser {
	Set<String> commonClasses;
	//ArrayList<String> javaClasses;
	ArrayList<String> apiClasses;
	Map<String, String> filePath;
	LinkedHashMap<String, LinkedHashSet<String>> apiMappingSet;
	LinkedHashMap<String, ArrayList<String>> apiMappingArray;

	public CodeParser(Set<String> commonClasses, String root) throws IOException {
		this.filePath = new HashMap<String, String>();
		this.apiMappingSet = new LinkedHashMap<String, LinkedHashSet<String>>();
		this.apiMappingArray = new LinkedHashMap<String, ArrayList<String>>();
		this.commonClasses = commonClasses;		
		this.apiClasses = getLuceneClasses(root);
		//this.javaClasses = getJavaClasses();
		
	}

	/**
	 * This method parse the file passed to it and returns all the API used in that file.
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public LinkedHashMap<String, LinkedHashSet<String>> getIncludedClasses() throws IOException {
		int parsedCount = 0;
		int nullCount = 0;
		for (String file : commonClasses) {
			String path = filePath.get(file);
			if(path == null) {
				nullCount++;
				continue;
			}
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String line;
			LinkedHashSet<String> classesInFile = new LinkedHashSet<String>();
			while ((line = br.readLine()) != null) {
				Set<String> classes = getUsedClasses(line, apiClasses);
				classesInFile.addAll(classes);
			}
			apiMappingSet.put(file, classesInFile);
			parsedCount++;
			br.close();
		}
		System.out.println(parsedCount + " files parsed.");
		System.out.println(nullCount + " files not parsed.");
		return apiMappingSet;
	}
	
	public LinkedHashMap<String, ArrayList<String>> getIncludedClassesAsArrayList() throws IOException {
		int parsedCount = 0;
		int nullCount = 0;
		for (String file : commonClasses) {
			String path = filePath.get(file);
			if(path == null) {
				nullCount++;
				continue;
			}
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String line;
			ArrayList<String> classesInFile = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				ArrayList<String> classes = getUsedClassesArrayList(line, apiClasses);
				classesInFile.addAll(classes);
			}
			apiMappingArray.put(file, classesInFile);
			parsedCount++;
			br.close();
		}
		System.out.println(parsedCount + " files parsed.");
		System.out.println(nullCount + " files not parsed.");
		return apiMappingArray;
	}

	private ArrayList<String> getJavaClasses() throws IOException {
		File file = new File("javaAllClasses.txt");
		ArrayList<String> javaclasses = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			javaclasses.add(line);
		}
		br.close();
		return javaclasses;
	}

	private ArrayList<String> getLuceneClasses(String root) {
		ArrayList<String> classes = new ArrayList<String>();
		getAllFiles(root, classes);
		return classes;
	}

	private void getAllFiles(String root, ArrayList<String> files) {
		File[] rootFolder = new File(root).listFiles();

		for (File f : rootFolder) {
			if (f.isDirectory()) {
				getAllFiles(f.getAbsolutePath(), files);
			}
			if (f.isFile() && f.getName().endsWith(".java") && commonClasses.contains(f.getName())) {
				files.add(f.getName().replace(".java", ""));
				filePath.put(f.getName(), f.getPath());
			}
		}
	}

	private ArrayList<String> getUsedClassesArrayList(String line, ArrayList<String> appClasses) {
		line = line.replaceAll("[^a-zA-Z0-9]", " ");
		String[] split = line.split("\\s+");
		ArrayList<String> usedClasses = new ArrayList<String>();
		for (String s : split) {
			/*if (javaClasses.contains(s)) {
				usedClasses.add(s);
			}*/
			if (appClasses.contains(s)) {
				usedClasses.add(s);
			}
		}
		return usedClasses;
	}
	
	private Set<String> getUsedClasses(String line, ArrayList<String> appClasses) {
		line = line.replaceAll("[^a-zA-Z0-9]", " ");
		String[] split = line.split("\\s+");
		Set<String> usedClasses = new HashSet<String>();
		for (String s : split) {
			if (appClasses.contains(s)) {
				usedClasses.add(s);
			}
		}
		return usedClasses;
	}
}