package com.terabyte.calculator;

import static com.terabyte.calculator.Token.Type.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Scanner {
	private String src;
	private int start;
	private int current;
	private final ArrayList<Token> tokens = new ArrayList<>();
	private static final HashMap<String, Token.Type> keywords = new HashMap<>();
	
	static {
	  keywords.put("mod", MOD);
	}
	
	@SuppressWarnings("serial")
	public static class LexicalError extends RuntimeException {
	  public final String where;
	  public final String msg;
	  
	  public LexicalError(String where, String msg) {
	    super("Lexical Error");
	    this.where = where;
	    this.msg = msg;
	  }
	}
	
	private void reset(String src) {
	  this.src = src;
	  tokens.clear();
	  start = 0;
	  current = 0;
	}
	public Scanner(String src) {
	  reset(src);
	}
	
	public ArrayList<Token> getTokens() {
	  while (!atEnd()) {
	    Token token = nextToken();
	    assert token != null;
	    tokens.add(token);
	  }
	  return tokens;
	}
	private Token nextToken() {
	  skipIgnoredChars();
	  if (atEnd()) {
	    return null;
	  }
	  start = current;
	  char ch = advance();
	  switch (ch) {
	    case '+': return makeToken(PLUS);
	    case '-': return makeToken(MINUS);
	    case '*': return makeToken(ASTERISK);
	    case '/': return makeToken(SLASH);
	    case '%': return makeToken(MODULO);
	    case '^': return makeToken(CARET);
	    case '(': return makeToken(LEFT_PAREN);
	    case ')': return makeToken(RIGHT_PAREN);
	    case '=': return makeToken(EQUAL);
	    case ',': return makeToken(COMMA);
	    default:
	      if (Character.isLetter(ch) || ch == '_') {
	        return handleIdentifiers();
	      } else if (Character.isDigit(ch)) {
	        return handleNumbers();
	      } else {
	        error("Invalid character.");
	      }
	  }
	  return null;
  }
	
	private void error(String msg) {
	  throw new LexicalError(src.subSequence(start, current).toString(), msg);
	}
	private Token makeToken(Token.Type type) {
	  return new Token(type, src.subSequence(start, current).toString());
	}
	private Token handleNumbers() {
	  while (Character.isDigit(peek())) {
	    advance();
	  }
	  if (peek() == '.') {
	    advance();
	    while (Character.isDigit(peek())) {
	      advance();
	    }
	  }
	  if (Character.toUpperCase(peek()) == 'E') {
	    advance(); // consume E
	    if (check('+') || check('-')) {
	      advance();
	    }
	    if (!Character.isDigit(advance())) {
	      error("Ill-formed number literal.");
	    }
	    while (Character.isDigit(peek())) {
	      advance();
	    }
	  }
	  return makeToken(NUMBER);
	}
	private Token handleIdentifiers() {
	  while (Character.isLetterOrDigit(peek()) || peek() == '_') {
	    advance();
	  }
	  String lexeme = src.subSequence(start, current).toString();
	  Token.Type type = keywords.get(lexeme);
	  return new Token(type != null ? type : IDENTIFIER, lexeme);
	}
	private char advance() {
	  if (!atEnd()) {
	    current++;
	  }
	  return src.charAt(current - 1);
	}
	
	private char peek(int distance) {
	  int target = current + distance;
	  return target >= src.length() ? '\0' : src.charAt(target);
	}
	private char peek() {
	  return peek(0);
	}
	private boolean check(char ch) {
	  return peek() == ch;
	}
	private void skipIgnoredChars() {
	  while (!atEnd()) {
	    switch (peek()) {
	      case ' ':
	      case '\n':
	      case '\t':
	        advance();
	        break;
	      default: return;
	    }
	  }
	}
	private boolean atEnd() {
	  return src.length() <= current;
	}
}