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

		//ArrayList<String> realnouns = getRealNouns();
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
		FileWriter fw = new FileWriter("action units.txt", true);
		for (File f : files) {
		
			ArrayList<String> verbObj = new ArrayList<String>();
			if(!f.getName().endsWith(".txt")) continue;
			System.out.println("Processing file " + ++i + ": " + f.getName());
			String filepath = f.getPath();
			DocumentPreprocessor dp = new DocumentPreprocessor(filepath);
			dp.setSentenceDelimiter("\n");
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

						/*phraseList.add(subtree);
						String np = subtree.toString();

						np = np.replaceAll("\\([A-Z]+ ", "");
						np = np.replaceAll("\\)", "");

						for (String tag : postags) {
							np = np.replace(tag, "");
						}
						String[] terms = np.split("\\s+");
						if (terms.length < 100 && terms.length > 0) {
							String goodterm = "";
							String goodtermstop = "";
							for (String s : terms) {
								s = s.toLowerCase();
								goodtermstop = goodtermstop + s + " ";
							}
							if (goodtermstop.lastIndexOf(" ") != -1)
								vp_each.add(goodtermstop.substring(0, goodtermstop.lastIndexOf(" ")));
						}*/
					}
				}
				// System.out.println(allnp);
			}
			fw.write(f.getName() + "\n");
			for(String vp : verbObj){
				fw.write(vp + "\n");
			}
			
			/*for (int j = 0; j < verbObj.size() - 1; j++) {
				String current = verbObj.get(j).substring(verbObj.get(j).indexOf(" ") + 1);
				String next = verbObj.get(j + 1).substring(verbObj.get(j + 1).indexOf(" ") + 1);
				//if (!current.equals(next))
					//System.out.println(verbObj.get(j));
			}*/
			
			
			//verb_phrase.put(f.getName(), verbObj);
			//System.out.println(verbObj);
			//System.out.println(verbObj.get(verbObj.size() - 1));
		} // Loop each file to get verb phrases
		//System.out.println(verb_phrase);
		fw.flush();
		fw.close();
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

	/*public static void actionUnitAnalysis(String filename, Set<String> vp, Set<String> np) throws IOException {

		ArrayList<String> verbs = new ArrayList<String>();
		File verbfile = new File("verbs.txt");
		BufferedReader br = new BufferedReader(new FileReader(verbfile));
		String line;
		while ((line = br.readLine()) != null) {
			verbs.add(line);
		}

		System.out.println("Analyzing file " + filename);
		FileWriter fw = new FileWriter("action unit.txt", true);
		fw.write(filename + "\n");
		for (String s : vp) {
			String[] splitted = s.split(" ");
			String verb = splitted[0];
			ArrayList<String> others = new ArrayList<String>();
			for (int i = 1; i < splitted.length; i++) {
				others.add(splitted[i]);
			}
			for (String n : np) {
				if (others.contains(n))
					fw.write(verb + " " + n + "\n");
			}
		}
		fw.flush();
		fw.close();
	}*/

	/*public static ArrayList<String> getRealNouns() throws IOException {
		ArrayList<String> actionUnits = new ArrayList<String>();
		File file = new File("nouns.txt");
		// File file = new File("shortcutkey.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			actionUnits.add(line.toLowerCase());
		}
		return actionUnits;
	}*/
}
