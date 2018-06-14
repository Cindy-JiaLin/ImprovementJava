package utility;

import sim.Sim;
public abstract class EditOperation
{ abstract boolean refine();
  // return true if there was a refinement possible, false otherwise
  abstract int nextA(int ia);
  // return the position of the element in a after a specific edit EditOperation
  abstract int nextB(int ib);
  // return the position of the element in b after a specific edit EditOperation
  abstract Sim calculate(Sim sim);

  abstract String beautify();
  abstract String html(int ia, int ib);
}
