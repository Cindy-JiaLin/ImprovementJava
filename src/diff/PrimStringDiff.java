package diff;

import sim.Sim;
import dcprototype.*;

import type.TYPE;
import value.TypeT;
import value.PrimString;

public class PrimStringDiff extends Diff 
{ private final PrimString a, b;
  private PartialSolution[] candidates;
  
  public PrimStringDiff(PrimString a, PrimString b)
  { this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null, this.a, this.b)};
  }        
  public TypeT getSourceValue(){ return this.a;}
  public TypeT getTargetValue(){ return this.b;}
  
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());} 
  public Sim getSim(){ return this.candidates[0].getSim();}  
  public PartialSolution getSolution(){ return this.candidates[0];}     
  
  public String toString(){ return this.candidates[0].toString();}
  public String beautify(){ return this.candidates[0].beautify();}
  public String html(){ return this.candidates[0].html();}
  
  // lwb==upb
  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}
  public boolean refine()
  { if (Main.VERBOSE)
    { for(int i=0; i<this.candidates.length; i++)
      System.out.println("["+i+"]"+this.candidates[i]);
      System.out.println("\n"); 
    }
    if (isFinal()){ return true;}    
    else
    { this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
      return false;
    }
  }
}
