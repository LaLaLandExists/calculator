package com.terabyte.calculator;

import static com.terabyte.calculator.Token.Type.*;
import java.util.ArrayList;

public class Parser {
	@SuppressWarnings("serial")
	public static class ParseError extends RuntimeException {
	  public final Token where;
	  public final String msg;
	  
	  public ParseError(Token token, String msg) {
	    super("Parse Error");
	    this.where = token;
	    this.msg = msg;
	  }
	}
	
	private ArrayList<Token> tokens;
	private int current = 0;
	
	public Expression parse(String src) {
	  tokens = new Scanner(src).getTokens();
	  current = 0;
	  if (atEnd()) return null;
	  Expression expr = assignmentExpr();
	  if (!atEnd()) {
	    error("Expected end of expression.");
	  }
	  return expr;
	}
	
	private Expression assignmentExpr() {
	  if (check(IDENTIFIER) && checkNext(EQUAL)) {
	    Token name = advance();
	    advance(); // skip =
	    return new Expression.Assignment(name, termExpr());
	  }
	  if (isFunctionDecl()) {
	    ArrayList<Token> params = new ArrayList<>();
	    Token name = advance();
	    assert name.type == IDENTIFIER;
	    Token leftParen = advance(); // skip (
	    assert leftParen.type == LEFT_PAREN;
	    if (!check(RIGHT_PAREN)) {
	      do {
	        params.add(advance());
	      } while (match(COMMA));
	    }
	    Token rightParen = advance();
	    assert rightParen.type == RIGHT_PAREN;
	    consume(EQUAL, "Expected '=' after function declaration.");
	    return new Expression.Function(name, params, termExpr());
	  }
	  return termExpr();
	}
	private Expression termExpr() {
	  Expression left = factorExpr();
	  while (matchAny(PLUS, MINUS)) {
	    Token operator = previous();
	    Expression right = factorExpr();
	    left = new Expression.Binary(left, operator, right);
	  }
	  return left;
	}
	private Expression factorExpr() {
	  Expression left = unaryExpr();
	  while (matchAny(ASTERISK, SLASH, MOD)) {
	    Token operator = previous();
	    Expression right = unaryExpr();
	    left = new Expression.Binary(left, operator, right);
	  }
	  return left;
	}
	private Expression unaryExpr() {
	  if (matchAny(PLUS, MINUS)) {
	    Token operator = previous();
	    return new Expression.Unary(operator, unaryExpr());
	  }
	  Expression expr = exponentExpr();
	  if (match(MODULO)) {
	    Token operator = previous();
	    expr = new Expression.Unary(operator, expr);
	  }
	  return expr;
	}
	private Expression exponentExpr() {
	  Expression left = callExpr();
	  while (match(CARET)) {
	    Token operator = previous();
	    Expression right = unaryExpr();
	    left = new Expression.Binary(left, operator, right);
	  }
	  return left;
	}
	private Expression callExpr() {
	  if (check(IDENTIFIER) && checkNext(LEFT_PAREN)) {
	    ArrayList<Expression> args = new ArrayList<>();
	    Token name = advance();
	    advance();
	    if (!check(RIGHT_PAREN)) {
	      do {
	        args.add(termExpr());
	      } while (match(COMMA));
	    }
	    consume(RIGHT_PAREN, "Expected ')' after call arguments.");
	    return new Expression.Call(name, args);
	  }
	  return literalExpr();
	}
	private Expression literalExpr() {
	  if (atEnd()) {
	    error("Unexpected end of string.");
	  }
	  
	  if (match(NUMBER)) {
	    return new Expression.Literal(previous());
	  } else if (match(IDENTIFIER)) {
	    return new Expression.Variable(previous());
	  } else if (match(LEFT_PAREN)) {
	    Expression grouped = termExpr();
	    consume(RIGHT_PAREN, "Expected ')' to close '('.");
	    return grouped;
	  } else {
	    error("Expected expression.");
	  }
	  return null;
	}
	
	private Token getAtIndex(int index) {
	  if (index >= tokens.size()) {
	    return null;
	  }
	  return tokens.get(index);
	}
	// checks if src matches <ID>"("(<ID>(","<ID>)*)?")" "="
	private boolean isFunctionDecl() {
	  if (check(IDENTIFIER) && checkNext(LEFT_PAREN)) {
	    int index = current + 2;
	    if (getAtIndex(index).type != RIGHT_PAREN) {
	      while (index < tokens.size()) {
	        Token id = getAtIndex(index++);
	        if (id == null || id.type != IDENTIFIER) {
	          return false;
	        }
	      
	        Token delim = getAtIndex(index++);
	        if (delim != null) {
	          if (delim.type == RIGHT_PAREN) {
	            break;
	          }
	          if (delim.type != COMMA) {
	            return false;
	          }
	        }
	      }
	    } else {
	      index++;
	    }
	    Token equalMarker = getAtIndex(index);
	    if (equalMarker == null) {
	      return false;
	    }
	    if (equalMarker.type == EQUAL) {
	      return true;
	    }
	  }
	  return false;
	}
	
	private Token consume(Token.Type type, String ifError) {
	  if (!check(type)) {
	    error(ifError);
	  }
	  return advance();
	}
	private boolean check(Token.Type type, int distance) {
	  return peek(distance).type == type;
	}
	private boolean check(Token.Type type) {
	  return check(type, 0);
	}
	private boolean checkNext(Token.Type type) {
	  return check(type, 1);
	}
	private boolean match(Token.Type type) {
	  if (check(type)) {
	    advance();
	    return true;
	  }
	  return false;
	}
	private boolean matchAny(Token.Type... types) {
	  for (Token.Type type : types) {
	    if (check(type)) {
	      advance();
	      return true;
	    }
	  }
	  return false;
	}
	private void error(String msg) {
	  errorAt(peek(), msg);
	}
	private void errorAt(Token where, String msg) {
	  throw new ParseError(where, msg);
	}
	private Token peek(int distance) {
	  int target = current + distance;
	  if (target >= tokens.size()) {
	    return tokens.get(tokens.size() - 1);
	  }
	  return tokens.get(target);
	}
	private Token peek() {
	  return peek(0);
	}
	private Token previous() {
	  return tokens.get(current - 1);
	}
	private Token advance() {
	  if (!atEnd()) {
	    current++;
	  }
	  return previous();
	}
	private boolean atEnd() {
	  return current >= tokens.size();
	}
}