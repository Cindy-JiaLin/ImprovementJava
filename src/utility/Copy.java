package utility;

import value.TypeT;
import sim.Sim;
import dcprototype.*;

public class Copy extends EditOperation
{ private final TypeT value;
  public Copy(TypeT value){ this.value=value;}
  
  public boolean refine(){ return true;}
  public int nextA(int ia){ return ia+1;}
  public int nextB(int ib){ return ib+1;}
  public Sim calculate(Sim sim){ return sim.inc(2*this.value.weight());}

  public String toString(){ return "="+this.value;}
  public String beautify(){ return Console.cpy(""+this.value);}
  public String html(int ia, int ib)
  { return HTML.TD(HTML.CPY,ia)+
           HTML.TD(HTML.CPY,ib)+
    (Main.SIM ? HTML.TD("") : "")+
           HTML.TD(HTML.CPY, HTML.encode(""+this.value));
  }
}
