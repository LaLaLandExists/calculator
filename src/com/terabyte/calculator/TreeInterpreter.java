package com.terabyte.calculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.terabyte.calculator.Expression.*;
import com.terabyte.calculator.ForeignFunction.BadFFI_Call;
import static com.terabyte.calculator.Token.Type.*;

public class TreeInterpreter implements ExpressionVisitor<Double> {
	private final Parser parser = new Parser();
	private Environment environment = new Environment();
	private void pushEnv(ArrayList<Token> predefs, ArrayList<Expression> args) {
	  Environment env = new Environment(environment);
	  environment = env;
	  if (predefs.size() != args.size()) {
	    throw new ValueError("Invalid arity for function call.");
	  }
	  for (int i = 0; i < predefs.size(); i++) {
	    Token param = predefs.get(i);
	    Double values = args.get(i).accept(this);
	    environment.defineDouble(param.lexeme, values);
	  }
	}
	private void popEnv() {
	  assert environment.enclosing != null;
	  environment = environment.enclosing;
	}
	
	private void addForeign(String name, ForeignFunction ffn) {
	  environment.defineForeign(name, ffn);
	}
	private void defineFunction(String def, String expr) {
	  this.evaluate(parser.parse(String.format("%s=%s", def, expr)));
	}
	
	public TreeInterpreter() {
	  addForeign("dump_env", new ForeignFunction(0) {
	  	@Override
	  	public Double execute(TreeInterpreter state, ArrayList<Double> args) {
	  		// Dump environment to calc_dump.txt
	  		StringBuilder builder = new StringBuilder().append("{\n");
	  		
	  		for (var ent : state.environment.getMap().entrySet()) {
	  		  builder.append(String.format("  \"%s\": ", ent.getKey()));
	  		  Object obj = ent.getValue();
	  		  if (obj instanceof Function) {
	  		    builder.append(new ExpressionPrinter().getString((Function) obj));
	  		  } else if (obj instanceof ForeignFunction) {
	  		    builder.append("<internal function>");
	  		  } else {
	  		    builder.append(String.format("%g", (Double) obj));
	  		  }
	  		  builder.append(",\n");
	  		}
	  		
	  		builder.append("}\n");
	  		byte[] x = builder.toString().getBytes();
	  		try {
	  		  Files.write(Paths.get("calc_dump.txt"), x);
	  		} catch (IOException err) {
					throw new DumpError();
	  		}
	  		return null;
	  	}    
	  });
	  
	  addForeign("exit", new ForeignFunction(0) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	System.exit(0);
		  	return 0.0;
	  	}    
	  });
	  
	  addForeign("abs", new ForeignFunction(1) {
	  	@Override
	  	public Double execute(TreeInterpreter state, ArrayList<Double> args) {
			  return Math.abs(args.get(0));
	  	}  
	  });
	  
	  addForeign("sin", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.sin(args.get(0));
	  	}    
	  });
	  
	  addForeign("cos", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.cos(args.get(0));
	  	}    
	  });
	  
	  addForeign("tan", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.tan(args.get(0));
	  	}    
	  });
	  
	  addForeign("asin", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.asin(args.get(0));
	  	}    
	  });
	  
	  addForeign("acos", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.acos(args.get(0));
	  	}    
	  });
	  
	  addForeign("atan", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.atan(args.get(0));
	  	}    
	  });
	  
	  addForeign("sinh", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.sinh(args.get(0));
	  	}    
	  });
	  
	  addForeign("cosh", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.cosh(args.get(0));
	  	}    
	  });
	  
	  addForeign("tanh", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.tanh(args.get(0));
	  	}    
	  });
	  
	  addForeign("ln", new ForeignFunction(1) {
	  	@Override
		  public Double execute(TreeInterpreter state, ArrayList<Double> args) {
		  	return Math.log(args.get(0));
	  	}    
	  });

	  defineFunction("log(base, x)", "ln(x)/ln(base)");
	  defineFunction("sqrt(x)", "root(x, 2)");
	  defineFunction("root(x, r)", "x^(1/r)");
	  environment.defineDouble("π", Math.PI);
		environment.defineDouble("pi", Math.PI);
	  environment.defineDouble("e", Math.E);
	}
	
	public Double evaluate(Expression expr) {
	  if (expr == null) {
	    return null;
	  }
	  Double result;
	  result = expr.accept(this);
	  if (result != null) {
	    environment.defineDouble("R", result);
	  }
	  return result;
	}
	
	private static <T> void assertNotNull(T obj) {
	  if (obj == null) {
	    throw new ValueError("Value cannot be null.");
	  }
	}
	@SuppressWarnings("serial")
	public static class ValueError extends RuntimeException {
	  public final String what;
	  public ValueError(String err) {
	    super("Value Error");
	    what = err;
	  }
	}

	@Override
	public Double visit(Literal expr) {
		assert expr.literal.type == NUMBER;
		return Double.parseDouble(expr.literal.lexeme);
	}

	@Override
	public Double visit(Unary expr) {
		var res = expr.operand.accept(this);
		assertNotNull(res);
		switch (expr.operator.type) {
		  case MINUS:
		    return -res;
		  case MODULO:
		    return res / 100.0;
		  default: throw new AssertionError();
		}
	}

	@Override
	public Double visit(Binary expr) {
		Double left = expr.left.accept(this);
		Double right = expr.right.accept(this);
		assertNotNull(left);
		assertNotNull(right);
		switch (expr.operator.type) {
		  case PLUS:
		    return left + right;
		  case MINUS:
		    return left - right;
		  case ASTERISK:
		    return left * right;
		  case SLASH:
		    if (right == 0) {
		      throw new ValueError("Division by zero is undefined.");
		    }
		    return left / right;
		  case MOD:
		    if (right == 0) {
		      throw new ValueError("Division by zero is undefined.");
		    }
		    return left % right;
		  case CARET:
		    return Math.pow(left, right);
		  default: throw new AssertionError();
		}
	}

	@Override
	public Double visit(Group expr) {
		return expr.expression.accept(this);
	}

	@Override
	public Double visit(Variable expr) {
		return environment.getDouble(expr.name.lexeme);
	}

	@Override
	public Double visit(Assignment expr) {
		Double value = expr.value.accept(this);
		assertNotNull(value);
		environment.defineDouble(expr.target.lexeme, value);
		return null;
	}

	@Override
	public Double visit(Function expr) {
		environment.defineFunction(expr.name.lexeme, expr);
		return null;
	}

	@Override
	public Double visit(Call expr) {
		Object func = environment.getFunction(expr.name.lexeme);
		Double n;
		if (func instanceof Function) {
		  Function fn = (Function) func;
		  pushEnv(fn.params, expr.args);
		  n = fn.expression.accept(this);
		  popEnv();
		} else if (func instanceof ForeignFunction) {
		  ArrayList<Double> values = new ArrayList<>();
		  for (Expression value : expr.args) {
		    values.add(value.accept(this));
		  }
		  ForeignFunction fn = (ForeignFunction) func;
		  fn.arityCheck(expr.args.size());
		  try {
		    n = fn.execute(this, values);
		  } catch (Throwable err) {
		    throw new BadFFI_Call("Something went wrong.");
		  }
		} else {
		  throw new ValueError(String.format("Cannot call '%s'", expr.name.lexeme));
		}
		
		return n;
	}
}