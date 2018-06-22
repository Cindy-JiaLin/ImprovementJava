package diff;

import sim.Sim;
import type.*;
import value.*;
import dcprototype.*;

public class SetDiff extends Diff 
{ private final TypeSet a, b;
  private PartialSolution[] candidates;
 
  private final static SimComparator simComparator = new SimComparator();
  
  public SetDiff(TypeSet a, TypeSet b)
  { if(!a.getBaseTYPE().equals(b.getBaseTYPE()))
      throw new RuntimeException("These two sets have different base type values.");
    this.a=a; this.b=b;
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
    { System.out.println(this.candidates[0]);
      for(int i=0; i<this.candidates.length; i++)
        System.out.println(""+i+": "+this.candidates[i].getSim());
      System.out.println();
    }
    if(this.candidates[0].trace==null)
    { this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
      Arrays.sort(this.candidates, simComparator);
      return false;
    }
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
    private PartialSolution[] expand()
    { if(SetDiff.this.b.size() ==  getTarget())
      { if(SetDiff.this.a.size() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{ delete(SetDiff.this.a.get(getSource()))};
      }
      else if(SetDiff.this.a.size() == getSource())
      { if(trace==null||(!this.trace.getTargetValues().contains(SetDiff.this.b.get(getTarget())))) 
          return new PartialSolution[]{ insert(SetDiff.this.b.get(getTarget()))};
        else return new PartialSolution[0];
      }
      else// when both are non-empty
      { TypeT[] targets = getTargetValues();
        PartialSolution[] temp = new PartialSolution[2*targets.length+1];
        int k=0;// k is the number of repeat insertion elements in b
        for(int i=0; i<targets.length; i++)
        { if(trace == null||(!trace.getTargetValues().contains(targets[i])))
            temp[2*i]=change(SetDiff.this.a.get(getSource()), targets[i]);
          else {temp[2*i]=null; k++;}
          if(trace == null||(!trace.getTargetValues().contains(targets[i])))
            temp[2*i+1]=insert(targets[i]);
          else {temp[2*i+1]=null; k++;}
        }
        temp[2*targets.length]=delete(SetDiff.this.a.get(getSource()));
        //return temp;
        if(k==0) return temp;
        else
        { PartialSolution[] res = new PartialSolution[2*targets.length+1-k];
          int n=0;
          for(int i=0; i<temp.length; i++)
            if(temp[i]!=null) res[n++]=temp[i];
          return res;
        }
      }
    }        
    
     private ArrayList<TypeT> getTargetValues()
    { if(trace == null)
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
}
