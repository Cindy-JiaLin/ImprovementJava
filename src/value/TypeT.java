package value;

import type.TYPE;

//There is no value represented by null
public abstract class TypeT 
{ public abstract TYPE typeOf();
  public abstract int weight();
 
  // get a member of the value at the position i 
  public static TypeT baseValue(TYPE T, TypeT value, int i)
  { if (!value.typeOf().equals(T)) throw new RuntimeException("Type Mismatch: "+value+" not of expected type: "+T);
    else if (T.isSTRING()) return (PrimString)value.charAt(i);
    else if (T.isPRODUCT()) return (TypeProduct)value.getValues().get(i);
    else if (T.isUNION()) return (TypeUnion)value.getValue();
    else if (T.isLIST()) return (TypeList)value.get(i);
    else if (T.isSET()) return (TypeSet)value.get(i);
    else if (T.isMSET()) return (TypeMultiset)value.get(i);
    else if (T.isMAPPING()) return (TypeMapping)value.get(i);
    else return value;//if (T.isPRIMITIVE()||T.isREAL())
  }
}
