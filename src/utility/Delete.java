package utility;

import value.TypeT;
import sim.Sim;
import dcprototype.Console;
import dcprototype.HTML;

public class Delete extends EditOperation
{ private final TypeT value;
  public Delete(TypeT value){ this.value=value;}

  public boolean refine(){ return true;}
  public int nextA(int ia){ return ia+1;}
  public int nextB(int ib){ return ib;}
  public Sim calculate(Sim sim){ return sim.dec(this.value.weight());}
  
  public String toString(){ return "-"+value;}
  public String beautify(){ return Console.del(""+this.value);}
  public String html(int ia, int ib)
  { return HTML.TD("")+
           HTML.TD(HTML.DEL,ia)+
    (SIM ? HTML.TD("") : "")+
           HTML.TD(HTML.DEL, HTML.encode(""+this.value));
  }
}
