/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
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


import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Map;
import java.util.HashMap;

public class Scanner {
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, 
		KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, 
		OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, 
		LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}

	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;
	
	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;
	
	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  

	/*hash map for single operators and separators
	 * it has Kinds associated to single operators and separators
	 */
	final HashMap<Character, Kind> opSep = new HashMap<Character, Kind>();
	
	/*hash map for operators combined with =
	 * it has Kinds associated to operators having some sign in combination with '=' operator
	 */
	//final HashMap<Character, Kind> opEq = new HashMap<Character, Kind>();
	
	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
		
		//add key values to opSep
		opSep.put('?', Kind.OP_Q);
		opSep.put(':', Kind.OP_COLON);
		opSep.put('&', Kind.OP_AND);
		opSep.put('|', Kind.OP_OR);
		opSep.put('+', Kind.OP_PLUS);
		opSep.put('%', Kind.OP_MOD);
		opSep.put('@', Kind.OP_AT);
		opSep.put('(', Kind.LPAREN);
		opSep.put(')', Kind.RPAREN);
		opSep.put('[', Kind.LSQUARE);
		opSep.put(']', Kind.RSQUARE);
		opSep.put(';', Kind.SEMI);
		opSep.put(',', Kind.COMMA);
	}

	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		/* TODO  Replace this with a correct and complete implementation!!! */
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		int token_length = 0;

		//start scanning the chars list
		int i = 0;
		while (chars[i] != EOFchar) {
			//scanning for string literal
			if (chars[i] == '\"') {
				token_length = 1;
				i++;
				while(chars[i] != EOFchar && chars[i]!= '\"') { 
					if (chars[i] == '\n' || chars[i] == '\r') {
						throw new LexicalException(String.format("end quotes are missing at position %d", 
								pos+token_length), pos+token_length);
					}
					else if (chars[i] == '\\') {
						if (Arrays.asList('b','t','f','r','n','\'','\\','\"').contains(chars[i+1])) {
							i = i+2;
							token_length = token_length+2;
						}
						else {
							throw new LexicalException(String.format(
									"invalid backslash(\\) encountered at position %d", 
									i+1), i+1);
						}
					}
					else {
						i++;
						token_length++;
					}
				}
				if (chars[i] == EOFchar) {
					throw new LexicalException(String.format("end quotes are missing at position %d", 
							chars.length-1), chars.length-1);
				}
				token_length++;
				i++;
				tokens.add(new Token(Kind.STRING_LITERAL, pos, token_length, line, posInLine));
				pos = pos+token_length;
				posInLine = posInLine+token_length;
				}
			
			//scanning for Comment
			else if (chars[i] == '/') {
				if (chars[i+1] == '/') {
					while (chars[i] != EOFchar && chars[i] != '\n' && chars[i]!='\r') {
						i++;
						pos++;
					}
				}
				//scanning for /
				else {
					tokens.add(new Token(Kind.OP_DIV, pos++, 1, line, posInLine++));
					i++;
					}
				continue;
			} 
			//scanning identifier
			else if ((chars[i] >= 'A' && chars[i]<= 'Z') || 
					(chars[i] >= 'a' && chars[i]<= 'z') ||
					chars[i] == '_' || chars[i] == '$') {
				StringBuilder temp_token = new StringBuilder();
				
				//in this case, add the current char as the beginning of the token
				temp_token.append(chars[i]);
				i++;
				while ((chars[i] != EOFchar) && ((chars[i] >= 'A' && chars[i]<= 'Z') || 
							(chars[i] >= 'a' && chars[i]<= 'z') ||
							(chars[i] >= '0' && chars[i]<= '9') ||
							chars[i] == '_' || chars[i] == '$')) {
					temp_token.append(chars[i]);
					i++;
				}
				token_length = temp_token.length();
				
				//check what kind of token has been created
				Kind token_kind = check_token(temp_token.toString());
				tokens.add(new Token(token_kind, pos, token_length, line, posInLine));
				pos = pos+token_length;
				posInLine = posInLine+token_length;
				
			}
			//scanning for separators and single operators
			else if (opSep.containsKey(chars[i])) {
				tokens.add(new Token(opSep.get(chars[i]), pos++, 1, line, posInLine++));
				i++;
				continue;
			}
			//scanning for = or ==
			else if (chars[i] == '=') {
				if (chars[i+1] == '=') {
					tokens.add(new Token(Kind.OP_EQ, pos, 2, line, posInLine));
					i++;
					pos++;
					posInLine++;
				}
				else {
					tokens.add(new Token(Kind.OP_ASSIGN, pos, 1, line, posInLine));
				}
				i++;
				pos++;
				posInLine++;
				continue;
			}
			//scanning for > or >= or
			else if (chars[i] == '>') {
				if (chars[i+1] == '=') {
					tokens.add(new Token(Kind.OP_GE, pos, 2, line, posInLine));
					i++;
					pos++;
					posInLine++;
				}
				else {
					tokens.add(new Token(Kind.OP_GT, pos, 1, line, posInLine));
				}
				i++;
				pos++;
				posInLine++;
				continue;
			}
			//scanning for < or <= or <-
			else if (chars[i] == '<') {
				if (chars[i+1] == '=') {
					tokens.add(new Token(Kind.OP_LE, pos, 2, line, posInLine));
					i++;
					pos++;
					posInLine++;
				}
				else if (chars[i+1] == '-') {
					tokens.add(new Token(Kind.OP_LARROW, pos, 2, line, posInLine));
					i++;
					pos++;
					posInLine++;
				}
				else {
					tokens.add(new Token(Kind.OP_LT, pos, 1, line, posInLine));
				}
				i++;
				pos++;
				posInLine++;
				continue;
			}
			//scanning for ! or !=
			else if (chars[i] == '!') {
				if (chars[i+1] == '=') {
					tokens.add(new Token(Kind.OP_NEQ, pos, 2, line, posInLine));
					i++;
					pos++;
					posInLine++;
				}
				else {
					tokens.add(new Token(Kind.OP_EXCL, pos, 1, line, posInLine));
				}
				i++;
				pos++;
				posInLine++;
				continue;
			}
			//scanning for - or ->
			else if (chars[i] == '-') {
				if (chars[i+1] == '>') {
					tokens.add(new Token(Kind.OP_RARROW, pos, 2, line, posInLine));
					i++;
					pos++;
					posInLine++;
				}
				else {
					tokens.add(new Token(Kind.OP_MINUS, pos, 1, line, posInLine));
				}
				i++;
				pos++;
				posInLine++;
				continue;
			}
			//scanning for * OR **
			else if (chars[i] == '*') {
				if (chars[i+1] == '*') {
					tokens.add(new Token(Kind.OP_POWER, pos, 2, line, posInLine));
					i++;
					pos++;
					posInLine++;
				}
				else {
					tokens.add(new Token(Kind.OP_TIMES, pos, 1, line, posInLine));
				}
				i++;
				pos++;
				posInLine++;
				continue;
			}
			//scanning for line terminator
			else if (chars[i]=='\n' || chars[i] == '\r') {
				pos++;
				line++;
				//new posInLine for next token in new line will be 1
				posInLine = 1;
				//CR immediately followed by LF counts as one line terminator, not two
				if (chars[i] == '\r' && chars[i+1] == '\n') {
					pos++;
					i++;
				}
				i++;
				continue;
			}
			//case like space, tab, form feed, 
			else if (Arrays.asList(' ','\t','\f').contains(chars[i])) {
				i++;
				pos++;
				posInLine++;
			}
			//if a backslash found outside a string literal
			//else if (chars[i] == '\\'){
			//	throw new LexicalException(String.format("invalid backslash(\\) found at position %d", pos), pos);
			//	}
			//scanning integer literal
			else if (chars[i] == '0') {
				tokens.add(new Token(Kind.INTEGER_LITERAL, pos++, 1, line, posInLine++));
				i++;
				continue;
			}
			else if (chars[i] >= '1' && chars[i] <= '9') {
				StringBuilder int_literal = new StringBuilder();
				token_length = 0;
				while (chars[i] >= '0' && chars[i] <= '9') {
					int_literal.append(chars[i]);
					token_length++;
					i++;
				}
				//exception handling for invalid integer length
				try {
					Integer.parseInt(int_literal.toString());
				}
				catch(NumberFormatException e){
					throw new LexicalException(String.format(
							"The integer starting at position %d is longer than Java can accept", pos), pos);
				}
				
				tokens.add(new Token(Kind.INTEGER_LITERAL, pos, token_length, line, posInLine));
				pos = pos+token_length;
				posInLine = posInLine+token_length;
				continue;
				}
			/*for invalid inputs*/
			else {
				throw new LexicalException(String.format(
						"Invalid input (%c) found at position %d.", chars[i], pos), pos);
			}
			}
		
		//adding EOF token to the tokens list
		tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
		return this;
	}

	/**
	 * This method checks what kind of token has been created.
	 *
	 * @return Kind of token
	 */
	public Kind check_token(String temp_token) {
		ArrayList<String> keywords = new ArrayList<>(Arrays.asList(
				"x", "X", "y", "Y", "r", "R", "a", "A", "Z", "DEF_X",
		            "DEF_Y", "SCREEN", "cart_x", "cart_y", "polar_a", 
		            "polar_r", "abs", "sin", "cos", "atan", "log", "image",
		            "int", "boolean", "url", "file"));
		//System.out.println(temp_token);
		if(keywords.contains(temp_token)) {
			String temp_kind = "KW_"+temp_token;
			return Kind.valueOf(temp_kind);
		}
		else if (temp_token.equals("true") || temp_token.equals("false")) {
			return Kind.BOOLEAN_LITERAL;
		}
		else {
			return Kind.IDENTIFIER;
		}
	}

	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}
	
	
	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
