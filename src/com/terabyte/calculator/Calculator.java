package com.terabyte.calculator;

import com.terabyte.calculator.Environment.NameError;
import com.terabyte.calculator.ForeignFunction.BadFFI_Call;
import com.terabyte.calculator.Parser.ParseError;
import com.terabyte.calculator.Scanner.LexicalError;
import com.terabyte.calculator.TreeInterpreter.ValueError;

public class Calculator {
  private final Parser parser = new Parser();
  private final TreeInterpreter interp = new TreeInterpreter();
  private boolean isSuccessful = true;
  private Double lastResult;

  public String evaluate(String input) {
    try {
      try {
        lastResult = interp.evaluate(parser.parse(input));
        isSuccessful = true;
        if (lastResult != null) {
          return String.format("%g", lastResult);
        }
        return "";
      } finally {
        isSuccessful = false;
      }
    } catch (LexicalError | ParseError err)   {
      return "Syntax Error!";
    } catch (ValueError err) {
      return String.format("Value Error! %s", err.what);
    } catch (NameError err) {
      return String.format("Undefined name '%s'!", err.name);
    } catch (BadFFI_Call err) {
      return String.format("Value Error! %s", err.what);
    } catch (StackOverflowError err) {
      return "Recursion Error!";
    } catch (DumpError err) {
      return "Cannot dump state.";
    }
  }

  public boolean hasResult() {
    return isSuccessful && lastResult != null;
  }
  public Double getResult() {
    return lastResult;
  }
}
