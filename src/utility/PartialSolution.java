package utility;

import type.TYPE;
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
  public String beautify(){ return (trace == null ? "" : trace.beautify());} 
  public String html(){ return HTML.TABLE(trace.html()+(SIM ? HTML.TD2(HTML.CHG,getSim().getPercentage()):""));}

  public static PartialSolution[] deleteFirst(PartialSolution[] cands)
  { PartialSolution[] res = new PartialSolution[cands.length-1];
    System.arraycopy(cands, 1, res, 0, cands.length-1);
    return res;
  }
  // insert the newCands to the cands
  // return a candidates list which is sorted descendingly by upper bound 
  // then lower bound of each candidate
  public static PartialSolution[] insertAll(PartialSolution[] newCands, PartialSolution[] cands)
  { int nonRedundant = 0;
    Sim sim = null;
    // surpress redundant new candidates
    for(int k = 0; k < newCands.length; k++)
    { if(newCands[k].isRedundant(cands)) newCands[k]=null;
      else
      { sim = newCands[k].getSim();
        nonRedundant++;
      }   
    }
    if(nonRedundant == 0) return cands;
    PartialSolution[] res = new PartialSolution[cands.length+nonRedundant];
    int i = 0;
    int j = 0;
    for(; i < cands.length && cands[i].getSim().compareTo(sim)>=0; i++)
      res[j++] = cands[i];
    for(int k =0; k < newCands.length; k++)
      if(newCands[k]!=null) res[j++] = newCands[k];
    for(; i < cands.length; i++)
      res[j++] = cands[i];
    return res;
  }

  // Get the last copy operation position
  public int getStopper(Trace trace)
  { if(trace == null) return -1;
    else if(trace.op instanceof Copy) return trace.ia;
    else return getStopper(trace.trace);
  }  
  // Get the last delete operation position
  public int getLastDelete(Trace trace)
  { if(trace == null) return -1;
    else if(trace.op instanceof Copy) return -1;
    else if(trace.op instanceof Delete) return trace.ia;
    else return getLastDelete(trace.trace);
  } 
  // Get the last insert operation position
  public int getLastInsert(Trace trace)
  { if(trace == null) return -1;
    else if(trace.op instanceof Copy) return -1;
    else if(trace.op instanceof Insert) return trace.ib;
    else return getLastInsert(trace.trace);
  }
  // let x, y be two characters -x, +y and +y, -x is the same partial solution
  // this isRedundant method is used to pick out such partial solution
  public boolean isRedundant(PartialSolution[] active) 
  { int stopper = getStopper(this.trace);
    int lastDelete = getLastDelete(this.trace);
    int lastInsert = getLastInsert(this.trace);
    for(int i = 0; i < active.length; i++)
    { Trace t = active[i].trace;
      if(stopper == getStopper(t) 
      && lastDelete == getLastDelete(t) 
      && lastInsert == getLastInsert(t))
        return true;
    }    
    return false;
  }

  private PartialSolution[] expand()
  { TYPE T=this.a.typeOf();
    /*PrimString*/
    if(T.isSTRING())
    { PrimString sourceStr = (PrimString)this.a;
      PrimString targetStr = (PrimString)this.b;
      TypeT memSource = sourceStr.charAt(getSource());
      TypeT memTarget = targetStr.charAt(getTarget())
      if(targetStr.weight() ==  getTarget())
      { if(sourceStr.weight() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{ delete(memSource)};
      }
      else if(sourceStr.weight() == getSource()) 
           return new PartialSolution[]{ insert(memTarget)};
      else if(memSource == memTarget) 
           return new PartialSolution[]{ copy(memSource)};
      else return new PartialSolution[]{ insert(memTarget), delete(memSource)};
    }
    /*TypeProduct*/
    else if(T.isPRODUCT())
    { TypeProduct sourceProduct = (TypeProduct)this.a;
      TypeProduct targetProduct = (TypeProduct)this.b;
      TypeT memSource = sourceProduct.getValues().get(getSource());
      TypeT memTarget = targetProduct.getValues().get(getTarget());
      return new PartialSolution[]{ change(memSource, memTarget)};
    }
    /*TypeUnion*/
    else if(T.isUNION()) 
    { TypeUnion sourceUnion = (TypeUnion)this.a;
      TypeUnion targetUnion = (TypeUnion)this.b;
      if(sourceUnion.getLabel().equals(targetUnion.getLabel())) 
        return new PartialSolution[]{ replace(sourceUnion,targetUnion)};
      else return new PartialSolution[]{ change(sourceUnion.getValue(),targetUnion.getValue())};
    }
    /*TypeList*/
    else if(T.isLIST())
    { TypeList sourceList = (TypeList)this.a;
      TypeList targetList = (TypeList)this.b;
      TypeT memSource = sourceList.get(getSource());
      TypeT memTarget = targetList.get(getTarget());
      if(targetList.size() ==  getTarget())
      { if(sourceList.size() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{ delete(memSource)};
      }
      else if(sourceList.size() == getSource()) 
             return new PartialSolution[]{ insert(memTarget)};
      else if(memSource.weight()==0)// delete an empty line  
             return new PartialSolution[]{ delete(memSource), insert(memTarget)};
      else if(memTarget.weight()==0)// insert an empty line
             return new PartialSolution[]{ delete(memSource), insert(memTarget)};      
      else return new PartialSolution[]{ change(memSource, memTarget), insert(memTarget), delete(memSource)};
    }
    /*TypeSet*/
    else if(T.isSET())
    { TypeSet sourceSet = (TypeSet)this.a;
      TypeSet targetSet = (TypeSet)this.b;
      if(targetSet.size() ==  getTarget())
      { if(sourceSet.size() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{ delete(sourceSet.get(getSource()))};
      }
      else if(sourceSet.size() == getSource())
      { if(trace==null||(!this.trace.getTargetValues().contains(targetSet.get(getTarget())))) 
          return new PartialSolution[]{ insert(targetSet.get(getTarget()))};
        else return new PartialSolution[0];
      }
      else// when both are non-empty
      { TypeT[] targets = getTargetValues();
        PartialSolution[] temp = new PartialSolution[2*targets.length+1];
        int k=0;// k is the number of repeat insertion elements in b
        for(int i=0; i<targets.length; i++)
        { if(trace == null||(!trace.getTargetValues().contains(targets[i])))
            temp[2*i]=change(sourceSet.get(getSource()), targets[i]);//
          else {temp[2*i]=null; k++;}
          if(trace == null||(!trace.getTargetValues().contains(targets[i])))
            temp[2*i+1]=insert(targets[i]);
          else {temp[2*i+1]=null; k++;}
        }
        temp[2*targets.length]=delete(sourceSet.get(getSource()));
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
    /*Other Types*/
  }

  private PartialSolution delete(TypeT memSource)
  { EditOperation op = new Delete(memSource);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }        
  private PartialSolution insert(TypeT memTarget)
  { EditOperation op = new Insert(memTarget);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }        
  private PartialSolution copy(TypeT memSource)
  { EditOperation op = new Copy(memSource);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }  
  private PartialSolution change(TypeT memSource, TypeT memTarget)
  { TYPE memT = memSource.typeOf();
    EditOperation op = new Change(Diff.newDiff(memT, memSource, memTarget));
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }
  private PartialSolution replace(TypeT memSource, TypeT memTarget)
  { EditOperation op = new Replace(memSource, memTarget);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }
}
