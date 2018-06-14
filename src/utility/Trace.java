package utility;

import sim.Sim;
import value.TypeT;
import dcprototype.Console;
import dcprototype.HTML;

private class Trace
{ private final TypeT a, b;
  private final Trace trace;
  private final int ia, ib;
  private final EditOperation op;
  private Sim sim;
  public Trace(TypeT a, TypeT b, Trace trace, EditOperation op)
  { this.a=a; 
    this.b=b;
    this.trace = trace;
    this.op = op;
    this.ia = op.nextA(trace == null ? 0 : trace.ia);
    this.ib = op.nextB(trace == null ? 0 : trace.ib);
    this.sim = op.calculate(trace == null ? this.getUnknown() : trace.getSim());
  }
  public Trace getTrace(){ return this.trace;}
  public EditOperation getOp(){ return this.op;}
  public Sim getSim(){ return this.sim;}
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());}
  /*
   
  */
  public boolean refine()
  { if(this.op.refine()) return true;
    this.sim=this.op.calculate(trace==null ? this.getUnknown() : trace.getSim());
    return false;
  }
        
  public String toString()
  { return (this.trace ==  null ? "" : this.trace.toString())+this.op;}
  public String beautify()
  { return (this.trace == null ? "" : this.trace.beatify())+this.op.beatify();}
  public String html()
  { return (this.trace == null ? "" : this.trace.html())+HTML.TR(op.html(ia,ib));}
}
