package apache.parser.baseline.supervised;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CodeParser {
	Set<String> commonClasses;
	//ArrayList<String> javaClasses;
	ArrayList<String> apiClasses;
	Map<String, String> filePath;
	Map<String, Set<String>> apiMapping;

	public CodeParser(Set<String> commonClasses, String root) throws IOException {
		this.filePath = new HashMap<String, String>();
		this.apiMapping = new HashMap<String, Set<String>>();
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
	public Map<String, Set<String>> getIncludedClasses() throws IOException {
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
			Set<String> classesInFile = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				Set<String> classes = getUsedClasses(line, apiClasses);
				classesInFile.addAll(classes);
			}
			apiMapping.put(file, classesInFile);
			parsedCount++;
			br.close();
		}
		System.out.println(parsedCount + " files parsed.");
		System.out.println(nullCount + " files not parsed.");
		return apiMapping;
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

	private Set<String> getUsedClasses(String line, ArrayList<String> javaClasses, ArrayList<String> appClasses) {
		line = line.replaceAll("[^a-zA-Z0-9]", " ");
		String[] split = line.split("\\s+");
		Set<String> usedClasses = new HashSet<String>();
		for (String s : split) {
			if (javaClasses.contains(s)) {
				usedClasses.add(s);
			}
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