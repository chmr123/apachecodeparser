package apache.parser.comments;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.opencsv.CSVWriter;

public class ApacheParserComments {
	static FileWriter writer;
	static File file;
	public static void main(String[] args) throws Exception {
		
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
			writer = new FileWriter("comments\\" + f.getName().replace(".java", ".txt"));
			count++;
			System.out.println("Parsing file "  + count + " / " + filesize + " " + f.getName());
			file = f;
			
			//FileInputStream in = new FileInputStream("C:\\Users\\Mingrui\\Desktop\\Apache\\UIBean.java");
			FileInputStream in = new FileInputStream(f.getAbsolutePath());
			//System.out.println(f.getAbsolutePath());
			CompilationUnit cu;
			try {
				// parse the file
				cu = JavaParser.parse(in);
				//String comment = "";
				List<Comment> comment = cu.getAllContainedComments();
				try {
					for(Comment c : comment){
					//System.out.println(comment);
						if(c.toString().contains("Licensed to the Apache Software Foundation")) continue;
						String content = c.toString();
						content = content.replace("*", "");
						content = content.replace("\\", "");
						writer.write(content + "\n");
					//writer.write("zzzzzzzzzz\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				new MethodVisitor().visit(cu, null);
			}catch (Exception e) {
				System.out.println("Error occured");
			} 
			finally {
				//System.out.println("Error occured");
				in.close();
			}

			// visit and print the methods names
			
			writer.close();
		}
		
		
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
			
			List<Node> children = n.getChildrenNodes();
			
			//System.out.println(n.getName() + ":");
			List<Node> nodes = new ArrayList<Node>();
			printChildrenNode(children, nodes);
			
			for(Node node : nodes){
				String comment = "";
				if(node.getComment() != null){
					comment = node.getComment().toString().replaceAll("\"", "");
				}
				try {
					writer.write(comment + "\n");
					//writer.write("\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
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
