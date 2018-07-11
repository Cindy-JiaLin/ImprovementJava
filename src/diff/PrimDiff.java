package diff;

import sim.*;
import type.*;
import dcprototype.*;

import value.TypeT;
import value.PrimReal;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.ArrayList;

import utility.PartialSolution;

// Diff between PrimUnit, PrimBool, PrimChar, PrimNat, PrimInt
public class PrimDiff extends Diff 
{ private final TypeT a, b;
  public PrimDiff(TypeT a, TypeT b){ this.a=a; this.b=b;}  
  
  public TypeT getSourceValue(){ return this.a;}
  public TypeT getTargetValue(){ return this.b;}

  public Sim getUnknown(){ return Sim.UNKNOWN(2);}// the weight of primitive type value is 1
  
  public Sim getSim()
  { TYPE t = this.a.typeOf();
    if(t.isPRIMITIVE())
    { if(this.a.equals(this.b)) return Sim.EQUAL(2);
      else return Sim.DIFF(2);
    }
    else if(t.isREAL())
    { PrimReal r1=(PrimReal)this.a;
      PrimReal r2=(PrimReal)this.b;
      double acc = r1.typeOf().getAcc();
      double simR = 1-(Math.abs(r1.getValue()-r2.getValue())/acc);
      if(r1.isSimilar(r2)) return new Sim(2*simR, 2*simR, 2);
      else return Sim.DIFF(2);
    }
    else throw new RuntimeException("Type error, must be the primitive type.");
  }

  public boolean isFinal(){ return true;}
  public boolean refine(){ return true;}

  public String toString()
  { TYPE t = this.a.typeOf();
    if(t.isPRIMITIVE())
    { if(this.a.equals(this.b)) return "="+this.a;
      else return "-"+this.a+"+"+this.b;
    }
    else if(t.isREAL())
    { PrimReal r1=(PrimReal)this.a;
      PrimReal r2=(PrimReal)this.b;
      double acc = r1.typeOf().getAcc();
      if(r1.isSimilar(r2)) return this.a+"~"+this.b;
      else return "-"+this.a+"+"+this.b;
    }
    else throw new RuntimeException("Type error, must be the primitive type.");
     
  }
  public String beautify()
  { TYPE t = this.a.typeOf();
    if(t.isPRIMITIVE())
    { if(this.a.equals(this.b)) return Console.cpy(""+this.a);
      else return Console.del(""+this.a)+Console.ins(""+this.b);
    }
    else if(t.isREAL())
    { PrimReal r1=(PrimReal)this.a;
      PrimReal r2=(PrimReal)this.b;
      double acc = r1.typeOf().getAcc();
      if(r1.isSimilar(r2)) return Console.chg(""+this.a+Encoding.APPROX+this.b);
      else return Console.chg(Console.del(""+this.a)+Console.ins(""+this.b));
    }
    else throw new RuntimeException("Type error, must be the primitive type.");
  }
  public String html()
  { TYPE t = this.a.typeOf();
    String rows = "";// only one row contains two cells
    if(t.isPRIMITIVE())
    { if(this.a.equals(this.b)) 
        rows = HTML.TR((Main.SIM ? HTML.TD("100%") : "")+
                              HTML.TD(HTML.CPY, HTML.encode(""+this.a)));
      else 
        rows = HTML.TR((Main.SIM ? HTML.TD("0%") : "")+
                              HTML.TD(HTML.DEL, HTML.encode(""+this.a))+HTML.TD(HTML.INS, HTML.encode(""+this.b)));
      return HTML.TABLE(rows);
    }
    else if(t.isREAL())
    { PrimReal r1=(PrimReal)this.a;
      PrimReal r2=(PrimReal)this.b;
      double acc = r1.typeOf().getAcc();
      if(r1.isSimilar(r2)) 
        rows = HTML.TR((Main.SIM ? HTML.TD(""+this.getSim().getPercentage1()) : "")+
                              HTML.TD(HTML.CHG, HTML.encode(""+this.a+HTML.encode('~')+this.b)));
      else 
        rows = HTML.TR((Main.SIM ? HTML.TD("0%") : "")+
                              HTML.TD(HTML.DEL, HTML.encode(""+this.a))+HTML.TD(HTML.INS, HTML.encode(""+this.b)));
      return HTML.TABLE(rows);
    }
    else throw new RuntimeException("Type error, must be the primitive type.");
  }
}
