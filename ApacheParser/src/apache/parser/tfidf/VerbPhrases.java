package apache.parser.tfidf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class VerbPhrases {
	static ArrayList<String> verbs = new ArrayList<String>();

	public void getVP(String folder) throws IOException {
		// NounPhrases nps = new NounPhrases();
		// LinkedHashMap<String, Set<String>> file_np = nps.getNounPhrases();
		
		String parserModel = "englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		ArrayList<String> stopwords = new ArrayList<String>();
		ArrayList<String> postags = new ArrayList<String>();
		Set<String> vp_each = new HashSet<String>();
		File file = new File("stopwords.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			stopwords.add(line);
		}
		br.close();

		file = new File("pos tags.txt");
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			postags.add(line);
		}


		TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a
																// PennTreebankLanguagePack
																// for English
		GrammaticalStructureFactory gsf = null;
		if (tlp.supportsGrammaticalStructures()) {
			gsf = tlp.grammaticalStructureFactory();
		}
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		int i = 0;
		File[] files = new File(folder).listFiles();
		//LinkedHashMap<String, ArrayList<String>> verb_phrase = new LinkedHashMap<String, ArrayList<String>>();
		
	for (File f : files) {		
		try{
			FileWriter fw = new FileWriter("vb_comment\\" + f.getName(), true);
			ArrayList<String> verbObj = new ArrayList<String>();
			if(!f.getName().endsWith(".txt")) continue;
			System.out.println("Processing file " + ++i + ": " + f.getName());
			String filepath = f.getPath();
			DocumentPreprocessor dp = new DocumentPreprocessor(filepath);
			String[] deliminator = {".", "?", "!", ":", "\n"};
			//dp.setSentenceDelimiter("\n");
			dp.setSentenceFinalPuncWords(deliminator);
			for (List<HasWord> sentence : dp) {
				Tree parse = lp.apply(sentence);
				//parse.pennPrint();

				if (gsf != null) {
					GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
					Collection tdl = gs.typedDependenciesCCprocessed();
				}

				List<Tree> phraseList = new ArrayList<Tree>();
				for (Tree subtree : parse) {
					if (subtree.label().value().equals("VP")) {
						for (Tree np_subtree : subtree) {
							if (np_subtree.label().value().contains("NP")) {
								// System.out.println("aaa");
								List<Tree> children = np_subtree.getChildrenAsList();
								String verb = "";
								String phrase = "";
								for (Tree c : children) {
									if (c.label().value().equals("NP") && !c.isLeaf()) {
										String np = c.toString();
										np = np.replaceAll("\\([A-Z]+ ", "");
										np = np.replaceAll("\\)", "");
										for (String tag : postags) {
											np = np.replace(tag, "");
										}
										String[] terms = np.split("\\s+");
										if (terms.length < 100 && terms.length > 0) {
											String goodtermstop = "";
											for (String s : terms) {
												s = s.toLowerCase();
												if(goodtermstop.contains("("))
													continue;
												if (stopwords.contains(s))
													continue;
												goodtermstop = goodtermstop + s + " ";
											}
											if (goodtermstop.lastIndexOf(" ") != -1) {
												goodtermstop = goodtermstop.substring(0,goodtermstop.lastIndexOf(" "));
											}
											if (subtree.firstChild().label().value().contains("VB")) {
												verb = subtree.firstChild().toString().replaceAll("\\(VB[A-Z]? ","").replace(")", "");
												verbObj.add(verb + " " + goodtermstop);
												break;
											}
										}
									}

									// if(c.label().value().contains("NN"))
									phrase = phrase + c.toString().replaceAll("\\([A-Z]+ ","").replace(")", "") + " ";
								}
								if (subtree.firstChild().label().value().contains("VB")) {
									verb = subtree.firstChild().toString().replaceAll("\\(VB[A-Z]? ", "").replace(")", "");
									if (phrase.length() != 0) {
										verbObj.add(verb + " " + phrase);
									}
								}

								break;
							}
						}
						
						verbs.clear();

						
					}
				}
				// System.out.println(allnp);
			}
			for(String vp : verbObj){
				fw.write(vp + "\n");
			}
			
			fw.flush();
			fw.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
						
		} // Loop each file to get verb phrases
		//System.out.println(verb_phrase);
		
	}

	public static void getParentVB(Tree parent, Tree root) {
		if (!parent.equals(root)) {
			Tree parentOfParent = parent.parent(root);
			if (parentOfParent.label().value().equals("VP") && parentOfParent.firstChild().label().value().contains("VB")) {
				// System.out.println(parentOfParent.firstChild());
				//System.out.println(parentOfParent.toString());
				verbs.add(parentOfParent.firstChild().toString().replaceAll("\\(VB[A-Z]? ", "").replace(")", ""));
			}
			getParentVB(parentOfParent, root);
		}
	}
}
