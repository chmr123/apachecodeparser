package apache.parser.baseline.IR;

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
	ArrayList<String> wordlist;
	int totalSize;
	public TFIDF(int totalSize, ArrayList<String> wordlist) throws IOException{
		BufferedReader br1 = new BufferedReader(new FileReader("req.txt"));
		String line;
		while((line = br1.readLine()) != null){
			String key = line.split(">>>")[0];
			highkeys.add(key);
		}
		
		BufferedReader br2 = new BufferedReader(new FileReader("classes.txt"));	
		while((line = br2.readLine()) != null){
			String key = line.split(">>>")[0];
			lowkeys.add(key);
		}
		this.wordlist = wordlist;
		this.totalSize = totalSize;
	}
	public LinkedHashMap<String, ArrayList<double[]>> getHighTFIDF(int size) throws IOException{
		//System.out.println("get high tf ");
		TF tf = new TF(wordlist);
		IDF idf = new IDF(totalSize, wordlist);
		LinkedHashMap<String, ArrayList<double[]>> highTFIDF = new LinkedHashMap<String, ArrayList<double[]>>();
		LinkedHashMap<String, ArrayList<Double>> highTF_entity = tf.gethighTF();
		LinkedHashMap<String, ArrayList<Double>> highIDF_entity = idf.gethighIDF();
		//FileWriter fw = new FileWriter("test.txt");
		for(String key : highkeys){	
			ArrayList<double[]> tfidf_entity = new ArrayList<double[]>();
			ArrayList<Double> tf_high = highTF_entity.get(key);
			ArrayList<Double> idf_high = highIDF_entity.get(key);
			for(int i = 0; i < tf_high.size(); i++){
				double tfidf = tf_high.get(i) * idf_high.get(i);
				double[] entry = new double[2];
				entry[0] = (double)i;
				entry[1] = tfidf;
				if(tfidf != 0){
					tfidf_entity.add(entry);
				}
			}	
			highTFIDF.put(key, tfidf_entity);
		}

		return highTFIDF;
		
	}
	
	public LinkedHashMap<String, ArrayList<double[]>> getLowTFIDF(int size) throws IOException{
		TF tf = new TF(wordlist);
		IDF idf = new IDF(totalSize, wordlist);
		LinkedHashMap<String, ArrayList<double[]>> lowTFIDF = new LinkedHashMap<String, ArrayList<double[]>>();
		LinkedHashMap<String, ArrayList<Double>> lowTF_entity = tf.getlowTF();
		LinkedHashMap<String, ArrayList<Double>> lowIDF_entity = idf.getlowIDF();
		for(String key : lowkeys){
			ArrayList<double[]> tfidf_entity = new ArrayList<double[]>();
			ArrayList<Double> tf_low = lowTF_entity.get(key);
			ArrayList<Double> idf_low = lowIDF_entity.get(key);
			for(int i = 0; i < tf_low.size(); i++){
				double tfidf = tf_low.get(i) * idf_low.get(i);
				double[] entry = new double[2];
				entry[0] = (double)i;
				entry[1] = tfidf;
				if(tfidf != 0){
					tfidf_entity.add(entry);
				}
			}
			lowTFIDF.put(key, tfidf_entity);
		}
		return lowTFIDF;
	}
	
	
}

