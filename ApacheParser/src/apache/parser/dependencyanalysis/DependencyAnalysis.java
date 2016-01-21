package apache.parser.dependencyanalysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DependencyAnalysis {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		String root = args[0];
		ArrayList<File> alljars = new ArrayList<File>();
		getAllFiles(root, alljars);
		
		int count = 1;
		for(File jar : alljars){
			System.out.println("Analyzing " + jar.getName()  +  " (" + count + " / " + alljars.size() + ")");
			getDependency(jar);
			count++;
		}
	}
	
	
	private static void getAllFiles(String root, ArrayList<File> files) {
		File[] rootFolder = new File(root).listFiles();

		for (File f : rootFolder) {
			if (f.isDirectory()) {
				getAllFiles(f.getAbsolutePath(), files);
			}
			if (f.isFile() && f.getName().endsWith(".jar")) {
				files.add(f);
			}

		}
	}
	
	private static void getDependency(File jar) throws IOException, InterruptedException{
		String outputfolder = "C:\\Users\\install\\Desktop\\ReqToCode\\dep_output\\";
		ProcessBuilder pr1 = new ProcessBuilder("DependencyExtractor.bat", "-out",outputfolder + jar.getName().replace(".jar",".txt"), jar.getAbsolutePath());
		//pr1.directory(new File("C:\\Users\\install\\Desktop\\DependencyFinder-1.2.1-beta4\\bin"));
		//pr1.directory(new File("/users5/csegrad/mingruic/transferlearning"));
		Process p = pr1.start();
		p.waitFor();

	}

}
