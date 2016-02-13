package apache.parser.baseline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Jaccard {

	
	public double getJaccardIndex(ArrayList<Double> high, ArrayList<Double> low){
		for(int i = 0; i < high.size(); i++){
			if(high.get(i) > 0){
				high.set(i, (double)i);
			}
		}
		
		for(int i = 0; i < low.size(); i++){
			if(low.get(i) > 0){
				low.set(i, (double)i);
			}
		}
		
		double intersection = intersection(high, low).size();
		double union = union(high, low).size();
		if(union == 0) return 0.0;
		
		double jaccardIndex = intersection / union;
		return jaccardIndex;
		
	}
	
	
	private List<Double> union(ArrayList<Double> list1, ArrayList<Double> list2) {
        Set<Double> set = new HashSet<Double>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<Double>(set);
    }

    private Set<Double> intersection(ArrayList<Double> list1, ArrayList<Double> list2) {
        Set<Double> list = new HashSet<Double>();

        for (Double t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}

