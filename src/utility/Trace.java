package utility;

import sim.Sim;
import value.TypeT;
import dcprototype.Console;
import dcprototype.HTML;

import java.util.ArrayList;

public class Trace
{ private final Trace trace;
  private final EditOperation op;
  private final TypeT a, b;
  private final int ia, ib;
  private Sim sim;

  public Trace(Trace trace, EditOperation op, TypeT a, TypeT b)
  { this.trace = trace;
    this.op = op;
    this.a=a; 
    this.b=b;
    this.ia = op.nextA(trace == null ? 0 : trace.ia);
    this.ib = op.nextB(trace == null ? 0 : trace.ib);
    this.sim = op.calculate(trace == null ? this.getUnknown() : trace.getSim());
  }
  public Trace trace(){ return this.trace;}
  public EditOperation op(){ return this.op;}
  public int ia(){ return this.ia;}
  public int ib(){ return this.ib;}
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
  { return (this.trace == null ? "" : this.trace.beautify())+this.op.beautify();}
  public String html()
  { return (this.trace == null ? "" : this.trace.html())+HTML.TR(op.html(ia,ib));}


  // expand() for DiffSet
  public ArrayList<TypeT> getTargetValues()
  { if(this.trace == null)
    { ArrayList<TypeT> res = new ArrayList<>();
      if(op instanceof Insert) 
      { Insert ins = (Insert)op;
        res.add(ins.getValue());
      }
      if(op instanceof Change)
      { Change chg = (Change)op;
        res.add(chg.getTargetValue());
      }
      return res;
    }
    else
    { ArrayList<TypeT> res=trace.getTargetValues();
      if(op instanceof Insert) 
      { Insert ins = (Insert)op;
        res.add(ins.getValue());
      }
      if(op instanceof Change)
      { Change chg = (Change)op;
        res.add(chg.getTargetValue());
      }
      return res;
    }
  }
}
