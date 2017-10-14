package graphtea.extensions.reports.planarity.planaritypq;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import graphtea.extensions.reports.planarity.planaritypq.PQNode;

public class PQHelpers {
    public static void setCircularLinks(List<PQNode> nodes) {
        int modulo = nodes.size();
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println("At " + i + ", so we are setting " + (Math.floorMod(i-1, modulo)) + " and " + (Math.floorMod(i+1, modulo)));
            nodes.get(i).circularLink_prev = nodes.get(Math.floorMod(i-1, modulo));
            nodes.get(i).circularLink_next = nodes.get(Math.floorMod(i+1, modulo));
        }
    }

    public static <E> boolean subset(List<E> list1, List<E> list2) {
        return list2.containsAll(list1);
    }

    public static <E> List<E> intersection(List<E> list1, List<E> list2){
        list1.retainAll(list2);
        return list1;
    }


    public static <E> List<E> union(List<E> list1, List<E> list2) {
        HashSet<E> set = new HashSet<>();

        // set does not insert duplicates
        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }

}
