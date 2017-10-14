package cop5556fa17;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.AST.*;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Simple test case with an empty program. This test expects an exception
	 * because all legal programs must have at least an identifier
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string. Parsing should fail
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the tokens
		Parser parser = new Parser(scanner); //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse();; //Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e);  //catch the exception and show it
			throw e;  //rethrow for Junit
		}
	}


	@Test
	public void testNameOnly() throws LexicalException, SyntaxException {
		String input = "prog";  //Legal program with only a name
		show(input);            //display input
		Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
		show(scanner);    //display the tokens
		Parser parser = new Parser(scanner);   //create parser
		Program ast = parser.parse();          //parse program and get AST
		show(ast);                             //Display the AST
		assertEquals(ast.name, "prog");        //Check the name field in the Program object
		assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
	}

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);
	}
	
	/*testing IDENTIFIER KW_boolean IDENTIFIER SEMI*/
	@Test
	public void testDec2() throws LexicalException, SyntaxException {
		String input = "prog boolean k;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Program ast= parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);
		assertEquals(KW_boolean, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);	
	}
	
	/*testing IDENTIFIER KW_int IDENTIFIER OP_ASSIGN Expression SEMI */
	@Test
	public void testDec3() throws LexicalException, SyntaxException {
		String input = "prog int k = 2;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		Expression_IntLit e = (Expression_IntLit) dec.e;
		assertEquals(2, e.value);
	}
	
	/*testing IDENTIFIER KW_boolean IDENTIFIER OP_ASSIGN Expression SEMI */
	@Test
	public void testDec4() throws LexicalException, SyntaxException {
		String input = "prog boolean k = 2;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);
		assertEquals(KW_boolean, dec.type.kind);
		assertEquals("k", dec.name);
		Expression_IntLit e = (Expression_IntLit) dec.e;
		assertEquals(2, e.value);
	}
	
	/*testing multiple declarations after IDENT */
	@Test
	public void testDec5() throws LexicalException, SyntaxException {
		String input = "prog boolean k = 2; int xyz; boolean wow;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);
		assertEquals(KW_boolean, dec.type.kind);
		assertEquals("k", dec.name);
		Expression_IntLit e = (Expression_IntLit) dec.e;
		assertEquals(2, e.value);
		
		dec = (Declaration_Variable) ast.decsAndStatements.get(1);
		assertEquals(KW_int, dec.type.kind);
		
		dec = (Declaration_Variable) ast.decsAndStatements.get(2);
		assertEquals(KW_boolean, dec.type.kind);
	}
	
	/*testing image declarations */
	@Test
	public void testDec7() throws LexicalException, SyntaxException {
		String input = "prog image [xy, za] abc <- $abc;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements
				.get(0);
		Expression_Ident e0 = (Expression_Ident) dec.xSize;
		Expression_Ident e1 = (Expression_Ident) dec.ySize;
		Source_Ident source = (Source_Ident) dec.source;
		assertEquals("xy", e0.name);
		assertEquals("za", e1.name);
		assertEquals("abc", dec.name);
		assertEquals("$abc", source.name);
		
		input = "prog image [xy, za] abc <- @2;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		dec = (Declaration_Image) ast.decsAndStatements
				.get(0);
		e0 = (Expression_Ident) dec.xSize;
		e1 = (Expression_Ident) dec.ySize;
		assertEquals("xy", e0.name);
		assertEquals("za", e1.name);
		assertEquals("abc", dec.name);
		assertEquals("$abc", source.name);
		Source_CommandLineParam param = (Source_CommandLineParam) dec.source;
		Expression_IntLit paramNum = (Expression_IntLit) param.paramNum;
		assertEquals(2, paramNum.value);
		
		input = "prog image [xy, za] abc <- \"wow\";";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		dec = (Declaration_Image) ast.decsAndStatements
				.get(0);
		e0 = (Expression_Ident) dec.xSize;
		e1 = (Expression_Ident) dec.ySize;
		assertEquals("xy", e0.name);
		assertEquals("za", e1.name);
		assertEquals("abc", dec.name);
		Source_StringLiteral s = (Source_StringLiteral) dec.source;
		assertEquals("wow", s.fileOrUrl);
		
		input = "prog image [xy, za] abc ;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		dec = (Declaration_Image) ast.decsAndStatements
				.get(0);
		e0 = (Expression_Ident) dec.xSize;
		e1 = (Expression_Ident) dec.ySize;
		assertEquals("xy", e0.name);
		assertEquals("za", e1.name);
		assertEquals("abc", dec.name);
		
		input = "prog image xyz;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		dec = (Declaration_Image) ast.decsAndStatements
				.get(0);
		assertEquals("xyz", dec.name);
		}
	
	/*testing SourceSinkDeclaration's */
	@Test
	public void testDec9() throws LexicalException, SyntaxException {
		String input = "abc url google = \"wow\";";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		Declaration_SourceSink dec = (Declaration_SourceSink) ast.decsAndStatements
				.get(0);
		assertEquals(KW_url, dec.type);
		assertEquals("google", dec.name);
		Source_StringLiteral s = (Source_StringLiteral) dec.source;
		assertEquals("wow", s.fileOrUrl);
		
		input = "abc url google = @2;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(KW_url, dec.type);
		assertEquals("google", dec.name);
		Source_CommandLineParam param = (Source_CommandLineParam) dec.source;
		Expression_IntLit paramNum = (Expression_IntLit) param.paramNum;
		assertEquals(2, paramNum.value);
		
		input = "abc url google = bmw;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(KW_url, dec.type);
		assertEquals("google", dec.name);
		Source_Ident source = (Source_Ident) dec.source;
		assertEquals("bmw", source.name);
		
		input = "abc file google = \"wow\";";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(KW_file, dec.type);
		assertEquals("google", dec.name);
		s = (Source_StringLiteral) dec.source;
		assertEquals("wow", s.fileOrUrl);
		
		
		input = "abc file google = @2;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(KW_file, dec.type);
		assertEquals("google", dec.name);
		param = (Source_CommandLineParam) dec.source;
		paramNum = (Expression_IntLit) param.paramNum;
		assertEquals(2, paramNum.value);
		
		input = "abc file google = bmw;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(KW_file, dec.type);
		source = (Source_Ident) dec.source;
		assertEquals("bmw", source.name);
		}
	
	/*testing AssignmentStatements*/
	@Test
	public void testStm1() throws LexicalException, SyntaxException {
		String input = "abc prog = 2;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		Statement_Assign stat = (Statement_Assign) ast.decsAndStatements.get(0);
		LHS l = (LHS) stat.lhs;
		assertEquals("prog", l.name);
		Expression_IntLit e = (Expression_IntLit) stat.e;
		assertEquals(2, e.value);
		
		input = "abc prog[[x,y]] = 2;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		stat = (Statement_Assign) ast.decsAndStatements.get(0);
		l = (LHS) stat.lhs;
		assertEquals("prog", l.name);
		Index i = (Index) l.index;
		Expression_PredefinedName iKind0 = (Expression_PredefinedName) i.e0;
		Expression_PredefinedName iKind1 = (Expression_PredefinedName) i.e1;
		assertEquals(KW_x, iKind0.kind);
		assertEquals(KW_y, iKind1.kind);
		e = (Expression_IntLit) stat.e;
		assertEquals(2, e.value);
		
		input = "abc prog[[r,A]] = 2;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		stat = (Statement_Assign) ast.decsAndStatements.get(0);
		l = (LHS) stat.lhs;
		assertEquals("prog", l.name);
		i = (Index) l.index;
		iKind0 = (Expression_PredefinedName) i.e0;
		iKind1 = (Expression_PredefinedName) i.e1;
		assertEquals(KW_r, iKind0.kind);
		assertEquals(KW_A, iKind1.kind);
		e = (Expression_IntLit) stat.e;
		assertEquals(2, e.value);
		}
	
	/*testing ImageOutStatements and ImageInStatements*/
	@Test
	public void testStm3() throws LexicalException, SyntaxException {
		String input = "abc prog -> xyz;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		Statement_Out stat = (Statement_Out) ast.decsAndStatements.get(0);
		assertEquals("prog", stat.name);
		Sink_Ident si = (Sink_Ident) stat.sink;
		assertEquals("xyz", si.name);
		
		input = "abc prog -> SCREEN;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		stat = (Statement_Out) ast.decsAndStatements.get(0);
		assertEquals("prog", stat.name);
		Sink_SCREEN ss = (Sink_SCREEN) stat.sink;
		assertEquals(KW_SCREEN, ss.kind);
		
		input = "abc prog <- \"wow\";";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		Statement_In statIn = (Statement_In) ast.decsAndStatements.get(0);
		assertEquals("prog", statIn.name);
		Source_StringLiteral s = (Source_StringLiteral) statIn.source;
		assertEquals("wow", s.fileOrUrl);
		
		input = "abc prog <- @2;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		statIn = (Statement_In) ast.decsAndStatements.get(0);
		assertEquals("prog", statIn.name);
		Source_CommandLineParam param = (Source_CommandLineParam) statIn.source;
		Expression_IntLit paramNum = (Expression_IntLit) param.paramNum;
		assertEquals(2, paramNum.value);
		
		input = "abc prog <- xyz;";
		show(input);
		scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		parser = new Parser(scanner);  //
		ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		statIn = (Statement_In) ast.decsAndStatements.get(0);
		assertEquals("prog", statIn.name);
		Source_Ident source = (Source_Ident) statIn.source;
		assertEquals("xyz", source.name);
		}
	
	/**
	 * This example invokes the method for expression directly.
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression2() throws SyntaxException, LexicalException {
		String input = "x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Expression ast = parser.expression();
		Expression_PredefinedName e = (Expression_PredefinedName) ast;
		assertEquals(KW_x, e.kind);
		
		input = "y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_y, e.kind);
		
		input = "r";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_r, e.kind);
		
		input = "a";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_a, e.kind);
		
		input = "X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_X, e.kind);
		
		input = "Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_Y, e.kind);
		
		input = "Z";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_Z, e.kind);
		
		input = "R";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_R, e.kind);
		
		input = "A";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_A, e.kind);
		
		input = "DEF_X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_DEF_X, e.kind);
		
		input = "DEF_Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		e = (Expression_PredefinedName) ast;
		assertEquals(KW_DEF_Y, e.kind);
		
		input = "xyz";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		Expression_Ident EI = (Expression_Ident) ast;
		assertEquals("xyz", EI.name);
		
		input = "xyz[x,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		Expression_PixelSelector EPS = (Expression_PixelSelector) ast;
		assertEquals("xyz", EPS.name);
		Index i = (Index) EPS.index;
		Expression_PredefinedName e0 = (Expression_PredefinedName) i.e0;
		Expression_IntLit e1 = (Expression_IntLit) i.e1;
		assertEquals(KW_x, e0.kind);
		assertEquals(3, e1.value);
		
		input = "123";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		Expression_IntLit EIL = (Expression_IntLit) ast;
		assertEquals(123, EIL.value);
		
		input = "(23)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		Expression_IntLit eil = (Expression_IntLit) ast;
		assertEquals(23, eil.value);
		
		input = "sin [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		Expression_FunctionAppWithIndexArg EFWIA = (Expression_FunctionAppWithIndexArg) ast;
		assertEquals(KW_sin, EFWIA.function);
		Expression_IntLit ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		Expression_IntLit ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "cos [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWIA = (Expression_FunctionAppWithIndexArg) ast;
		assertEquals(KW_cos, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "atan [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWIA = (Expression_FunctionAppWithIndexArg) ast;
		assertEquals(KW_atan, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "abs [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWIA = (Expression_FunctionAppWithIndexArg) ast;
		assertEquals(KW_abs, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "cart_x [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWIA = (Expression_FunctionAppWithIndexArg) ast;
		assertEquals(KW_cart_x, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "cart_y [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWIA = (Expression_FunctionAppWithIndexArg) ast;
		assertEquals(KW_cart_y, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "polar_a [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWIA = (Expression_FunctionAppWithIndexArg) ast;
		assertEquals(KW_polar_a, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "polar_r [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWIA = (Expression_FunctionAppWithIndexArg) ast;
		assertEquals(KW_polar_r, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "sin (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		Expression_FunctionAppWithExprArg EFWEA = (Expression_FunctionAppWithExprArg) ast;
		assertEquals(KW_sin, EFWEA.function);
		Expression_IntLit check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "cos (2)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWEA = (Expression_FunctionAppWithExprArg) ast;
		assertEquals(KW_cos, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(2, check.value);
		
		input = "atan (2)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWEA = (Expression_FunctionAppWithExprArg) ast;
		assertEquals(KW_atan, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(2, check.value);
		
		input = "abs (2)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWEA = (Expression_FunctionAppWithExprArg) ast;
		assertEquals(KW_abs, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(2, check.value);
		
		input = "cart_x (2)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWEA = (Expression_FunctionAppWithExprArg) ast;
		assertEquals(KW_cart_x, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(2, check.value);
		
		input = "cart_y (2)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWEA = (Expression_FunctionAppWithExprArg) ast;
		assertEquals(KW_cart_y, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(2, check.value);
		
		input = "polar_a (2)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWEA = (Expression_FunctionAppWithExprArg) ast;
		assertEquals(KW_polar_a, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(2, check.value);
		
		input = "polar_r (2)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		EFWEA = (Expression_FunctionAppWithExprArg) ast;
		assertEquals(KW_polar_r, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(2, check.value);
	
		input = "!x";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		Expression_Unary eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		Expression_PredefinedName eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_x, eps.kind);
		
		input = "!y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_y, eps.kind);
		
		input = "!r";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_r, eps.kind);
		
		input = "!a";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_a, eps.kind);
		
		input = "!X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_X, eps.kind);
		
		input = "!Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_Y, eps.kind);
		
		input = "!Z";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_Z, eps.kind);
		
		input = "!R";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_R, eps.kind);
		
		input = "!A";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_A, eps.kind);
		
		input = "!DEF_X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_DEF_X, eps.kind);
		
		input = "!DEF_Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_DEF_Y, eps.kind);
		
		input = "!xyz";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		Expression_Ident ei = (Expression_Ident) eu.e;
		assertEquals("xyz", ei.name);
		
		input = "!xyz [x,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		Expression_PixelSelector ep = (Expression_PixelSelector) eu.e;
		assertEquals("xyz", ep.name);
		i = (Index) ep.index;
		Expression_PredefinedName epNew = (Expression_PredefinedName) i.e0;
		assertEquals(epNew.kind, KW_x);
		eil = (Expression_IntLit) i.e1;
		assertEquals(e1.value, 3);
		
		input = "!123";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eil = (Expression_IntLit) eu.e;
		assertEquals(123, eil.value);
		
		input = "!(23)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		eil = (Expression_IntLit) eu.e;
		assertEquals(23, eil.value);
		
		input = "!sin [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_sin, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "!cos [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cos, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "!atan [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_atan, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "!abs [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_abs, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "!cart_x [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cart_x, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "!cart_y [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cart_y, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "!polar_a [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_polar_a, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "!polar_r [2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_polar_r, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "!sin (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_sin, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!cos (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cos, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!sin (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_sin, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!atan (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_atan, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!sin (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_sin, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!abs (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_abs, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!cart_x (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cart_x, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!cart_y (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cart_y, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!sin (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_sin, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!polar_a (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_polar_a, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!sin (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_sin, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "!polar_r (3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_polar_r, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
	}
	
	/*This example invokes the method for expression directly.
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression3() throws SyntaxException, LexicalException {
		
		String input = "-x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Expression ast = parser.expression();
		Expression_Unary eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		Expression_PredefinedName eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_x, eps.kind);
		
		input = "-y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_y, eps.kind);
		
		input = "-a";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_a, eps.kind);
		
		input = "-r";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_r, eps.kind);
		
		input = "-X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_X, eps.kind);
		
		input = "-Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_Y, eps.kind);
		
		input = "-Z";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_Z, eps.kind);
		
		input = "-R";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_R, eps.kind);
		
		input = "-A";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_A, eps.kind);
		
		input = "-DEF_X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_DEF_X, eps.kind);
		
		input = "-DEF_Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_DEF_Y, eps.kind);
		
		input = "-xyz";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		Expression_Ident ei = (Expression_Ident) eu.e;
		assertEquals("xyz", ei.name);
		
		input = "-xyz [x,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		Expression_PixelSelector test1 = (Expression_PixelSelector) eu.e;
		assertEquals("xyz", test1.name);
		Index i = (Index) test1.index;
		Expression_PredefinedName e0 = (Expression_PredefinedName) i.e0;
		Expression_IntLit e1 = (Expression_IntLit) i.e1;
		assertEquals(KW_x, e0.kind);
		assertEquals(3, e1.value);
		
		input = "-123";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		Expression_IntLit eil = (Expression_IntLit) eu.e;
		assertEquals(123, eil.value);
		
		input = "-(23)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		eil = (Expression_IntLit) eu.e;
		assertEquals(23, eil.value);
		
		input = "-sin[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		Expression_FunctionAppWithIndexArg EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_sin, EFWIA.function);
		Expression_IntLit ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		Expression_IntLit ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-cos[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cos, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-atan[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_atan, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-abs[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_abs, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-cart_x[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cart_x, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-cart_y[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cart_y, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-polar_a[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_polar_a, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-polar_r[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_polar_r, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-sin(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		Expression_FunctionAppWithExprArg EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_sin, EFWEA.function);
		Expression_IntLit check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-cos(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cos, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-atan(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_atan, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-abs(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_abs, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-cart_x(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cart_x, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-cart_y(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cart_y, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-polar_a(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_polar_a, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-polar_r(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_polar_r, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-cos(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cos, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-!x";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		Expression_Unary EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		Expression_PredefinedName epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_x, epn.kind);
		
		input = "-!y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_y, epn.kind);
		
		input = "-!r";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_r, epn.kind);
		
		input = "-!a";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_a, epn.kind);
		
		input = "-!X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_X, epn.kind);
		
		input = "-!Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_Y, epn.kind);
		
		input = "-!Z";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_Z, epn.kind);
		
		input = "-!R";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_R, epn.kind);
		
		input = "-!A";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_A, epn.kind);
		
		input = "-!DEF_X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_DEF_X, epn.kind);
		
		input = "-!DEF_Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_DEF_Y, epn.kind);
		
		input = "-!xyz";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		ei = (Expression_Ident) EU.e;
		assertEquals("xyz", ei.name);
		
		input = "-!xyz[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		Expression_PixelSelector ep = (Expression_PixelSelector) EU.e;
		assertEquals("xyz", ep.name);
		ex0 = (Expression_IntLit) ep.index.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) ep.index.e1;
		assertEquals(3, ex1.value);
		
		input = "-!123";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		eil = (Expression_IntLit) EU.e;
		assertEquals(123, eil.value);
		
		input = "-!(23)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		eil = (Expression_IntLit) EU.e;
		assertEquals(23, eil.value);
		
		input = "-!sin[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_sin, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-!cos[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_cos, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-!atan[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_atan, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-!abs[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_abs, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-!cart_x[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_cart_x, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-!cart_y[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_cart_y, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-!polar_r[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_polar_r, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-!polar_a[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_polar_a, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "-!sin(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_sin, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-!cos(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_cos, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-!abs(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_abs, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-!atan(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_atan, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-!cart_x(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_cart_x, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-!cart_y(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_cart_y, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-!polar_r(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_polar_r, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "-!polar_a(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_MINUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_polar_a, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
	}
	
	@Test
	public void expression4() throws SyntaxException, LexicalException {
		
		String input = "+x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Expression ast = parser.expression();
		Expression_Unary eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		Expression_PredefinedName eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_x, eps.kind);
		
		input = "+y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_y, eps.kind);
		
		input = "+a";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_a, eps.kind);
		
		input = "+r";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_r, eps.kind);
		
		input = "+X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_X, eps.kind);
		
		input = "+Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_Y, eps.kind);
		
		input = "+Z";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_Z, eps.kind);
		
		input = "+R";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_R, eps.kind);
		
		input = "+A";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_A, eps.kind);
		
		input = "+DEF_X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_DEF_X, eps.kind);
		
		input = "+DEF_Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eps = (Expression_PredefinedName) eu.e;
		assertEquals(KW_DEF_Y, eps.kind);
		
		input = "+xyz";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		Expression_Ident ei = (Expression_Ident) eu.e;
		assertEquals("xyz", ei.name);
		
		input = "+xyz [x,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		Expression_PixelSelector test1 = (Expression_PixelSelector) eu.e;
		assertEquals("xyz", test1.name);
		Index i = (Index) test1.index;
		Expression_PredefinedName e0 = (Expression_PredefinedName) i.e0;
		Expression_IntLit e1 = (Expression_IntLit) i.e1;
		assertEquals(KW_x, e0.kind);
		assertEquals(3, e1.value);
		
		input = "+123";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		Expression_IntLit eil = (Expression_IntLit) eu.e;
		assertEquals(123, eil.value);
		
		input = "+(23)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		eil = (Expression_IntLit) eu.e;
		assertEquals(23, eil.value);
		
		input = "+sin[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		Expression_FunctionAppWithIndexArg EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_sin, EFWIA.function);
		Expression_IntLit ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		Expression_IntLit ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+cos[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cos, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+atan[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_atan, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+abs[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_abs, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+cart_x[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cart_x, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+cart_y[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_cart_y, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+polar_a[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_polar_a, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+polar_r[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWIA = (Expression_FunctionAppWithIndexArg) eu.e;
		assertEquals(KW_polar_r, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+sin(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		Expression_FunctionAppWithExprArg EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_sin, EFWEA.function);
		Expression_IntLit check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+cos(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cos, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+atan(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_atan, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+abs(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_abs, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+cart_x(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cart_x, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+cart_y(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cart_y, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+polar_a(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_polar_a, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+polar_r(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_polar_r, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+cos(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EFWEA = (Expression_FunctionAppWithExprArg) eu.e;
		assertEquals(KW_cos, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+!x";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		Expression_Unary EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		Expression_PredefinedName epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_x, epn.kind);
		
		input = "+!y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_y, epn.kind);
		
		input = "+!r";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_r, epn.kind);
		
		input = "+!a";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_a, epn.kind);
		
		input = "+!X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_X, epn.kind);
		
		input = "+!Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_Y, epn.kind);
		
		input = "+!Z";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_Z, epn.kind);
		
		input = "+!R";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_R, epn.kind);
		
		input = "+!A";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_A, epn.kind);
		
		input = "+!DEF_X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_DEF_X, epn.kind);
		
		input = "+!DEF_Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		epn = (Expression_PredefinedName) EU.e;
		assertEquals(KW_DEF_Y, epn.kind);
		
		input = "+!xyz";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		ei = (Expression_Ident) EU.e;
		assertEquals("xyz", ei.name);
		
		input = "+!xyz[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		Expression_PixelSelector ep = (Expression_PixelSelector) EU.e;
		assertEquals("xyz", ep.name);
		ex0 = (Expression_IntLit) ep.index.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) ep.index.e1;
		assertEquals(3, ex1.value);
		
		input = "+!123";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		eil = (Expression_IntLit) EU.e;
		assertEquals(123, eil.value);
		
		input = "+!(23)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		eil = (Expression_IntLit) EU.e;
		assertEquals(23, eil.value);
		
		input = "+!sin[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_sin, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+!cos[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_cos, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+!atan[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_atan, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+!abs[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_abs, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+!cart_x[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_cart_x, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+!cart_y[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_cart_y, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+!polar_r[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_polar_r, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+!polar_a[2,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWIA = (Expression_FunctionAppWithIndexArg) EU.e;
		assertEquals(KW_polar_a, EFWIA.function);
		ex0 = (Expression_IntLit) EFWIA.arg.e0;
		assertEquals(2, ex0.value);
		ex1 = (Expression_IntLit) EFWIA.arg.e1;
		assertEquals(3, ex1.value);
		
		input = "+!sin(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_sin, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+!cos(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_cos, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+!abs(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_abs, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+!atan(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_atan, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+!cart_x(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_cart_x, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+!cart_y(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_cart_y, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+!polar_r(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_polar_r, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
		
		input = "+!polar_a(3)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eu = (Expression_Unary) ast;
		assertEquals(eu.op, OP_PLUS);
		EU = (Expression_Unary) eu.e;
		assertEquals(EU.op, OP_EXCL);
		EFWEA = (Expression_FunctionAppWithExprArg) EU.e;
		assertEquals(KW_polar_a, EFWEA.function);
		check = (Expression_IntLit) EFWEA.arg;
		assertEquals(3, check.value);
	}
	
	public void expression5() throws SyntaxException, LexicalException {
		
		String input = "+x*+x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Expression ast = parser.expression();
		Expression_Binary eb = (Expression_Binary) ast;
		Expression_Unary eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		Expression_PredefinedName epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_x);
		assertEquals(eb.op, OP_TIMES);
		Expression_Unary eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		Expression_PredefinedName epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_x);
		
		input = "+y/+y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_y);
		assertEquals(eb.op, OP_DIV);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_y);
		
		input = "+r++r";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_r);
		assertEquals(eb.op, OP_PLUS);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_r);
		
		input = "+X++X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_X);
		assertEquals(eb.op, OP_PLUS);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_X);
		
		input = "+Y-+Z";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_Y);
		assertEquals(eb.op, OP_MINUS);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_Z);
		
		input = "+Z<+R";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_Z);
		assertEquals(eb.op, OP_LT);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_R);
		
		input = "+R>+A";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_R);
		assertEquals(eb.op, OP_GT);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_A);
		
		input = "+A<=-DEF_X";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_A);
		assertEquals(eb.op, OP_LE);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_MINUS);
		epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_DEF_X);
		
		input = "+DEF_X>=+DEF_Y";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_DEF_X);
		assertEquals(eb.op, OP_GE);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		epn1 = (Expression_PredefinedName) eu1.e;
		assertEquals(epn1.kind, KW_DEF_Y);
		
		input = "+DEF_Y==-xyz";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		epn0 = (Expression_PredefinedName) eu0.e;
		assertEquals(epn0.kind, KW_DEF_Y);
		assertEquals(eb.op, OP_EQ);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_MINUS);
		Expression_Ident ei = (Expression_Ident) eu1.e;
		assertEquals(ei.name, "xyz");
		
		input = "+xyz!=+xyz [x,3]";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		Expression_Ident ei0 = (Expression_Ident) eu0.e;
		assertEquals(ei0.name, "xyz");
		assertEquals(eb.op, OP_NEQ);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		Expression_PixelSelector eu1Name = (Expression_PixelSelector) eu1.e;
		assertEquals(eu1Name.name, "xyz");
		Expression_PredefinedName ex0 = (Expression_PredefinedName) eu1Name.index.e0;
		assertEquals(KW_x, ex0.kind);
		Expression_IntLit ex1 = (Expression_IntLit) eu1Name.index.e1;
		assertEquals(3, ex1.value);
		
		input = "+xyz [x,3]&-123";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		Expression_PixelSelector eps = (Expression_PixelSelector) eu0.e;
		assertEquals(eps.name, "xyz");
		ex0 = (Expression_PredefinedName) eps.index.e0;
		assertEquals(ex0.kind, KW_x);
		ex1 = (Expression_IntLit) eps.index.e1;
		assertEquals(ex1.value, 3);
		assertEquals(eb.op, OP_AND);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_MINUS);
		ex1 = (Expression_IntLit) eu1.e;
		assertEquals(ex1.value, 123);
		
		input = "+123|+(23)";
		show(input);
		scanner = new Scanner(input).scan();  
		show(scanner);   
		parser = new Parser(scanner);  
		ast = parser.expression();
		eb = (Expression_Binary) ast;
		eu0 = (Expression_Unary) eb.e0;
		assertEquals(eu0.op, OP_PLUS);
		Expression_IntLit el = (Expression_IntLit) eu0.e;
		assertEquals(el.value, 123);
		assertEquals(eb.op, OP_OR);
		eu1 = (Expression_Unary) eb.e1;
		assertEquals(eu1.op, OP_PLUS);
		ex1 = (Expression_IntLit) eu1.e;
		assertEquals(ex1.value, 23);
	}
}
