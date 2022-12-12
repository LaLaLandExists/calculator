package com.terabyte.calculator;

import java.util.HashMap;

import com.terabyte.calculator.Expression.Function;

public class Environment {
	@SuppressWarnings("serial")
	public static class NameError extends RuntimeException {
	  public final String name;
	  
	  public NameError(String name) {
	    super("Name Error");
	    this.name = name;
	  }
	}

	@SuppressWarnings("serial")
	public static class DumpError extends RuntimeException {
		public DumpError() {
			super("Cannot dump environment.");
		}
	}
	
	public final Environment enclosing;
	private final HashMap<String, Object> values = new HashMap<>();
	
	public Environment(Environment enclosing) {
	  this.enclosing = enclosing;
	}
	public Environment() {
	  enclosing = null;
	}
	
	public HashMap<String, Object> getMap() {
	  return values;
	}
	public void defineDouble(String name, Double x) {
	  values.put(name, (Object) x);
	}
	public void defineFunction(String name, Function fn) {
	  values.put(name, (Function) fn);
	}
	public void defineForeign(String name, ForeignFunction fn) {
	  values.put(name, (ForeignFunction) fn);
	}
	
	private Object get(String name) {
	  Object got = values.get(name);
	  if (got == null && enclosing != null) {
	    return enclosing.get(name);
	  }
	  return got;
	}
	public Double getDouble(String name) {
	  Object extracted = get(name);
	  if (!(extracted instanceof Double)) {
	    throw new NameError(name);
	  }
	  return (Double) extracted;
	}
	public Object getFunction(String name) {
	  Object extracted = get(name);
	  if (extracted == null) {
	    throw new NameError(name);
	  }
	  return extracted;
	}
}