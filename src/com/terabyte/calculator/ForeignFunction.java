package com.terabyte.calculator;

import java.util.ArrayList;

public abstract class ForeignFunction {
	@SuppressWarnings("serial")
	public static class BadFFI_Call extends RuntimeException {
	  public final String what;
	  
	  public BadFFI_Call(String what) {
	    super("Bad FFI Call");
	    this.what = what;
	  }
	}
	
	private final int arity;
	
	public void arityCheck(int arity) {
	  if (arity != this.arity) {
	    throw new BadFFI_Call("Invalid arity for function call.");
	  }
	}
	public ForeignFunction(int arity) {
	  this.arity = arity;
	}
	public abstract Double execute(TreeInterpreter state, ArrayList<Double> args);
}