package apache.parser.patch;

import java.util.ArrayList;

public class IndexFile {
	private ArrayList<String> addedLine;
	public IndexFile(){
		addedLine = new ArrayList<String>();
	}
	
	public void addLine(String line){
		addedLine.add(line);
	}
	
	public ArrayList<String> getAddedLines(){
		return addedLine;
	}
}
