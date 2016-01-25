package apache.parser.lemmademo;

public class LemmaDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StanfordLemmatizer lemma = new StanfordLemmatizer();
		String a = "doesn't";
		String b = lemma.lemmatize(a);
		System.out.println(b);
		
	}

}
