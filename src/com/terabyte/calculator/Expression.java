package com.terabyte.calculator;

import java.util.ArrayList;

public abstract class Expression {
	public abstract <R> R accept(ExpressionVisitor<R> visitor);
	
	public static class Literal extends Expression {
	  public final Token literal;
	  
	  public Literal(Token token) {
	    literal = token;
	  }

  	@Override
	  public <R> R accept(ExpressionVisitor<R> visitor) {
	  	return visitor.visit(this);
  	}	  
	}
	
	public static class Unary extends Expression {
	  public final Token operator;
	  public final Expression operand;
	  
	  public Unary(Token operator, Expression operand) {
	    this.operator = operator;
	    this.operand = operand;
	  }

	  @Override
	  public <R> R accept(ExpressionVisitor<R> visitor) {
		  return visitor.visit(this);
	  }
	}
	
	public static class Binary extends Expression {
	  public final Token operator;
	  public final Expression left;
	  public final Expression right;
	  
	  public Binary(Expression left, Token operator, Expression right) {
	    this.left = left;
	    this.operator = operator;
	    this.right = right;
	  }

  	@Override
	  public <R> R accept(ExpressionVisitor<R> visitor) {
		  return visitor.visit(this);
	  }
	}
	
	public static class Group extends Expression {
	  public final Expression expression;
	  
	  public Group(Expression expression) {
	    this.expression = expression;
	  }

    @Override
	  public <R> R accept(ExpressionVisitor<R> visitor) {
		  return visitor.visit(this);
	  }
	}
	
	public static class Variable extends Expression {
	  public final Token name;
	  
	  public Variable(Token name) {
	    this.name = name;
	  }

	  @Override
	  public <R> R accept(ExpressionVisitor<R> visitor) {
		  return visitor.visit(this);
	  }
	}
	
	public static class Assignment extends Expression {
	  public final Token target;
	  public final Expression value;
	  
	  public Assignment(Token target, Expression value) {
	    this.target = target;
	    this.value = value;
	  }

	  @Override
	  public <R> R accept(ExpressionVisitor<R> visitor) {
		  return visitor.visit(this);
	  }
	}
	
	public static class Function extends Expression {
	  public final Token name;
	  public final ArrayList<Token> params;
	  public final Expression expression;
	  
	  public Function(Token name, ArrayList<Token> params, Expression expression) {
	    this.name = name;
	    this.params = params;
	    this.expression = expression;
	  }
	  
  	@Override
	  public <R> R accept(ExpressionVisitor<R> visitor) {
	  	return visitor.visit(this);
  	}
	}
	
	public static class Call extends Expression {
	  public final Token name;
	  public final ArrayList<Expression> args;
	  
	  public Call(Token name, ArrayList<Expression> args) {
	    this.name = name;
	    this.args = args;
	  }
	  
	  @Override
	  public <R> R accept(ExpressionVisitor<R> visitor) {
	    return visitor.visit(this);
	  }
	}
}