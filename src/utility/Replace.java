package utility;

import value.TypeT;
import sim.Sim;
import dcprototype.*;

public class Replace extends EditOperation
{ private final TypeT value1, value2;
  public Replace(TypeT value1, TypeT value2){ this.value1=value1; this.value2=value2;}
  
  public boolean refine(){ return true;}
  public int nextA(int ia){ return ia+1;}
  public int nextB(int ib){ return ib+1;}
  public Sim calculate(Sim sim){ return sim.dec(value1.weight()).dec(value2.weight());}
  
  public String toString(){ return "(-"+value1+"+"+value2+")";}
  public String beautify(){ return Console.del(""+this.value1)+Console.ins(""+this.value2);}
  public String html(int ia, int ib)
  { return HTML.TD(HTML.DEL,ia)+
           HTML.TD(HTML.INS,ib)+
    (Main.SIM ? HTML.TD(""): "")+
           HTML.TD(HTML.DEL, HTML.encode(""+this.value1))+
           HTML.TD(HTML.INS, HTML.encode(""+this.value2));
  }
}
