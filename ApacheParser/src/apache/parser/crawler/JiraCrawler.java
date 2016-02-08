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

public class JiraCrawler {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String root = "C:\\Users\\install\\Desktop\\ReqToCode\\frames";
		FileWriter fw = new FileWriter("pages.csv");
		
		String base = "https://lucene.apache.org/core/5_4_1/core/";
		
		
		File[] filelist = new File(root).listFiles();
		for(File f : filelist){
			ArrayList<String> urls = getClassURLs(f.getPath());
			System.out.println(urls.size());
			for(String url : urls){
				//System.out.println("Processing " + url);
				getMethodDescirption(base + url);
			}
		}
		
		
		
		
		/*for(int i = 1; i <= 837; i++){
			System.out.println("Fetching page " + i);
			Document document = Jsoup.connect(base + i + "?f=-").timeout(10*1000).userAgent("Mozilla").get();
			Elements products = document.select("div.ProductGridItem");
			for(Element p : products){
				String itemName = p.select("div.GridItemName").text();
				String itemRating = p.select("div.GridItemRating").text().replace("? ", "");
				String itemTag = p.select("div.GridItemTag").text().replace(" ? ", "");;
				String itemPrice = p.select("div.GridItemPrice").text();
				String itemShipping = p.select("div.GridItemShipping").text();
				String itemCategory = p.select("div.GridItemPrimaryCategory").text().replace("more", "").replace(" ?", "");
				
				if(itemName.equals("next")) continue;
				Element image = p.select("img[src$=.jpg]").first();
				String imageURL = image.absUrl("src");
				fw.write(itemName + "," + itemRating + "," + itemTag + "," + itemPrice + "," + itemShipping + "," + itemCategory + "," + imageURL + "\n");
			}
		}
		fw.flush();
		fw.close();*/

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
				api_urls.add(link);
				//System.out.println(link + ".html");
			}
		}
		return api_urls;
	}
	
	private static Map<String, String> getMethodDescirption(String classURL) throws IOException{
		
		System.out.println(classURL);
		Map<String, String> method_desc = new HashMap<String, String>();
		Document document = Jsoup.connect(classURL).timeout(10*1000).userAgent("Mozilla").get();
		Elements elements = document.select("li.blockList");
		System.out.println(elements.size());
		for(Element e : elements){
			String methodName = e.select("h4").text();
			System.out.println(methodName);
		}
		
		return method_desc;
	}

}
