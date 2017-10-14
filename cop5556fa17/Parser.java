package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
//import java.util.Map;
import java.util.List;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.AST.*;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}

	HashMap<Object, List<Kind>> predicts = new HashMap<Object, List<Kind>>();

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
		
		predicts.put("program_Declaration", Arrays.asList(Kind.KW_int, 
				Kind.KW_boolean,Kind.KW_image, Kind.KW_url, Kind.KW_file));
		predicts.put("dec_variableDeclaration", Arrays.asList(Kind.KW_int, 
				Kind.KW_boolean));
		predicts.put("dec_imageDeclaration", Arrays.asList(Kind.KW_image));
		predicts.put("dec_sourceSinkDeclaration", Arrays.asList(Kind.KW_url, 
				Kind.KW_file));
		predicts.put("program_Statement", Arrays.asList(Kind.IDENTIFIER));
		predicts.put("stm_assignmentStatement", Arrays.asList(Kind.LSQUARE, 
				Kind.OP_ASSIGN));
		predicts.put("stm_imageOutStatement", Arrays.asList(Kind.OP_RARROW));
		predicts.put("stm_imageInStatement", Arrays.asList(Kind.OP_LARROW));
		predicts.put("sink", Arrays.asList(Kind.IDENTIFIER, Kind.KW_SCREEN));
		predicts.put("XySelector", Arrays.asList(Kind.KW_x));
		predicts.put("RaSelector", Arrays.asList(Kind.KW_r));
		predicts.put("lhs", Arrays.asList(Kind.LSQUARE));
		predicts.put("UENPM_Primary", Arrays.asList(Kind.INTEGER_LITERAL, 
				Kind.LPAREN, Kind.KW_sin, Kind.KW_cos, Kind.KW_atan, 
				Kind.KW_abs, Kind.KW_cart_x, Kind.KW_cart_y, Kind.KW_polar_a,
				Kind.KW_polar_r, Kind.BOOLEAN_LITERAL));
		predicts.put("UNEPM_IOPSE", Arrays.asList(Kind.IDENTIFIER));
		predicts.put("UNEPM_rest", Arrays.asList( Kind.KW_x, Kind.KW_y, 
				Kind.KW_r, Kind.KW_a, Kind.KW_X, Kind.KW_Y, Kind.KW_Z, 
				Kind.KW_A, Kind.KW_R, Kind.KW_DEF_X, Kind.KW_DEF_Y));
	}
	
	/*consumes a token and goes to next token in the tokens list*/
	void consume() {
		t = scanner.nextToken();
		}

	/*function to match terminals and consume if matches, otherwise throw exception*/
	 void match(Kind kind) throws SyntaxException {
		 if(t.kind == kind){
			 consume();
			 } 
		 else  {
			 throw new SyntaxException(t, String.format(
						"Parsing Error: Expected %s but got %s at pos %d.", 
						kind.toString(), (t.kind).toString(), t.pos));
		 }
	 }
	
	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	//public void parse() throws SyntaxException {
	 public Program parse() throws SyntaxException{
		Program p = program();
		matchEOF();
		return p;
	}
	
	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		//TODO  implement this
		Token firstToken = null;
		Token name = null;
		Declaration dec = null;
		Statement stat = null;
		Program p = null;
		ArrayList<ASTNode> decsAndStatements= new ArrayList<ASTNode>();
		//FIRST of program has to be identifier
		firstToken = t;
		name = t;
		match(Kind.IDENTIFIER);
		//check for IDENT being followed by Declaration or Statement
		while (predicts.get("program_Declaration").contains(t.kind) 
				|| predicts.get("program_Statement").contains(t.kind)) {
			//check if IDENT followed by Declaration
			if (predicts.get("program_Declaration").contains(t.kind)){
				dec = declaration();
				decsAndStatements.add(dec);
				}
			//check if IDENT followed by Statement, and add it to decsAndStatements
			else if (predicts.get("program_Statement").contains(t.kind)) {
				stat = statement();
				decsAndStatements.add(stat);
				}
			//a SEMI should exist after any Declaration or Statement
			match(Kind.SEMI);
			}
		//create a new object of Program, and return it
		p = new Program(firstToken, name, decsAndStatements);
		return p;
		}

	/**
	 * Declaration ::= VariableDeclaration | ImageDeclaration | SourceSinkDeclaration 
	 * 
	 * @throws SyntaxException
	 */
	Declaration declaration() throws SyntaxException {
		//checks if token belongs to PREDICT(Declaration ::= VariableDeclaration)
		Declaration dec = null;
		if (predicts.get("dec_variableDeclaration").contains(t.kind)) {
			dec = variableDeclaration();
		}
		//checks for PREDICT(Declaration ::= ImageDeclaration)
		else if (predicts.get("dec_imageDeclaration").contains(t.kind)) {
			dec = imageDeclaration();
		}
		//checks for PREDICT(Declaration ::= SourceSinkDeclaration)
		else if (predicts.get("dec_sourceSinkDeclaration").contains(t.kind)) {
			dec = sourceSinkDeclaration();
		}
		return dec;
	}

	/**
	 * VariableDeclaration  ::=  VarType IDENTIFIER  (  OP_ASSIGN  Expression  | ε )
	 * 
	 * @throws SyntaxException
	 */
	Declaration_Variable variableDeclaration() throws SyntaxException {
		//already found VarType, so can consume it
		Token firstToken = null;
		Token type = null;
		Token name = null;
		Expression e = null;
		firstToken = t;
		type = t;
		consume();
		name = t;
		match(Kind.IDENTIFIER);
		if (t.kind == Kind.OP_ASSIGN) {
				consume();
				e = expression();
				}
		Declaration_Variable DV = new Declaration_Variable(firstToken, type, name, e);
		return DV;
	}
	
	/**
	 * ImageDeclaration ::=  KW_image  (LSQUARE Expression COMMA Expression RSQUARE | ε) 
	 * IDENTIFIER ( OP_LARROW Source | ε ) 
	 * 
	 * @throws SyntaxException
	 */
	Declaration_Image imageDeclaration() throws SyntaxException {
		//already found KW_image
		Token firstToken = null;
		Expression xSize = null; 
		Expression ySize = null; 
		Token name = null;
		Source source = null;
		firstToken = t;
		consume();
		if (t.kind == Kind.LSQUARE) {
			consume();
			{
				xSize = expression();
				match(Kind.COMMA);
				ySize = expression();
				match(Kind.RSQUARE);
			}
		}
		name = t;
		match(Kind.IDENTIFIER);
		if (t.kind == Kind.OP_LARROW) {
			consume();
			source  = source();
		}
		Declaration_Image DI = new Declaration_Image(firstToken, xSize, ySize, 
				name, source);
		return DI;
	}

	/**
	 * SourceSinkDeclaration ::= SourceSinkType IDENTIFIER  OP_ASSIGN  Source
	 * 
	 * @throws SyntaxException
	 */
	Declaration_SourceSink sourceSinkDeclaration() throws SyntaxException{
		//already found SourceSinkType
		Token firstToken = null;
		Token type = null;
		Token name = null;
		Source source = null;
		firstToken = t;
		type = t;
		consume();
		name = t;
		match(Kind.IDENTIFIER);
		match(Kind.OP_ASSIGN);
		source = source();
		Declaration_SourceSink DSS= new Declaration_SourceSink(firstToken, 
				type, name, source);
		return DSS;
	}
	
	/**
	 * Source ::= STRING_LITERAL | OP_AT Expression | IDENTIFIER 
	 * 
	 * @throws SyntaxException
	 */
	Source source() throws SyntaxException {
		Token firstToken = null;
		Expression paramNum = null;
		Source s = null;
		firstToken = t;
		if (t.kind == Kind.STRING_LITERAL) {
			s = new Source_StringLiteral(t, t.getText());
			consume();
		}
		else if (t.kind == Kind.OP_AT) {
			consume();
			paramNum = expression();
			s = new Source_CommandLineParam(firstToken, paramNum);
		}
		else {
			s = new Source_Ident(t,t);
			match(Kind.IDENTIFIER);
		}
		return s;
	}
	
	/**
	 * Statement  ::= AssignmentStatement | ImageOutStatement  | ImageInStatement 
	 * 
	 * @throws SyntaxException
	 */
	Statement statement() throws SyntaxException {
		Statement stat = null;
		Token firstToken = t;
		Token name = t;
		//already know this is IDENT, so consume it
		consume();
		//checks for PREDICT(Declaration ::= ImageOutStatement)
		if (predicts.get("stm_imageOutStatement").contains(t.kind)) {
			stat = imageOutStatement(firstToken, name);
		}
		//checks for PREDICT(Declaration ::= ImageInStatement)
		else if (predicts.get("stm_imageInStatement").contains(t.kind)) {
			stat = imageInStatement(firstToken, name);
		}
		//checks if token belongs to PREDICT(Statement ::= AssignmentStatement)
		else if (predicts.get("stm_assignmentStatement").contains(t.kind)) {
			stat = assignmentStatement(firstToken, name);
			}
		return stat;
	}
	
	/**
	 * ImageOutStatement ::= OP_RARROW Sink 
	 * 
	 * @throws SyntaxException
	 */
	Statement_Out imageOutStatement(Token firstToken, Token name) throws SyntaxException {
		Sink sink = null;
		Statement_Out SO = null;
		//already found OP_RARROW, so consume it
		consume();
		//check if found PREDICTS of Sink
		if (predicts.get("sink").contains(t.kind)) {
			if (t.kind == Kind.IDENTIFIER) {
				sink = new Sink_Ident(firstToken, t);
			}
			else {
				sink = new Sink_SCREEN(firstToken);
			}
			consume();
		}
		else {
			match(Kind.IDENTIFIER);
		}
		SO = new Statement_Out(firstToken, name, sink);
		return SO;
	}
	
	/**
	 * ImageInStatement ::= OP_LARROW Source
	 * 
	 * @throws SyntaxException
	 */
	Statement_In imageInStatement(Token firstToken, Token name) throws SyntaxException {
		Source source = null;
		Statement_In SI = null;
		//already found OP_LARROW, so consume it
		consume();
		source = source();
		SI = new Statement_In(firstToken, name, source);
		return SI;
	}
	
	/**
	 * AssignmentStatement ::= Lhs OP_ASSIGN Expression
	 * 
	 * @throws SyntaxException
	 */
	Statement_Assign assignmentStatement(Token firstToken, Token name) throws SyntaxException {
		LHS lhs = null;
		Expression e = null;
		Statement_Assign SA = null;
		lhs = lhs(firstToken, name);
		match(Kind.OP_ASSIGN);
		e = expression();
		SA = new Statement_Assign(firstToken, lhs, e);
		return SA;
	}
	
	/**
	 * Lhs::=   IDENTIFIER (LSQUARE LhsSelector RSQUARE | e)
	 * 
	 * @throws SyntaxException
	 */
	LHS lhs(Token firstToken, Token name) throws SyntaxException {
		//already found LSQUARE, so consume it
		Index index = null;
		LHS lhs = null;
		if (predicts.get("lhs").contains(t.kind)) {
			consume();
			index = lhsSelector();
			match(Kind.RSQUARE);
		}
		lhs = new LHS(firstToken, name, index);
		return lhs;
	}
	
	/**
	 * LhsSelector ::= LSQUARE  ( XySelector  | RaSelector  )   RSQUARE
	 * 
	 * @throws SyntaxException
	 */
	Index lhsSelector() throws SyntaxException {
		Index index = null;
		match(Kind.LSQUARE);
		if (predicts.get("XySelector").contains(t.kind)) {
			index = xySelector();
		}
		else {
			index = raSelector();
		}
		match(Kind.RSQUARE);
		return index;
	}
	
	/**
	 * XySelector ::= KW_x COMMA KW_y
	 *
	 * @throws SyntaxException
	 */
	Index xySelector() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Expression e1 = null;
		Index i = null;
		firstToken = t;
		e0 = new Expression_PredefinedName(t, t.kind);
		match(Kind.KW_x);
		match(Kind.COMMA);
		e1 = new Expression_PredefinedName(t, t.kind);
		match(Kind.KW_y);
		i = new Index(firstToken, e0, e1);
		return i;
	}
	
	/**
	 * RaSelector ::= KW_r COMMA KW_A
	 * 
	 * @throws SyntaxException
	 */
	Index raSelector() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Expression e1 = null;
		Index i = null;
		e0 = new Expression_PredefinedName(t, t.kind);
		match(Kind.KW_r);
		match(Kind.COMMA);
		e1 = new Expression_PredefinedName(t, t.kind);
		match(Kind.KW_A);
		i = new Index(firstToken, e0, e1);
		return i;
	}
	
	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		//TODO implement this.
		Token firstToken = null;
		Expression condition = null;
		Expression trueExpression = null;
		Expression falseExpression = null;
		Expression e = null;
		firstToken = t;
		condition = orExpression();
		e = condition;
		if (t.kind == Kind.OP_Q) {
			consume();
			trueExpression = expression();
			match(Kind.OP_COLON);
			falseExpression = expression();
			Expression_Conditional EC = new Expression_Conditional(firstToken, 
					condition, trueExpression, falseExpression);
			e = EC;
		}
		return e;
	}
	
	/**
	 * OrExpression ::= AndExpression   (  OP_OR  AndExpression)*
	 * 
	 * @throws SyntaxException
	 */
	Expression orExpression() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		firstToken = t;
		e0 = andExpression();
		while(t.kind == Kind.OP_OR) {
			op = t;
			consume();
			e1 = andExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	/**
	 * AndExpression ::= EqExpression ( OP_AND  EqExpression )*
	 * 
	 * @throws SyntaxException
	 */
	Expression andExpression() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		firstToken = t;
		e0 = eqExpression();
		while(t.kind == Kind.OP_AND) {
			op = t;
			consume();
			e1 = eqExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	/**
	 * EqExpression ::= RelExpression  (  (OP_EQ | OP_NEQ )  RelExpression )*
	 * 
	 * @throws SyntaxException
	 */
	Expression eqExpression() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		firstToken = t;
		e0 = relExpression();
		while(t.kind == Kind.OP_EQ || t.kind == Kind.OP_NEQ) {
			op = t;
			consume();
			e1 = relExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	/**
	 *RelExpression ::= AddExpression (  ( OP_LT  | OP_GT |  OP_LE  | OP_GE )   AddExpression)*
	 * 
	 * @throws SyntaxException
	 */
	Expression relExpression() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		firstToken = t;
		e0 = addExpression();
		while(Arrays.asList(
				Kind.OP_LT, Kind.OP_GT, Kind.OP_LE, Kind.OP_GE).contains(t.kind)) {
			op = t;
			consume();
			e1 = addExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	/**
	 *AddExpression ::= MultExpression   (  (OP_PLUS | OP_MINUS ) MultExpression )*
	 * 
	 * @throws SyntaxException
	 */
	Expression addExpression() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		firstToken = t;
		e0 = multExpression();
		while(t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS) {
			op = t;
			consume();
			e1 = multExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	/**
	 *MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV  | OP_MOD ) UnaryExpression )*
	 * 
	 * @throws SyntaxException
	 */
	Expression multExpression() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		firstToken = t;
		e0 = unaryExpression();
		while(t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || 
				t.kind == Kind.OP_MOD) {
			op = t;
			consume();
			e1 = unaryExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	/**
	 *UnaryExpression ::= OP_PLUS UnaryExpression | OP_MINUS UnaryExpression | 
	 *UnaryExpressionNotPlusMinus
	 * 
	 * @throws SyntaxException
	 */
	Expression unaryExpression() throws SyntaxException {
		Token firstToken = null;
		Token op = null;
		Expression e = null;
		Expression e1 = null;
		firstToken = t;
		if (t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS) {
			op = t;
			consume();
			e = unaryExpression();
			e1 = new Expression_Unary(firstToken, op, e);
		}
		else {
			e1 = unaryExpressionNotPlusMinus();
		}
		return e1;
	}
	
	/**
	 *UnaryExpressionNotPlusMinus ::=  OP_EXCL  UnaryExpression  | 
	 *Primary | IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r 
	 *| KW_a | KW_X | KW_Y | KW_Z | KW_A | KW_R | KW_DEF_X | KW_DEF_Y
	 * 
	 * @throws SyntaxException
	 */
	Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		Token firstToken = null;
		Token op = null;
		Expression e = null;
		Expression e1 = null;
		firstToken = t;
		if (predicts.get("UENPM_Primary").contains(t.kind)) {
			e1 = primary();
		}
		else if (predicts.get("UNEPM_IOPSE").contains(t.kind)) {
			e1 = identOrPixelSelectorExpression();
		}
		else if (predicts.get("UNEPM_rest").contains(t.kind)) {
			Expression_PredefinedName epn = new Expression_PredefinedName(t, t.kind);
			e1 = epn;
			consume();
		}
		else {
			op = t;
			match(Kind.OP_EXCL);
			e = unaryExpression();
			e1 = new Expression_Unary(firstToken, op, e);
			}
		return e1;
		}
	
	/**
	 *Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | 
	 *FunctionName FunctionApplication | BOOLEAN_LITERAL 
	 * 
	 * @throws SyntaxException
	 */
	Expression primary() throws SyntaxException{
		Token firstToken = null;
		Expression e = null;
		firstToken = t;
		//Primary ::= INTEGER_LITERAL
		if (t.kind == Kind.INTEGER_LITERAL) {
			e = new Expression_IntLit(firstToken, t.intVal());
			consume();
		}
		//Primary ::= LPAREN Expression RPAREN
		else if (t.kind == Kind.LPAREN) {
			consume();
			e = expression();
			match(Kind.RPAREN);
		}
		//Primary ::= BOOLEAN_LITERAL
		else if (t.kind == Kind.BOOLEAN_LITERAL) {
			e = new Expression_BooleanLit(firstToken, Boolean.valueOf(t.getText()));
			consume();
		}
		//Primary ::= FunctionApplication
		else {
			e = functionApplication();
		}
		return e;
	}
	
	/**
	 *IdentOrPixelSelectorExpression::=  IDENTIFIER LSQUARE Selector RSQUARE   | IDENTIFIER
	 * 
	 * @throws SyntaxException
	 */
	Expression identOrPixelSelectorExpression() throws SyntaxException{
		//IDENT has already been found, so consume it
		Token firstToken = null;
		Token name = null;
		Index index  = null;
		Expression e = null;
		firstToken = t;
		name = t;
		e = new Expression_Ident(firstToken, name);
		consume();
		if (t.kind == Kind.LSQUARE) {
			consume();
			index = selector();
			match(Kind.RSQUARE);
			e = new Expression_PixelSelector(firstToken, name, index);
		}
		return e;
	}
	
	/**
	 *FunctionApplication ::= FunctionName LPAREN Expression RPAREN  | 
	 *						FunctionName LSQUARE Selector RSQUARE 
	 * 
	 * @throws SyntaxException
	 */
	Expression functionApplication() throws SyntaxException{
		Token firstToken = null;
		Kind function = null;
		Expression e = null;
		firstToken = t;
		function = functionName();
		if (t.kind == Kind.LPAREN) {
			consume();
			Expression arg = expression();
			e = new Expression_FunctionAppWithExprArg(firstToken, function, arg);
			match(Kind.RPAREN);
		}
		else {
			match(Kind.LSQUARE);
			Index arg = selector();
			e = new Expression_FunctionAppWithIndexArg(firstToken, function, arg);
			match(Kind.RSQUARE);
		}
		return e;
	}
	
	/**
	 *FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | 
	 *KW_cart_x | KW_cart_y | KW_polar_a | KW_polar_r
	 * 
	 * @throws SyntaxException
	 */
	Kind functionName() throws SyntaxException{
		Kind function = null;
		if (Arrays.asList(Kind.KW_sin, Kind.KW_cos, 
				Kind.KW_atan, Kind.KW_abs, Kind.KW_cart_x, Kind.KW_cart_y, 
				Kind.KW_polar_a, Kind.KW_polar_r).contains(t.kind)) {
			function = t.kind;
			consume();
		}
		return function;
	}
	
	/**
	 *Selector ::=  Expression COMMA Expression
	 * 
	 * @throws SyntaxException
	 */
	Index selector() throws SyntaxException {
		Token firstToken = null;
		Expression e0 = null;
		Expression e1 = null;
		Index i = null;
		firstToken = t;
		e0 = expression();
		match(Kind.COMMA);
		e1 = expression();
		i = new Index(firstToken, e0, e1);
		return i;
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
