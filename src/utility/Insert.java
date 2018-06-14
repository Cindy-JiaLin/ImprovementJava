package utility;

import value.TypeT;
import sim.Sim;
import dcprototype.Console;
import dcprototype.HTML;

public class Insert extends EditOperation
{ private final TypeT value;
  public Insert(TypeT value){ this.value=value;}

  public boolean refine(){ return true;}
  public int nextA(int ia){ return ia;}
  public int nextB(int ib){ return ib+1;}
  public Sim calculate(Sim sim){ return sim.dec(this.value.weight());}
  
  public String toString(){ return "+"+value;}
  public String beautify(){ return Console.ins(""+this.value);}
  public String html(int ia, int ib)
  { return HTML.TD("")+
           HTML.TD(HTML.INS,ib)+
    (SIM ? HTML.TD("") : "")+
           HTML.TD(HTML.INS, HTML.encode(""+this.value));
  }
}
