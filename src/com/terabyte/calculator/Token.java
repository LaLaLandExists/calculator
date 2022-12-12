package com.terabyte.calculator;

public class Token {
	public static enum Type {
	  NUMBER,
	  PLUS,
	  MINUS,
	  ASTERISK,
	  SLASH,
	  CARET,
	  MODULO,
	  EQUAL,
	  LEFT_PAREN,
	  RIGHT_PAREN,
	  IDENTIFIER,
	  COMMA,
	  
	  MOD,
	}
	
	public final Type type;
	public final String lexeme;
	
	public Token(Type type, String lexeme) {
	  this.type = type;
	  this.lexeme = lexeme;
	}
	public String toString() {
	  return String.format("%s -> '%s'", type.toString(), lexeme);
	}
}