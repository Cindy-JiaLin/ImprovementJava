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
  public String beautify(){ return (trace == null ? "" : tarce.beautify());} 
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
      if(targetStr.weight() ==  getTarget())
      { if(sourceStr.weight() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{ delete(T)};
      }
      else if(sourceStr.weight() == getSource()) 
           return new PartialSolution[]{ insert(T)};
      else if(sourceStr.charAt(getSource()) == targetStr.charAt(getTarget())) 
           return new PartialSolution[]{ copy(T)};
      else return new PartialSolution[]{ insert(T), delete(T)};
    }
    /*TypeProduct*/
    else if(T.isPRODUCT()) return new PartialSolution[]{ change(T)};
    /*TypeUnion*/
    else if(T.isUNION()) 
    { TypeUnion sourceUnion = (TypeUnion)this.a;
      TypeUnion targetUnion = (TypeUnion)this.b;
      if(sourceUnion.getLabel().equals(targetUnion.getLabel())) 
        return new PartialSolution[]{ replace(T)};
      else return new PartialSolution[]{ change(T)};
    }
    /*TypeList*/
    else if(T.isLIST())
    { TypeList sourceList = (TypeList)this.a;
      TypeList targetList = (TypeList)this.b;
      if(targetList.size() ==  getTarget())
      { if(sourceList.size() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{ delete(T)};
      }
      else if(sourceList.size() == getSource()) 
             return new PartialSolution[]{ insert(T)};
      else if(sourceList.get(getSource()).weight()==0) 
             return new PartialSolution[]{ delete(T), insert(T)};// delete an empty line 
      else if(targetList.get(getTarget()).weight()==0) 
             return new PartialSolution[]{ delete(T), insert(T)};// insert an empty line
      else return new PartialSolution[]{ change(T), insert(T), delete(T)};
    }
    /*TypeSet*/
    else if(T.isSET())
    { TypeSet sourceSet = (TypeSet)this.a;
      TypeSet targetSet = (TypeSet)this.b;
      if(targetSet.size() ==  getTarget())
      { if(sourceSet.size() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{ delete(T)};
      }
      else if(sourceSet.size() == getSource())
      { if(trace==null||(!this.trace.getTargetValues().contains(targetSet.get(getTarget())))) 
          return new PartialSolution[]{ insert(T)};
        else return new PartialSolution[0];
      }
      else// when both are non-empty
      { TypeT[] targets = getTargetValues();
        PartialSolution[] temp = new PartialSolution[2*targets.length+1];
        int k=0;// k is the number of repeat insertion elements in b
        for(int i=0; i<targets.length; i++)
        { if(trace == null||(!trace.getTargetValues().contains(targets[i])))
            temp[2*i]=change(T.getBaceTYPE(), sourceSet.get(getSource()), targets[i]);//
          else {temp[2*i]=null; k++;}
          if(trace == null||(!trace.getTargetValues().contains(targets[i])))
            temp[2*i+1]=insert(T, targets[i]);
          else {temp[2*i+1]=null; k++;}
        }
        temp[2*targets.length]=delete(T);
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

  private PartialSolution delete(TYPE T, TypeT memSource)
  { EditOperation op = new Delete(memSource)deletion(T, this.a);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }        
  private PartialSolution insert(TYPE T)
  { EditOperation op = insertion(T, this.b);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }        
  private PartialSolution copy(TYPE T)
  { EditOperation op = duplication(T, this.a);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }  
  private PartialSolution change(TYPE T)
  { EditOperation op = modification(T, this.a, this.b);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }
  private PartialSolution change(TYPE memT, TypeT memSource, TypeT memTarget)
  { EditOperation op = new Change(Diff.newDiff(memT, memSource, memTarget));
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }
  private PartialSolution replace(TYPE T)
  { EditOperation op = replacement(T, this.a, this.b);
    Trace trace = new Trace(this.trace, op, this.a, this.b);
    return new PartialSolution(trace, this.a, this.b);
  }
  
  private EditOperation deletion(TYPE T, TypeT source)
  { if(T.isSTRING()) return new Delete((PrimString)source.charAt(getSource()));
    else if(T.isPRODUCT()) return new Delete((TypeProduct)source.getValues().get(getSource()));
    else if(T.isUNION) return new Delete((TypeUnion)source); 
    else if(T.isSET()) return new Delete((TypeSet)source.get(getSource()));
    else if(T.isLIST()) return new Delete((TypeList)source.get(getSource()));
    else if(T.isMSET()) return new Delete((TypeMultiset)source.get(getSource()));
    else return new Delete((TypeMapping)source.get(getSource()));
  }
  private EditOperation insertion(TYPE T, TypeT target)
  { if(T.isSTRING()) return new Insert((PrimString)target.charAt(getTarget()));
    else if(T.isPRODUCT()) return new Insert((TypeProduct)target.getValues().get(getTarget()));
    else if(T.isUNION) return new Insert((TypeUnion)target); 
    else if(T.isSET()) return new Insert((TypeSet)target.get(getTarget()));
    else if(T.isLIST()) return new Insert((TypeList)target.get(getTarget()));
    else if(T.isMSET()) return new Insert((TypeMultiset)target.get(getTarget()));
    else return new Insert((TypeMapping)target.get(getTarget()));
  }
  private EditOperation duplication(TYPE T, TypeT source)
  { if(T.isSTRING()) return new Copy((PrimString)source.charAt(getSource()));
    else if(T.isPRODUCT()) return new Copy((TypeProduct)source.getValues().get(getSource()));
    else if(T.isUNION) return new Copy((TypeUnion)source); 
    else if(T.isSET()) return new Copy((TypeSet)source.get(getSource()));
    else if(T.isLIST()) return new Copy((TypeList)source.get(getSource()));
    else if(T.isMSET()) return new Copy((TypeMultiset)source.get(getSource()));
    else return new Copy((TypeMapping)source.get(getSource()));
  }
  private EditOperation modification(TYPE T, TypeT source, TypeT target)
  { TypeT memSource = TypeT.baseValue(T, source, getSource());// member of the source
    TypeT memTarget = TypeT.baseValue(T, target, getTarget());// member of the target
    TYPE memT = memSource.typeOf();// member TYPE 
    return new Change(Diff.newDiff(memT, memSource, memTarget));
  }
  // only Union type needs this replacement
  // when labels are not the same
  private EditOperation replacement(TYPE T, TypeT source, TypeT target)
  { if(T.isUNION) return new Replace((TypeUnion)source, (TypeUnion)target); 
    else throw new RuntimeException("Replacement is only develped for different labeled values of UNION TYPE");
  }
}
