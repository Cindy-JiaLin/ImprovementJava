package utility;

import diff.Diff;
import sim.Sim;
import dcprototype.Console;
import dcprototype.HTML;

public final static class Change extends EditOperation
{ private final Diff diff;
  public Change(Diff diff){ this.diff=diff;}

  public boolean refine(){ return diff.refine();}
  public int nextA(int ia){ return ia+1;}
  public int nextB(int ib){ return ib+1;}
  public Sim calculate(Sim sim)
  { Sim s = this.diff.getSim();
    if(s.isDiscrete()) return sim.inc(s.getIncrement()).dec(s.getDecrement());
    else return sim.inc(s.getIncrementReal()).dec(s.getDecrementReal());
  }
  
  public String toString(){ return ""+diff;}
  public String beautify(){ return Console.chg(""+this.diff);}
  public String html(int ia, int ib)
  { return HTML.TD(HTML.CHG,ia)+
           HTML.TD(HTML.CHG,ib)+
    (SIM ? HTML.TD(""+(diff.getSim().getPercentage1()) : "")+
           HTML.TD(HTML.CHG, diff.html());
  }
}
