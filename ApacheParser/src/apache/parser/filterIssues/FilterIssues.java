package apache.parser.filterIssues;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVReader;

public class FilterIssues {

	public static void main(String[] args) throws IOException {
		CSVReader reader = new CSVReader(new FileReader("lucene.csv"));
	     String [] nextLine;
	     while ((nextLine = reader.readNext()) != null) {
	    	 if(nextLine[1].equals("Bug")) continue;
	    	 if(!nextLine[2].equals("Fixed")) continue;
	    	 String id = nextLine[0];
	    	 String summary = nextLine[3];
	    	 String description = nextLine[4];
	    	 FileWriter fw = new FileWriter("filteredIssues\\" + id + ".txt");
	    	 fw.write(summary);
	    	 fw.write("\n");
	    	 fw.write(description);
	    	 fw.flush();
	    	 fw.close();
	     }

	}

}
