/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "\" greetings  ";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
	
	/*@Test
	public void failUnopenedStringLiteral() throws LexicalException {
		String input = " greetings \"";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(0,e.getPos());
			throw e;
		}
	}*/
	
	/**
	 * Test for  STRING_LITERAL. 
	 */
	@Test
	public void testStringLiteral() throws LexicalException {
		String input = "x=\"Ro\\ahit\"\n123";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			checkNext(scanner, KW_x, 0, 1, 1, 1);
			checkNext(scanner, OP_ASSIGN, 1,1,1,2);
			checkNext(scanner, STRING_LITERAL, 2, 9, 1, 3);
			checkNext(scanner, INTEGER_LITERAL, 12, 3, 2, 1);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			assertEquals(5,e.getPos());
			throw e;
		}
	}
	
	/**
	 * Test for  illegal backslash. 
	 */
	@Test
	public void testIllegealBackslash() throws LexicalException {
		String input = "Ro\\it\n123";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			checkNext(scanner, IDENTIFIER, 0, 2, 1, 1);
			checkNext(scanner, IDENTIFIER, 3, 3, 1, 4);
			checkNext(scanner, INTEGER_LITERAL, 7, 3, 2, 1);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			assertEquals(2,e.getPos());
			throw e;
		}
	}
	
	/**
	 * This example shows how to test for comment. 
	 */
	@Test
	public void testComment() throws LexicalException {
		String input = "123//check\\\\\\\nasdf123";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0,3,1,1);
		checkNext(scanner, IDENTIFIER, 14, 7, 2,1);
		checkNextIsEOF(scanner);
	}
	
	/**
	 * This example shows how to test for horizontal tab. 
	 */
	@Test
	public void testTab() throws LexicalException {
		String input = "123\tasdf123";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0,3,1,1);
		checkNext(scanner, IDENTIFIER, 4, 7, 1,5);
		checkNextIsEOF(scanner);
	}
	
	
	/**
	 * Test for an IDENTIFIERSS. 
	 */
	@Test
	public void testIdentifier() throws LexicalException {
		String input = "true_$123";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 9, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	/**
	 * Test for a keyword. 
	 */
	@Test
	public void testKeyword() throws LexicalException {
		String input = "x X y Y r R a A Z DEF_X DEF_Y SCREEN cart_x cart_y polar_a polar_r "
				+ "abs sin cos atan log image int boolean url file";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_x, 0, 1, 1, 1);
		checkNext(scanner, KW_X, 2, 1, 1, 3);
		checkNext(scanner, KW_y, 4, 1, 1, 5);
		checkNext(scanner, KW_Y, 6, 1, 1, 7);
		checkNext(scanner, KW_r, 8, 1, 1, 9);
		checkNext(scanner, KW_R, 10, 1, 1, 11);
		checkNext(scanner, KW_a, 12, 1, 1, 13);
		checkNext(scanner, KW_A, 14, 1, 1, 15);
		checkNext(scanner, KW_Z, 16, 1, 1, 17);
		checkNext(scanner, KW_DEF_X, 18, 5, 1, 19);
		checkNext(scanner, KW_DEF_Y, 24, 5, 1, 25);
		checkNext(scanner, KW_SCREEN, 30, 6, 1, 31);
		checkNext(scanner, KW_cart_x, 37, 6, 1, 38);
		checkNext(scanner, KW_cart_y, 44, 6, 1, 45);
		checkNext(scanner, KW_polar_a, 51, 7, 1, 52);
		checkNext(scanner, KW_polar_r, 59, 7, 1, 60);
		checkNext(scanner, KW_abs, 67, 3, 1, 68);
		checkNext(scanner, KW_sin, 71, 3, 1, 72);
		checkNext(scanner, KW_cos, 75, 3, 1, 76);
		checkNext(scanner, KW_atan, 79, 4, 1, 80);
		checkNext(scanner, KW_log, 84, 3, 1, 85);
		checkNext(scanner, KW_image, 88, 5, 1, 89);
		checkNext(scanner, KW_int, 94, 3, 1, 95);
		checkNext(scanner, KW_boolean, 98, 7, 1, 99);
		checkNext(scanner, KW_url, 106, 3, 1, 107);
		checkNext(scanner, KW_file, 110, 4, 1, 111);
		checkNextIsEOF(scanner);
	}
	
	/**
	 * Test for an INTEGER_LITERAL. 
	 */
	@Test
	public void testIntegerLiteral() throws LexicalException {
		String input = "03011\\";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			Scanner scanner = new Scanner(input).scan();
			checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
			checkNext(scanner, INTEGER_LITERAL, 1, 4, 1, 2);
			checkNextIsEOF(scanner);
		}
		catch (LexicalException e) {
			show(e);
			assertEquals(5,e.getPos());
			throw e;
		}
	}
	
	/**
	 * This example shows how to test for separators. 
	 */
	@Test
	public void testSeparator() throws LexicalException {
		String input = "[this(]";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LSQUARE, 0,1,1,1);
		checkNext(scanner, IDENTIFIER, 1, 4, 1,2);
		checkNext(scanner, LPAREN, 5,1,1,6);
		checkNext(scanner, RSQUARE, 6,1,1,7);
		checkNextIsEOF(scanner);
	}
	
	/**
	 * This example shows how to test for operators. 
	 */
	@Test
	public void testOperator() throws LexicalException {
		String input = "==<-";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_EQ, 0,2,1,1);
		checkNext(scanner, OP_LARROW, 2, 2, 1,3);
		checkNextIsEOF(scanner);
	}
	
	
	/**
	 * Test for combinations. 
	 */
	@Test
	public void testCombinations() throws LexicalException {
		String input = "030;\r\n0;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, INTEGER_LITERAL, 1, 2, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 1, 4);
		checkNext(scanner, INTEGER_LITERAL, 6, 1, 2, 1);
		checkNext(scanner, SEMI, 7, 1, 2, 2);
		checkNextIsEOF(scanner);
		
		input = "030;\n\r0;";
		scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, INTEGER_LITERAL, 1, 2, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 1, 4);
		checkNext(scanner, INTEGER_LITERAL, 6, 1, 3, 1);
		checkNext(scanner, SEMI, 7, 1, 3, 2);
		checkNextIsEOF(scanner);
		
		input = "030;\r\n0;asf\n30true\r\nxor";
		//input = "030;\r\n0;asf\n30";
		scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, INTEGER_LITERAL, 1, 2, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 1, 4);
		checkNext(scanner, INTEGER_LITERAL, 6, 1, 2, 1);
		checkNext(scanner, SEMI, 7, 1, 2, 2);
		checkNext(scanner, IDENTIFIER, 8, 3, 2, 3);
		checkNext(scanner, INTEGER_LITERAL, 12, 2, 3, 1);
		checkNext(scanner, BOOLEAN_LITERAL, 14, 4, 3, 3);
		checkNext(scanner, IDENTIFIER, 20, 3, 4, 1);
		checkNextIsEOF(scanner);
		
		input = "\"abc\\\"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			scanner = new Scanner(input).scan();
			show(scanner);
			checkNext(scanner, STRING_LITERAL, 0, 6, 1, 1);
			checkNextIsEOF(scanner);
		}
		catch(LexicalException e) {
			show(e);
			assertEquals(4, e.getPos());
			throw e;
		}
	}
}
