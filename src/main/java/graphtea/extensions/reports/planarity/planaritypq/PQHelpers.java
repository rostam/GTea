package graphtea.extensions.reports.planarity.planaritypq;

import java.lang.Math;
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
}
