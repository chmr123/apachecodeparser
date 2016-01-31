package apache.parser.comments.preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Preprocess {

	public static void main(String[] args) throws IOException {
		File[] files = new File("comments").listFiles();
		int count = 1;
		for(File f : files){
			System.out.println("Preprocessing " + count + " / " + files.length);
			FileWriter fw = new FileWriter("preprocess\\" + f.getName());
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			String content = "";
			while((line = br.readLine()) != null){
				line = line.replace("*", "");
				line = line.replace("/", "");
				line = line.replaceAll("<.*>", "");
				if(line.startsWith("@")) continue;
				content = content + line + " ";
			}
			fw.write(content.replaceAll("\\s+", " "));
			fw.flush();
			fw.close();
			count++;
		}

	}

}
