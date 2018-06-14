package utility;

import value.TypeT;
import sim.Sim;
import dcprototype.*;

public class PartialSolution  
{ private final Trace trace;
  private final TypeT a,b; 
  private PartialSolution(Trace trace, TypeT a, TypeT b)
  { this.trace = trace; this.a=a; this.b=b;}
   
  public int getSource(){ return (this.trace == null ? 0 : trace.ia);}
  public int getTarget(){ return (this.trace == null ? 0 : trace.ib);}
  public Sim getSim(){ return (trace == null ? Sim.UNKNOWN(this.a.weight()+this.b.weight()):trace.getSim());}
        
  public boolean refine(){ if(trace==null) return false; else return trace.refine();}    
  
  public String toString(){ return (trace == null ? "" : ""+trace);} 
  public String beautify(){ return (trace == null ? "" : tarce.beautify());} 
  public String html(){ return HTML.TABLE(trace.html()+(SIM ? HTML.TD2(HTML.CHG,getSim().getPercentage()):""));}
}
