package apache.parser.baseline;

import java.util.ArrayList;

public class ApacheBaseline {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		parseMethod("setDateAndYear");
	}
	
	
	private static ArrayList<String> parseMethod(String method){
		ArrayList<String> methodterms = new ArrayList<String>();
		boolean capitalFound = false;
		int startIndex = 0;
		int endIndex = 0;
		for(int index = 0; index < method.length(); index++){
			if(Character.isUpperCase(method.charAt(index)) ){
				startIndex = endIndex;
				endIndex = index;
				String term = method.substring(startIndex, endIndex);
				System.out.println(term);
				methodterms.add(term);
			}
			if(index == method.length() - 1){
				startIndex = endIndex;
				endIndex = index;
				String term = method.substring(startIndex, endIndex+1);
				System.out.println(term);
				methodterms.add(term);
			}
		}
		
		return methodterms;
	}

}
