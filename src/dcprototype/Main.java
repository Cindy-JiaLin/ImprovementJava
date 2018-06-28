package dcprototype;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.ArrayList;

import type.*;
import value.*;
import diff.*;

public class Main 
{ public static boolean VERBOSE = false;// if true all intermediate states will be logged
  public static boolean SIM = false;// displays similarity as percentage
  public static boolean DIFF = false;// displays difference as a solution (PartialSolution) 
  //public static boolean INFO = false;// displays runtime statistics
  public static boolean HTMLCODE=false;// displays difference as text
  public static boolean BEAUTIFY=false;// displays difference in colour box
  public static boolean UNICODE=false;// displays difference in Unicode Point Code

  private static String htmlFileName;

  public static void main(String[] args) throws UnsupportedEncodingException
  { final long startTime = System.currentTimeMillis();
    if(Options.isSet(args, "-verbose")) { VERBOSE = true; args = Options.remove(args, "-verbose");}
    if(Options.isSet(args, "-sim")) { SIM = true; args = Options.remove(args, "-sim");}
    if(Options.isSet(args, "-diff")) { DIFF = true; args = Options.remove(args, "-diff");}
    if(Options.isSet(args, "-beautify")) { BEAUTIFY = true; args = Options.remove(args, "-beautify");}
    if(Options.isSet(args, "-unicode")) { UNICODE = true; args = Options.remove(args, "-unicode");}
    // get file numbers
    htmlFileName=Options.getOption(args, "-html");//get the arg after the -html
    if(htmlFileName!=null){ HTMLCODE=true; args=Options.remove(args,"-html", htmlFileName);}
    String typeFileName=Options.getOption(args, "-type");//get the arg after the -type
    if(typeFileName!=null) args = Options.remove(args, "-type", typeFileName);
    String sourceFileName = Options.getOption(args, "-source");// get the arg after the -source
    if(sourceFileName!=null) args = Options.remove(args, "-source", sourceFileName);
    String targetFileName = Options.getOption(args, "-target");// get the arg after the -target
    if(targetFileName!=null) args = Options.remove(args, "-target", targetFileName);

    // get TYPE 
    String strTYPE = null;
    if(typeFileName==null){ strTYPE = Options.getFirst(args); args = Options.removeFirst(args);}
    else strTYPE = Options.getFileContentsAsString(typeFileName);
    List<String> lovs = new ArrayList<String>();
    TYPE resTYPE=ParseTYPEresult.parseTYPE(lovs, strTYPE).getResult();//parse TYPE
   
    // get source value
    String source = null;
    // if sourceFileName is null get the first arg as source string to be compared
    if(sourceFileName==null) { source = Options.getFirst(args); args = Options.removeFirst(args);}
    else source = Options.getFileContentsAsString(sourceFileName);
    TypeT resV1=ParseVALUEresult.parseVALUE(resTYPE, source).getResult();//parse VALUE1 
    
    // get target value
    String target = null;
    // if targetFileName is null get the first arg as target string to be compared
    if(targetFileName==null) { target = Options.getFirst(args); args = Options.removeFirst(args);}
    else target = Options.getFileContentsAsString(targetFileName);
    TypeT resV2=ParseVALUEresult.parseVALUE(resTYPE, target).getResult();//parse VALUE2

    // model values with their TYPE, in particular for REC TYPE
    TypeT model1 = model(resTYPE, resV1);
    TypeT model2 = model(resTYPE, resV2);
    
    //System.out.println("TYPE: "+resTYPE+"\n");
    //System.out.println("SOURCE: "+model1+"\n");
    //System.out.println("TARGET: "+model2+"\n");
    
    if(VERBOSE)
    { System.out.println("SOURCE:"); System.out.println(resV1);
      System.out.println("TARGET:"); System.out.println(resV2);
    }    
    if(source!=null && target!=null)
    { if(model1.weight()==0 && model2.weight()==0)// empty value (Lists, Sets, Multisets and Mappings)
        System.out.println(Console.cpy(""+model1));
      else
      { Diff diff = Diff.newDiff(resTYPE, model1, model2);
        for(; !diff.refine(); );
        if(!VERBOSE && DIFF && UNICODE) Encoding.printUnicode(diff.beautify());
        //if(!VERBOSE && DIFF) System.out.println(""+diff);
        if(HTMLCODE) writeHTML(diff.html());
        if(SIM) System.out.println(diff.getSim().getPercentage());
      }
    }
    final long endTime   = System.currentTimeMillis();
    final long totalTime = (endTime - startTime)/1000;
    System.out.println("duration:"+totalTime+"s");
  }

  // build a html file and present diff in html format
  private static void writeHTML(String table)
  { if(htmlFileName!=null)
    try
    { FileWriter out = new FileWriter(htmlFileName);
      out.write(HTML.BODY(table));
      out.flush();
      out.close();
    }  
    catch(IOException e){ System.err.println("Promblem writing to:"+htmlFileName);}
  }    
 
  // model values with their TYPE
  // factory method 
  public static TypeT model(TYPE T, TypeT t)
  { // recursive type
    if(T.isREC() && TYPE.unfold(T).equals(t.typeOf()))
      return new TypeRec(T, t);
    // other types
    else if(T.equals(t.typeOf()))
    { if(T.isUNIT())return new PrimUnit(T);
      else if(T.isBOOL()) 
      { PrimBool v = (PrimBool)t; return new PrimBool(T, v.getValue());}
      else if(T.isCHAR())
      { PrimChar v = (PrimChar)t; return new PrimChar(T, v.getValue());} 
      else if(T.isSTRING())
      { PrimString v = (PrimString)t; return new PrimString(T, v.getValue());}    
      else if(T.isNAT())
      { PrimNat v = (PrimNat)t; return new PrimNat(T, v.getValue());}
      else if(T.isINT())
      { PrimInt v = (PrimInt)t; return new PrimInt(T, v.getValue());}  
      else if(T.isREAL())
      { PrimReal v = (PrimReal)t; return new PrimReal(T, v.getValue());} 
      else if(T.isPRODUCT())
      { TypeProduct v =(TypeProduct)t; return new TypeProduct(T, v.getLabels(), v.getValues());}   
      else if(T.isUNION())
      { TypeUnion v = (TypeUnion)t; return new TypeUnion(T, v.getLabel(), v.getValue());}    
      else if(T.isLIST())
      { TypeList v = (TypeList) t;
        if(v.isEmptyList()) return new TypeList(T.getBaseTYPE());
        else{ return new TypeList(T.getBaseTYPE(), v.getValue());}
      }
      else if(T.isSET())
      { TypeSet v = (TypeSet) t;
        if(v.isEmptySet()) return new TypeSet(T.getBaseTYPE());
        else{ return new TypeSet(T.getBaseTYPE(), v.getValue());}
      }    
      else if(T.isMSET())
      { TypeMultiset v = (TypeMultiset)t;
        if(v.isEmptyMultiset()) return new TypeMultiset(T.getBaseTYPE());
        else { return new TypeMultiset(T.getBaseTYPE(), v.getValue());}
      }
      else if(T.isMAPPING())
      { TypeMapping v = (TypeMapping)t;
        if(v.isEmptyMapping()) return new TypeMapping(T.getDOM(), T.getCOD());
        else{ return new TypeMapping(T.getDOM(), T.getCOD(), v.getDomFst(), v.getCodFst(), v.getRest());}
      }  
      else throw new RuntimeException("There is no other TYPE currently");
    }
    else throw new RuntimeException(T+"does not match the TYPE of "+t);
  }        
}
