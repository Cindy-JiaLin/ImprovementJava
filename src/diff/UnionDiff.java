package diff;

import java.util.Arrays;
import java.util.Comparator;

import sim.Sim;
import type.*;
import value.*;
import dcprototype.*;

public class UnionDiff extends Diff 
{ private final TypeUnion a, b;
  private PartialSolution[] candidates;
  
  private final static SimComparator simComparator = new SimComparator();
 
  public UnionDiff(TypeUnion a, TypeUnion b)
  { this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }        
  public TypeT getSourceValue(){ return this.a;}
  public TypeT getTargetValue(){ return this.b;}
 
  public Sim getSim(){ return this.candidates[0].getSim();}  
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());}
  public PartialSolution getSolution(){ return this.candidates[0];}
  
  public String toString(){ return this.candidates[0].toString();}
  public String beautify(){ return this.candidates[0].beautify();}
  public String html(){ return this.candidates[0].html();}
  
  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}
  public boolean refine()
  { if (Main.VERBOSE) 
    { for(int i=0; i<this.candidates.length; i++)
        System.out.println(""+i+": "+this.candidates[i].getSim()+this.candidates[i]);
      System.out.println();
    }
    // the initial state, the trace is null, outside refine is needed
    if(this.candidates[0].trace==null)
    { this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
      Arrays.sort(this.candidates, simComparator);
      return false;
    }
    // the intermediate state, the trace is not null
    // this.candidates[0].refine() is determined by the trace.refine()
    // if the current EditOperation is Change, the trace.refine() return false
    // which means one more inside refine step needs to do
    else if(!this.candidates[0].refine())
    { Arrays.sort(this.candidates, simComparator);// sort the candidates after each inside refine step
      return false;
    }
    else if (isFinal()){ return true;}    
    else// when each edit operation has been completely refined, expand one more step
    { this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
      Arrays.sort(this.candidates, simComparator);
      return false;
    }
  }
}
