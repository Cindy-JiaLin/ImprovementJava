package diff;

import java.util.Arrays;

import sim.*;
import type.*;
import value.*;
import dcprototype.*;

import utility.PartialSolution;

public class ProductDiff extends Diff 
{ private final TypeProduct a, b;
  private PartialSolution[] candidates;
  
  private final static SimComparator simComparator = new SimComparator();
   
  public ProductDiff(TypeProduct a, TypeProduct b)
  { if(a.size()!=b.size())// These two product values must have the same size 
      throw new RuntimeException("Different size Product values cannot be compared.");
    this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null,a,b)};
  }        
  
  public TypeProduct getSourceValue(){ return this.a;}
  public TypeProduct getTargetValue(){ return this.b;}
  
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());} 
  public Sim getSim(){ return this.candidates[0].getSim();}
 
  public String toString(){ return "("+this.candidates[0].toString()+")";}
  public String beautify(){ return Console.cpy("(")+this.candidates[0].beautify()+Console.cpy(")");}
  public String html(){ return this.candidates[0].html();}

  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}
  public boolean refine()
  { if (Main.VERBOSE) 
    { System.out.println(this.candidates[0]);
      for(int i=0; i<this.candidates.length; i++)
        System.out.println(""+i+": "+this.candidates[i].getSim());
      System.out.println();
    }
    // the initial state, the trace is null, outside refine is needed
    if(this.candidates[0].trace()==null)
    { this.candidates = PartialSolution.insertAll(this.candidates[0].expand(), PartialSolution.deleteFirst(this.candidates));
      Arrays.sort(this.candidates, simComparator);
      return false;
    }
    else if(!this.candidates[0].refine())
    { Arrays.sort(this.candidates, simComparator);// sort the candidates after each inside refine step
      return false;
    }
    else if (isFinal()){ return true;}    
    else// when each edit operation has been completely refined, expand one more step
    { this.candidates = PartialSolution.insertAll(this.candidates[0].expand(), PartialSolution.deleteFirst(this.candidates));
      Arrays.sort(this.candidates, simComparator);
      return false;
    }
  }
}
