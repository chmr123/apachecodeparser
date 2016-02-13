import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.opencsv.CSVWriter;

public class ApacheParser {
	static CSVWriter writer;
	static File file;
	public static void main(String[] args) throws Exception {
		writer = new CSVWriter(new FileWriter("classes.csv"), ',');
		// creates an input stream for the file to be parsed
		String root = args[0];
		ArrayList<File> files = new ArrayList<File>();
		
		getAllFiles(root, files);

		int filesize = 0;
		for(File f : files){
			if(f.getName().endsWith(".java")) filesize++;
		}
		
		int count = 0;
		for (File f : files) {
			if(!f.getName().endsWith(".java")) continue;
			count++;
			System.out.println("Parsing file " + count + " / " + filesize);
			file = f;
			
			//FileInputStream in = new FileInputStream("C:\\Users\\Mingrui\\Desktop\\Apache\\UIBean.java");
			FileInputStream in = new FileInputStream(f.getAbsolutePath());
			//System.out.println(f.getAbsolutePath());
			CompilationUnit cu;
			try {
				// parse the file
				cu = JavaParser.parse(in);
			} finally {
				in.close();
			}

			// visit and print the methods names
			new MethodVisitor().visit(cu, null);
		}
		
		writer.close();
	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private static class MethodVisitor extends VoidVisitorAdapter {

		//@Override 
		public void visit(MethodDeclaration n, Object arg) {
			// here you can access the attributes of the method.
			// this method will be called for all methods in this
			// CompilationUnit, including inner class methods
			String[] entries = new String[4];
			List<Node> children = n.getChildrenNodes();
			//System.out.println(n.getName() + ":");
			List<Node> nodes = new ArrayList<Node>();
			printChildrenNode(children, nodes);
			for(Node node : nodes){
				String nodeName = "";
				String sourceFile = "";
				String comment = "";
				if(node != null){
					nodeName = node.toString().replaceAll("\"", "").replaceAll("'", "").replaceAll("\\n", " ");
				}
				
				if(nodeName.contains("switch")) continue;
				sourceFile = file.getName().replaceAll("\"", "");
				
				if(node.getComment() != null){
					comment = node.getComment().toString().replaceAll("\"", "");
				}
				
				String newnode = "";
				//nodeName = nodeName.replaceAll("^[a-zA-Z0-9]", " ");
				String[] split = nodeName.split("\\s+|\n");
				for(String s : split){
					newnode = newnode + s + " ";
				}
				entries[0] = newnode;
				entries[1] = n.getName();
				entries[2] = sourceFile;
				//entries[3] = comment;
				writer.writeNext(entries);
			}
			super.visit(n, arg);
		}
	}

	private static void printChildrenNode(List<Node> l, List<Node> nodes) {
		if (l.size() > 1) {
			for (int i = 0; i < l.size(); i++)
				printChildrenNode(l.get(i).getChildrenNodes(), nodes);
		}
		if (l.size() == 1) {
			nodes.add(l.get(0));
		}
		//
	}

	private static void getAllFiles(String root, ArrayList<File> files) {
		File[] rootFolder = new File(root).listFiles();

		for (File f : rootFolder) {
			if (f.isDirectory()) {
				getAllFiles(f.getAbsolutePath(), files);
			}
			if (f.isFile()) {
				files.add(f);
			}
		}
	}

}
