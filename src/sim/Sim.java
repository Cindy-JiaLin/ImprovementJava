package sim;

public final class Sim implements Comparable<Sim>
{ // similarity is represented relative to the total weight of the component, i.e. as [num1/den, num2/den] 
  // or equivalently with floating point values. Both representations are equivalent, the boolean state discrete
  // indicate which one is considered the "more appropriate" one.
  private boolean discrete;
  private final int num1, num2, den;//den stands for denominator
  private double dlwb, dupb;
  private Sim(int num1, int num2, int den)
  { this.discrete=true; this.num1=num1; this.num2=num2; this.den=den; this.dlwb=1.0*num1; this.dupb=1.0*num2;
    if(this.num1>this.num2) throw new RuntimeException("new Sim("+num1+","+num2+","+den+")");
  }
  public Sim(double dlwb, double dupb, int w)
  { dlwb=rounding(dlwb); dupb=rounding(dupb);
    this.discrete=false; this.num1=(int)dlwb; this.num2=(int)dupb; this.den=w; this.dlwb=dlwb; this.dupb=dupb;
    if(this.dlwb>this.dupb) throw new RuntimeException("new Sim("+dlwb+","+dupb+","+w+")");
  }
  // dirty trick to compensate for numerical inaccuracies
  private static double rounding(double r){ return Math.rint(r*1.0E8)*1.0E-8;}

  public boolean isDiscrete(){ return this.discrete;}

  public String toString()
  { if(discrete) 
      return (isUnknown() ? "[0,1]" : "["+this.num1+"/"+this.den+","+this.num2+"/"+this.den+"]");
    else return (isUnknown() ? "[0,1]" : "["+this.dlwb+","+this.dupb+"]");
  }
  public String beautify()
  { if(discrete) 
      return (isUnknown() ? "[0,1]" : "["+this.num1+"/"+this.den+","+this.num2+"/"+this.den+"]");
    else return (isUnknown() ? "[0,1]" : "["+this.dlwb+","+this.dupb+"]");
  } 
  
  public int getDecrement(){ return den-num2;}
  public int getIncrement(){ return num1;}
  public double getDecrementReal(){ return den-dupb;}
  public double getIncrementReal(){ return dlwb;}
  
  public final static Sim UNKNOWN(int n){ return new Sim(0,n,n);}
  public final static Sim EQUAL(int n){ return new Sim(n,n,n);}
  public final static Sim DIFF(int n){ return new Sim(0,0,n);}

  public Sim inc(int n){ return new Sim(this.num1+n, this.num2, this.den);}
  public Sim dec(int n){ return new Sim(this.num1, this.num2-n, this.den);}
  public Sim inc(double r){ return new Sim(this.dlwb+r, this.dupb, this.den);}
  public Sim dec(double r){ return new Sim(this.dlwb, this.dupb-r, this.den);}
  
  public boolean isFinal()
  { if(discrete) return this.num1==this.num2; else return Math.abs(this.dlwb-this.dupb) < 1.0E-8;}

  public boolean isEqual(){ return this.num1==this.den && this.num2==this.den;}// real number will never equal
  public boolean isUnknown(){ return this.num1==0 && this.num2==this.den;}
  
  public String getPercentage(int num, int den)
  { if(num==den) return "100%";
    else 
    { String s=""+(int)(100000*(1.0+(1.0*num)/den));
      return s.substring(1,3)+"."+s.substring(3,6)+"%";// 3 decimal
    }
  }  
  public String getPercentage1(int num, int den)
  { if(num==den) return "100%";
    else 
    { String s=""+(int)(100000*(1.0+(1.0*num)/den));
      return s.substring(1,3)+"."+s.substring(3,4)+"%";// 1 decimal
    }
  } 
  public String getPercentage0(int num, int den)
  { if(num==den) return "100%";
    else 
    { String s=""+(int)(100000*(1.0+(1.0*num)/den));
      return s.substring(1,3)+"%";// 0 decimal
    }
  } 
  public String getPercentage(double num, int den)
  { String s=""+(100000*(1.0+(1.0*num)/den));
    return s.substring(1,3)+"."+s.substring(3,6)+"%";// 3 decimal
  }  
  public String getPercentage1(double num, int den)
  { String s=""+(100000*(1.0+(1.0*num)/den));
    return s.substring(1,3)+"."+s.substring(3,4)+"%";// 1 decimal
  }
  public String getPercentage0(double num, int den)
  { String s=""+(100000*(1.0+(1.0*num)/den));
    return s.substring(1,3)+"%";// 0 decimal
  }

  public String getPercentage()
  { if(discrete)
    { if(num1==num2) return getPercentage(num1,den);
      else return getPercentage(num1,den)+".."+getPercentage(num2,den);
    }
    else
    { if(Math.abs(dlwb-den)<1.0E-8) return "100%";
      else return getPercentage(dlwb,den);
    }
  } 
  public String getPercentage1()
  { if(discrete)
    { if(num1==num2) return getPercentage1(num1,den);
      else return getPercentage(num1,den)+".."+getPercentage(num2,den);
    }
    else
    { if(Math.abs(dlwb-den)<1.0E-8) return "100%";
      else return getPercentage1(dlwb,den);
    }
  }         
  public String getPercentage0()
  { if(discrete)
    { if(num1==num2) return getPercentage0(num1,den);
      else return getPercentage(num1,den)+".."+getPercentage(num2,den);
    }
    else
    { if(Math.abs(dlwb-den)<1.0E-8) return "100%";
      else return getPercentage0(dlwb,den);
    }

  } 
  
  // used to sort partial solutions upb first then lwb
  public int compareTo(Sim sim)
  { if(sim==null) return 1;
    else if(this.den==sim.den)//the normal case
    { if(discrete)
      { if(this.num2<sim.num2) return -1;
        else if(this.num2>sim.num2) return +1;
        else // this.num2==sim.num2
        if(this.num1<sim.num1) return -1;
        else if(this.num1>sim.num1) return +1;
        else // this.num1==sim.num1
        return 0;
      }
      else
      { if(this.dupb<sim.dupb) return -1;
        else if(this.dupb>sim.dupb) return +1;
        else if(this.dlwb<sim.dlwb) return -1;
        else return +1;//this.dlwb>sim.dlwb
      }
    }  
    else
    { if(discrete)
      { int t2=this.num2*sim.den;// t1--this.num1, t2--this.num2
        int s2=sim.num2*this.den;// s1 --sim.num1, s2 --sim.num2 
        if(t2<s2) return -1;
        else if(t2>s2) return +1;
        else// t2==s2
        { int t1=this.num1*sim.den;
          int s1=sim.num1*this.den;
          if(t1<s1) return -1;
          else if(t1>s2) return +1;
          else return 0;
        }
      }
      else
      { double tu = this.dupb*sim.den; // tl -- this.dlwb, tu -- this.dupb
        double su = sim.dupb*this.den; // sl -- sim.dlwb, su -- sim.dupb
        if(tu<su) return -1;
        else if(tu>su) return +1;
        else
        { double tl = this.dlwb*sim.den;
          double sl = sim.dlwb*this.den;
          if(tl<sl) return -1;
          else return +1;// tl>sl
        }
      }
    }    
  }        
}
