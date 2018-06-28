package value;

import type.TYPE;

//There is no value represented by null
public abstract class TypeT 
{ public abstract TYPE typeOf();
  public abstract int weight();
 
  // get a member of the value at the position i 
  public static TypeT baseValue(TYPE T, TypeT value, int i)
  { if (!value.typeOf().equals(T)) 
      throw new RuntimeException("Type Mismatch: "+value+" not of expected type: "+T);
    else if (T.isSTRING())
    { PrimString str = (PrimString)value;
      return new PrimChar(TYPE.CHAR, str.charAt(i));
    }
    else if (T.isPRODUCT())
    { TypeProduct product = (TypeProduct)value;
      return product.getValues().get(i);
    }
    else if (T.isUNION())
    { TypeUnion union = (TypeUnion)value;
      return union.getValue();
    }
    else if (T.isLIST()) 
    { TypeList list = (TypeList)value;
      return list.get(i);
    }
    else if (T.isSET())
    { TypeSet set = (TypeSet)value;
      return set.get(i);
    }
    else if (T.isMSET())
    { TypeMultiset multiset = (TypeMultiset)value;
      return multiset.get(i);
    }
    //else if (T.isMAPPING())
    //{ TypeMapping mapping = (TypeMapping)value;
      //return mapping.get(i);
    //}
    else return value;//if (T.isPRIMITIVE()||T.isREAL())
  }
}
