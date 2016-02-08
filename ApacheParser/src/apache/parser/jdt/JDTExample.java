package apache.parser.jdt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class JDTExample {
	
	
	public static void main(String[] args) {
		 String code = " Iterator it = onlySegments.iterator();";
		 char[] source = code.toCharArray();
		 ASTParser parser = ASTParser.newParser(AST.JLS3);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
		 parser.setSource(source);
		 // In order to parse 1.5 code, some compiler options need to be set to 1.5
		 Map options = JavaCore.getOptions();
		 JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		 parser.setCompilerOptions(options);
		 CompilationUnit result = (CompilationUnit) parser.createAST(null);
		 System.out.println(result);

	}

}
