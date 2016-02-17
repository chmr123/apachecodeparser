package apache.parser.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.opencsv.CSVWriter;

public class JiraCrawler {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String root = "frames";
		//FileWriter fw = new FileWriter("pages.csv");
		
		CSVWriter writer = new CSVWriter(new FileWriter("methodDescription.csv"), ',');
	     // feed in your array (or convert your data to an array)	
		String base = "https://lucene.apache.org/core/5_4_1/";
				
		File[] filelist = new File(root).listFiles();
		int filecount = 0;
		for(File f : filelist){
			filecount++;
			String packageName = f.getName().replace(".txt", "");
			ArrayList<String> urls = getClassURLs(f.getPath());
			System.out.println(urls.size());
			int count = 0;
			for(String url : urls){
				count++;
				System.out.println("Processing " + count + " / " + urls.size()  + " urls in " + filecount + " / " + filelist.length + " files (" + f.getName() + ")");
				Map<String, String> method_desc = getMethodDescirption(base + "/" + packageName + "/" + url);
				String[] entries = new String[3];
				for(String key : method_desc.keySet()){
					String[] split = key.split("#");
					entries[2] = split[0] + ".java"; //class name
					entries[1] = split[1]; //method name
					entries[0] = method_desc.get(key); //description
					  writer.writeNext(entries);
				}
				
			}
		}
			
		 writer.close();

	}
	
	private static ArrayList<String> getClassURLs(String framedoc) throws IOException{
		
		ArrayList<String> api_urls = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(framedoc)));
		String line;
		while((line = br.readLine()) != null){
			if(line.startsWith("<li>")){
				int startIndex = line.indexOf("\"") + 1;
				int endIndex = line.indexOf(".html");
				String link = line.substring(startIndex, endIndex);
				api_urls.add(link + ".html");
				//System.out.println(link + ".html");
			}
		}
		return api_urls;
	}
	
	private static Map<String, String> getMethodDescirption(String classURL) throws IOException{
		
		//System.out.println(classURL);
		String className = classURL.substring(classURL.lastIndexOf("/") + 1).replace(".html", "");
		
		Map<String, String> method_desc = new HashMap<String, String>();
		Document document = Jsoup.connect(classURL).timeout(10*1000).userAgent("Mozilla").get();
		Elements elements = document.select("li.blockList");
		//System.out.println(elements.size());
		for(Element e : elements){
			String methodName = e.select("h4").text();
			String text = e.select("div.block").text();
			//System.out.println(methodName);
			//System.out.println("\t" + text);
			if(methodName.length() != 0 && text.length() != 0 && methodName.split(" ").length == 1){
				method_desc.put(className + "#" + methodName, text);
				//System.out.println(className + "#" + methodName);
			}
		}
		
		return method_desc;
	}

}
