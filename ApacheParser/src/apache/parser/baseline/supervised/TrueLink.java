package apache.parser.baseline.supervised;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TrueLink {
	public int[][] getTrueLink(int highSize, int lowSize) throws IOException{
		
		int[][] truelink = new int[highSize][lowSize];
		ArrayList<String> lowfilenames = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("classes.txt"));
		String line;
		while((line = br.readLine()) != null){
			String name = line.split(">>>")[0];
			lowfilenames.add(name);
			//System.out.println(name);
		}
		int numTrueLink = 0;
		br = new BufferedReader(new FileReader("truelinks.txt"));
		int highIndex = 0;
		while((line = br.readLine()) != null){
			
			if(!line.contains("%")){
				String[] links = line.split("\\s+");
				if(links.length > 1){
					for(int i = 1; i < links.length; i++){
						for(int lowIndex = 0; lowIndex < lowfilenames.size(); lowIndex++){	
							//System.out.println(links[i].toLowerCase());
							if(links[i].equals(lowfilenames.get(lowIndex))) {
								truelink[highIndex][lowIndex] = 1;
								//System.out.println("Add link");
								numTrueLink++;
							}
						}
					}
				}
				highIndex++;
			}
		}
		System.out.println("Number of links: " + numTrueLink);
		return truelink;
	}
}

