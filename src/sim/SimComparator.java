package sim;
import java.util.Comparator;
public static class SimComparator implements Comparator<PartialSolution>
{ public int compare(PartialSolution sol1, PartialSolution sol2)
  { if(sol1.getSim()==null) return (sol2.getSim()==null ? 0 : 1);
    else return -sol1.getSim().compareTo(sol2.getSim());
   //reverse order, Array.sort is ascending, we need descending
  }
}

