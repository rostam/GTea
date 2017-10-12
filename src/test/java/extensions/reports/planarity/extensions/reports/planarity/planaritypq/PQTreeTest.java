package extensions.reports.planarity.extensions.reports.planarity.planaritypq;

import graphtea.extensions.reports.planarity.planaritypq.PQ;
import graphtea.extensions.reports.planarity.planaritypq.PQNode;
import graphtea.graph.graph.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class PQTreeTest {

    @Test
    public void unionTest(){
        List<PQNode> initialiser = new ArrayList<>();
        for(int i=0; i<10; i++){
            // todo: Creating a PQNode throws a null exception
            PQNode n = new PQNode();
            initialiser.add(n);

        }
        List<PQNode> list1 = new LinkedList<>(initialiser.subList(0, 2));
        List<PQNode> list2 = new LinkedList<>(initialiser.subList(2, 4));

        List<PQNode> tester = PQ.union(list1, list2);

        assertTrue(tester.size() == 6);

        /*assertTrue(tester.get(0).getId() == 0);
        assertTrue(tester.get(1).getId() == 1);
        assertTrue(tester.get(2).getId() == 2);
        assertTrue(tester.get(3).getId() == 3);
        assertTrue(tester.get(4).getId() == 4);*/

    }

    @Test
    public void intersectionTest(){
        List<PQNode> initialiser = new ArrayList<>();
        for(int i=0; i<10; i++){
            initialiser.add(new PQNode());

        }
        List<PQNode> list1 = new LinkedList<>(initialiser.subList(0, 5));
        List<PQNode> list2 = new LinkedList<>(initialiser.subList(3, 9));

        List<PQNode> tester = PQ.intersection(list1, list2);

        // Checks if test has elements from indices 3,4,5 and no others.
        assertTrue(tester.size() == 3);

        assertTrue(tester.get(0).getId() == 3);
        assertTrue(tester.get(1).getId() == 4);
        assertTrue(tester.get(2).getId() == 5);
    }

    @Test
    public void subsetTest(){

    }

    @Test
    public void bubbleTest(){

    }

    @Test
    public void reduceTest(){

    }

    @Test
    public void templateP1Test(){

    }

    @Test
    public void templateP2Test(){

    }

    @Test
    public void templateP3Test(){

    }

    @Test
    public void templateP4Test(){

    }

    @Test
    public void templateP5Test(){

    }

    @Test
    public void templateP6Test(){

    }

    @Test
    public void templateQ1Test(){

    }

    @Test
    public void templateQ2Test(){

    }

    @Test
    public void templateL1Test(){

    }

}
