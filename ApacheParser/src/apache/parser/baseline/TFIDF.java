package apache.parser.baseline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TFIDF {
	ArrayList<String> highkeys = new ArrayList<String>();
	ArrayList<String> lowkeys = new ArrayList<String>();
	int totalSize;
	public TFIDF(int totalSize) throws IOException{
		BufferedReader br1 = new BufferedReader(new FileReader("req_all_stem.txt"));
		String line;
		while((line = br1.readLine()) != null){
			String key = line.split(">>>")[0];
			highkeys.add(key);
		}
		
		BufferedReader br2 = new BufferedReader(new FileReader("uc_all_stem.txt"));	
		while((line = br2.readLine()) != null){
			String key = line.split(">>>")[0];
			lowkeys.add(key);
		}
		
		this.totalSize = totalSize;
	}
	public LinkedHashMap<String, ArrayList<Double>> getHighTFIDF(int size) throws IOException{
		//System.out.println("get high tf ");
		TF tf = new TF();
		IDF idf = new IDF(totalSize);
		LinkedHashMap<String, ArrayList<Double>> highTFIDF = new LinkedHashMap<String, ArrayList<Double>>();
		LinkedHashMap<String, ArrayList<Double>> highTF_entity = tf.gethighTF();
		LinkedHashMap<String, ArrayList<Double>> highIDF_entity = idf.gethighIDF();
		//FileWriter fw = new FileWriter("test.txt");
		for(String key : highkeys){	
			ArrayList<Double> tfidf_entity = new ArrayList<Double>();
			ArrayList<Double> tf_high = highTF_entity.get(key);
			ArrayList<Double> idf_high = highIDF_entity.get(key);
			for(int i = 0; i < tf_high.size(); i++){
				double tfidf = tf_high.get(i) * idf_high.get(i);
				tfidf_entity.add(tfidf);
			}	
			highTFIDF.put(key, tfidf_entity);
		}

		return highTFIDF;
		
	}
	
	public LinkedHashMap<String, ArrayList<Double>> getLowTFIDF(int size) throws IOException{
		TF tf = new TF();
		IDF idf = new IDF(totalSize);
		LinkedHashMap<String, ArrayList<Double>> lowTFIDF = new LinkedHashMap<String, ArrayList<Double>>();
		LinkedHashMap<String, ArrayList<Double>> lowTF_entity = tf.getlowTF();
		LinkedHashMap<String, ArrayList<Double>> lowIDF_entity = idf.getlowIDF();
		for(String key : lowkeys){
			ArrayList<Double> tfidf_entity = new ArrayList<Double>();
			ArrayList<Double> tf_low = lowTF_entity.get(key);
			ArrayList<Double> idf_low = lowIDF_entity.get(key);
			for(int i = 0; i < tf_low.size(); i++){
				double tfidf = tf_low.get(i) * idf_low.get(i);
				tfidf_entity.add(tfidf);
			}
			lowTFIDF.put(key, tfidf_entity);
		}
		return lowTFIDF;
	}
	
	
}
