package com.terabyte.calculator;

import com.terabyte.calculator.Expression.*;

public class ExpressionPrinter implements ExpressionVisitor<String> {
  public String getString(Expression expr) {
    return expr.accept(this);
  }
  
	@Override
	public String visit(Literal expr) {
		return expr.literal.lexeme;
	}

	@Override
	public String visit(Unary expr) {
		String operand = expr.operand.accept(this);
		return String.format("(u[%s]: %s)", expr.operator.lexeme, operand);
	}

	@Override
	public String visit(Binary expr) {
		String left = expr.left.accept(this);
		String right = expr.right.accept(this);
		return String.format("(b[%s]: %s, %s)", expr.operator.lexeme, left, right);
	}

	@Override
	public String visit(Group expr) {
		return String.format("(%s)", expr.expression.accept(this));
	}

	@Override
	public String visit(Variable expr) {
		return String.format("(var: %s)", expr.name.lexeme);
	}

	@Override
	public String visit(Assignment expr) {
		String value = expr.value.accept(this);
		return String.format("%s <- %s", expr.target.lexeme, value);
	}

	@Override
	public String visit(Function expr) {
		StringBuilder builder = new StringBuilder();
		builder.append(expr.name.lexeme)
		       .append("(");
		for (int i = 0; i < expr.params.size(); i++) {
		  builder.append(expr.params.get(i).lexeme);
		  if (i != expr.params.size() - 1) {
		    builder.append(", ");
		  }
		}
		builder.append(") -> ");
		builder.append(expr.expression.accept(this));
		return builder.toString();
	}
	
	@Override
	public String visit(Call expr) {
	  StringBuilder builder = new StringBuilder();
	  builder.append("(call ")
	         .append(expr.name.lexeme)
	         .append("(");
	  int cap = expr.args.size();
	  for (int i = 0; i < cap; i++) {
	    builder.append(expr.args.get(i).accept(this));
	    if (i != cap - 1) {
	      builder.append(", ");
	    }
	  }
	  builder.append("))");
	  return builder.toString();
	}
}