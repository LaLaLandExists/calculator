package com.terabyte.calculator;

public interface ExpressionVisitor<R> {
	R visit(Expression.Literal expr);
	R visit(Expression.Unary expr);
	R visit(Expression.Binary expr);
	R visit(Expression.Group expr);
	R visit(Expression.Variable expr);
	R visit(Expression.Assignment expr);
	R visit(Expression.Function expr);
	R visit(Expression.Call expr);
}