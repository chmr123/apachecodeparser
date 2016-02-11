package apache.parser.baseline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public class LinkMain2 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		boolean useRelation = false;
		String relationFileName = "";
		ArrayList<String> relationfilelist = new ArrayList<String>();
		String highFolder = args[0];
		String lowFolder = args[1];
		int highSize = new File(highFolder).listFiles().length;
		int lowSize = new File(lowFolder).listFiles().length;
		//String topicFolder = args[2];
		//double jaccard = Double.valueOf(args[3]);
		if(args.length == 2) useRelation = false;
		else{
			useRelation = true;
			relationFileName = args[2];
			String[] filenames = relationFileName.split(",");
			for(String file : filenames){
				relationfilelist.add(file);
			}
			//size = Integer.valueOf(args[1]);
		}
		
		//String highOriginal = args[4];
		//String lowOriginal = args[5];
		
		TFIDF tfidf = new TFIDF(highSize + lowSize);
		LinkedHashMap<String, ArrayList<Double>> highTFIDF = tfidf.getHighTFIDF(highSize);
		LinkedHashMap<String, ArrayList<Double>> lowTFIDF = tfidf.getLowTFIDF(lowSize);
		
		//BigramTFIDF bigramtfidf = new BigramTFIDF(highSize + lowSize);
		//LinkedHashMap<String, ArrayList<Double>> bigram_highTFIDF = bigramtfidf.getHighTFIDF(highSize);
		//LinkedHashMap<String, ArrayList<Double>> bigram_lowTFIDF = bigramtfidf.getLowTFIDF(lowSize);
		
		TrueLink truelink = new TrueLink();
		int[][] trueLinkMatrix = truelink.getTrueLink(highSize, lowSize);
		double[][] tfidfSimMatrix = new double[highSize][lowSize];
		double[][] tfidfSimMatrixBi = new double[highSize][lowSize];
		//FunctionMatch f = new FunctionMatch(highFolder, lowFolder, topicFolder, jaccard);
		//double[][] functionMatrix = f.getFunctionMatchMatrix();
		
	
			
			int highIndex = 0;
			for(String high_key : highTFIDF.keySet()){
				int lowIndex = 0;
				for(String low_key : lowTFIDF.keySet()){
					System.out.print("Calculating " + highIndex + " " + lowIndex + "\r");
					double cosSim = getSimilarity(highTFIDF.get(high_key), lowTFIDF.get(low_key));
					//double cosSimBi = getSimilarity(bigram_highTFIDF.get(high_key), bigram_lowTFIDF.get(low_key));
					//double functionSim = functionMatrix[highIndex][lowIndex];
					//double sim = cosSim > functionSim ? cosSim : functionSim;
					//double sim = Math.sqrt(cosSim * functionSim);
					tfidfSimMatrix[highIndex][lowIndex] = cosSim;
					//tfidfSimMatrixBi[highIndex][lowIndex] = cosSimBi;
					lowIndex++;
				}
				highIndex++;
			}
			System.out.println();
			/*if(useRelation == true){
				
				Relation r = new Relation();
				
				int[][] relationMatrix = new int[highSize][lowSize];
				for(String file : relationfilelist){
					LinkedHashMap<String, String[]> relation = r.getRelation(file);
					int[][] tempRelationMatrix = r.checkRelation(highSize, lowSize, relation);; 
					for(int i = 0; i < highSize; i++){
						for(int j = 0; j < lowSize; j++){
							relationMatrix[i][j] = relationMatrix[i][j] + tempRelationMatrix[i][j];
						}
					}
				}
				
				for(int i = 0; i < highSize; i++){
					for(int j = 0; j < lowSize; j++){
						predictedLinkMatrix[i][j] = predictedLinkMatrix[i][j] * relationMatrix[i][j];
					}
				}
			}*/
			
			//FunctionMatch f = new FunctionMatch(highFolder, lowFolder, topicFolder, jaccard);
			//double[][] functionMatrix = f.getFunctionMatchMatrix();
			//Component c = new Component(highOriginal, lowOriginal);
			//int[][] componentMatrix = c.getComponentMatch();
			/*for(int i = 0; i < highSize; i++){
				for(int j = 0; j < lowSize; j++){
					predictedLinkMatrix[i][j] = predictedLinkMatrix[i][j] + (functionMatrix[i][j] * componentMatrix[i][j]);
				}
			}*/
			
			/*if(args.length > 2)
				{
					count = args[2].split(",").length;
				}*/
		for(double threshold = 0.1; threshold < 0.9; threshold += 0.1){
			System.out.println("Threshold: " + threshold);
			int[][] predictedLinkMatrix = new int[highSize][lowSize];
			for(int high = 0; high < highTFIDF.keySet().size(); high++){
				for(int low = 0; low < lowTFIDF.keySet().size(); low++){
					if(tfidfSimMatrix[high][low] >= threshold || tfidfSimMatrixBi[high][low] >= threshold)
						predictedLinkMatrix[high][low] = 1;
				}
			}
			compareLink(predictedLinkMatrix, trueLinkMatrix, highSize, lowSize);
			//getPairs(predictedLinkMatrix, highTFIDF, lowTFIDF, threshold);
			//DiffAR diffar = new DiffAR(highTFIDF, lowTFIDF);
			//System.out.println("DiffAR: " + diffar.getDiffAR(predictedLinkMatrix, trueLinkMatrix, highSize, lowSize));
			System.out.println();
		}
		
		MAP map = new MAP();
		map.getMAP(highSize, lowSize, tfidfSimMatrix, trueLinkMatrix);
		
		//FunctionMatch f = new FunctionMatch(highFolder, lowFolder, topicFolder, jaccard);
		//int[][] functionMatrix = f.getFunctionMatchMatrix();
		//MAPwithEvent mapevent = new MAPwithEvent();
		//mapevent.getMAP(highSize, lowSize, tfidfSimMatrix, trueLinkMatrix, functionMatrix);
	}
	
	public static double getSimilarity(ArrayList<Double> high, ArrayList<Double> low){
		double up = 0;
		for(int i = 0; i < high.size(); i++){
			up = up + high.get(i) * low.get(i);
		}
		
		double down1 = 0;
		double down2 = 0;
		for(int i = 0; i < high.size(); i++){
			down1 = down1 + Math.pow(high.get(i), 2);
			down2 = down2 + Math.pow(low.get(i), 2);
		}
		
		double sim = up / (Math.sqrt(down1) * Math.sqrt(down2));
		return sim;
	}
	
	public static void compareLink(int[][] predictedMatrix, int[][] trueMatrix, int highSize, int lowSize){
		double tp = 0;
		double fp = 0;
		double fn = 0;
		//int a = 0;
		for(int i = 0; i < highSize; i++){
			for(int j = 0; j < lowSize; j++){
				//if(trueMatrix[i][j] ==1) System.out.println(a++);
				if(predictedMatrix[i][j] >= 1 && trueMatrix[i][j] == 1) tp++;
				if(predictedMatrix[i][j] >= 1 && trueMatrix[i][j] == 0) fp++;
				if(predictedMatrix[i][j] == 0 && trueMatrix[i][j] == 1) fn++;
			}
		}
		System.out.println(tp + " " + fp + " " + fn);
		//double recall = 0;
		//double precision = 0;
		//double fscore = 0;
		//if(tp != 0){
			double recall = tp / (tp + fn);
			double precision = tp / (tp + fp);
			double fscore = 2*recall*precision / (precision + recall);
		//}
		 
		System.out.printf("Recall: %.2f Precision: %.2f Fscore: %.2f\n", recall, precision, fscore);
		
	}
	
	private static void getPairs(int[][] predictedMatrix, LinkedHashMap<String, ArrayList<Double>> highTFIDF, LinkedHashMap<String, ArrayList<Double>> lowTFIDF, double threshold) throws IOException{
		FileWriter fw = new FileWriter("pairs" + threshold + ".txt");
		int highIndex = 0;
		for(String highKey : highTFIDF.keySet()){
			int lowIndex = 0;
			for(String lowKey : lowTFIDF.keySet()){
				if(predictedMatrix[highIndex][lowIndex] != 0)
					fw.write(highKey + " , " + lowKey + "\n");
				lowIndex++;
			}
			highIndex++;
		}
		fw.flush();
		fw.close();
	}

}
