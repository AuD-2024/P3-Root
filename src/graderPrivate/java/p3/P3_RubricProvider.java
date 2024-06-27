package p3;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.Grader;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import p3.graph.AdjacencyGraphTest;
import p3.graph.AdjacencyMatrixTest;
import p3.solver.BellmanFordCalculatorTest;
import p3.solver.DFSTest;
import p3.solver.PrimCalculatorTest;
import p3.solver.TopologicalSortTest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class P3_RubricProvider implements RubricProvider {

    @SafeVarargs
    private static Criterion createCriterion(String shortDescription, int maxPoints, Callable<Method>... methodReferences) {
        return createCriterion(shortDescription, maxPoints, Arrays.stream(methodReferences).map(JUnitTestRef::ofMethod).toArray(JUnitTestRef[]::new));
    }

    private static Criterion createCriterion(String shortDescription, int maxPoints, JUnitTestRef... testRefs) {

        if (testRefs.length == 0) {
            return Criterion.builder()
                .shortDescription(shortDescription)
                .maxPoints(maxPoints)
                .build();
        }

        Grader.TestAwareBuilder graderBuilder = Grader.testAwareBuilder();

        for (JUnitTestRef testRef : testRefs) {
            graderBuilder.requirePass(testRef);
        }

        return Criterion.builder()
            .shortDescription(shortDescription)
            .grader(graderBuilder
                .pointsFailedMin()
                .pointsPassedMax()
                .build())
            .maxPoints(maxPoints)
            .build();
    }

    private static Criterion createParentCriterion(String task, String shortDescription, Criterion... children) {
        return Criterion.builder()
            .shortDescription("H" + task + " | " + shortDescription)
            .addChildCriteria(children)
            .build();
    }

    public static final Criterion H1_1_1 = createCriterion("Die Methoden [[[hasEdge]]] und [[[addEdge]]] der Klasse [[[AdjacencyMatrix]]] funktionieren vollständig korrekt", 1,
        () -> AdjacencyMatrixTest.class.getMethod("testHasEdge", JsonParameterSet.class),
        () -> AdjacencyMatrixTest.class.getMethod("testAddEdge", JsonParameterSet.class));

    public static final Criterion H1_1_2 = createCriterion("Die Methode [[[getAdjacentIndices]]] der Klasse [[[AdjacencyMatrix]]] funktioniert vollständig korrekt", 1,
        () -> AdjacencyMatrixTest.class.getMethod("testGetAdjacentIndices", JsonParameterSet.class));

    public static final Criterion H1_1 = createParentCriterion("1 a)", "AdjacencyMatrix", H1_1_1, H1_1_2);

    public static final Criterion H1_2_1 = createCriterion("Die Methoden [[[hasEdge]]] und [[[addEdge]]] der Klasse [[[AdjacencyList]]] funktionieren vollständig korrekt", 1,
        () -> AdjacencyMatrixTest.class.getMethod("testHasEdge", JsonParameterSet.class),
        () -> AdjacencyMatrixTest.class.getMethod("testAddEdge", JsonParameterSet.class));

    public static final Criterion H1_2_2 = createCriterion("Die Methode [[[getAdjacentIndices]]] der Klasse [[[AdjacencyList]]] funktioniert vollständig korrekt", 1,
        () -> AdjacencyMatrixTest.class.getMethod("testGetAdjacentIndices", JsonParameterSet.class));

    public static final Criterion H1_2 = createParentCriterion("1 b)", "AdjacencyList", H1_2_1, H1_2_2);

    public static final Criterion H1_3_1 = createCriterion("Die Methode [[[addNode]]] der Klasse [[[AdjacencyGraph]]] funktioniert vollständig korrekt", 1,
        () -> AdjacencyGraphTest.class.getMethod("testAddNode", JsonParameterSet.class));

    public static final Criterion H1_3_2 = createCriterion("Die Methode [[[addEdge]]] der Klasse [[[AdjacencyGraph]]] funktioniert vollständig korrekt", 1,
        () -> AdjacencyGraphTest.class.getMethod("testAddEdge", JsonParameterSet.class));

    public static final Criterion H1_3_3 = createCriterion("Die Methode [[[getEdge]]] der Klasse [[[AdjacencyGraph]]] funktioniert vollständig korrekt", 1,
        () -> AdjacencyGraphTest.class.getMethod("testGetEdge", JsonParameterSet.class));

    public static final Criterion H1_3_4 = createCriterion("Der Konstruktor der Klasse [[[AdjacencyGraph]]] funktioniert vollständig korrekt", 1,
        () -> AdjacencyGraphTest.class.getMethod("testConstructor", JsonParameterSet.class));

    public static final Criterion H1_3_5 = createCriterion("Die Methode [[[getOutgoingEdges]]] der Klasse [[[AdjacencyGraph]]] funktioniert vollständig korrekt", 2,
        () -> AdjacencyGraphTest.class.getMethod("testGetOutgoingEdges", JsonParameterSet.class));

    public static final Criterion H1_3 = createParentCriterion("1 c)", "AdjacencyGraph", H1_3_1, H1_3_2, H1_3_3, H1_3_4, H1_3_5);

    public static final Criterion H1 = createParentCriterion("1", "Graphenrepräsentationen", H1_1, H1_2);

    public static final Criterion H2_1_1 = createCriterion("Die Methode [[[init]]] der Klasse [[[DFS]]] funktioniert vollständig korrekt", 1,
        () -> DFSTest.class.getMethod("testInit", JsonParameterSet.class));

    public static final Criterion H2_1_2 = createCriterion("Die Methode [[[traverse]]] der Klasse [[[DFS]]] funktioniert vollständig korrekt", 1,
        () -> DFSTest.class.getMethod("testTraverse", JsonParameterSet.class));

    public static final Criterion H2_1_3 = createCriterion("Die Methode [[[visit]]] der Klasse [[[DFS]]] funktioniert korrekt wenn der buschte Knoten keine ausgehenden Kanten besitzt", 1,
        () -> DFSTest.class.getMethod("testVisitNoOutgoingEdges", JsonParameterSet.class));

    public static final Criterion H2_1_4 = createCriterion("Die Methode [[[visit]]] der Klasse [[[DFS]]] funktioniert vollständig korrekt", 1,
        () -> DFSTest.class.getMethod("testVisit", JsonParameterSet.class));

    public static final Criterion H2_1 = createParentCriterion("2 a)", "Depth-First-Search", H2_1_1, H2_1_2, H2_1_3, H2_1_4);

    public static final Criterion H2_2_1 = createCriterion("Die Methode [[[isCyclic]]] der Klasse [[[DFS]]] funktioniert vollständig korrekt", 2,
        () -> DFSTest.class.getMethod("testCyclic", JsonParameterSet.class));

    public static final Criterion H2_2 = createParentCriterion("2 b)", "Zyklenerkennung", H2_2_1);

    public static final Criterion H2_3_1 = createCriterion("Die Methode [[[sort]]] der Klasse [[[TopologicalSort]]] funktioniert vollständig korrekt", 2,
        () -> TopologicalSortTest.class.getMethod("testSortNonCyclic", JsonParameterSet.class),
        () -> TopologicalSortTest.class.getMethod("testSortCyclic", JsonParameterSet.class));

    public static final Criterion H2_3 = createParentCriterion("2 c)", "Topologische Sortierung", H2_3_1);

    public static final Criterion H2 = createParentCriterion("2", "Deapth-First-Search + Topologische Sortierung", H2_1, H2_2, H2_3);

    public static final Criterion H3_1 = createCriterion("Die Methode [[[initSSSP]]] der Klasse [[[BellmanFordPathCalculator]]] funktioniert vollständig korrekt", 1,
        () -> BellmanFordCalculatorTest.class.getMethod("testInitSSSP", JsonParameterSet.class));

    public static final Criterion H3_2 = createCriterion("Die Methode [[[relax]]] der Klasse [[[BellmanFordPathCalculator]]] funktioniert vollständig korrekt", 1,
        () -> BellmanFordCalculatorTest.class.getMethod("testRelax", JsonParameterSet.class));

    public static final Criterion H3_3 = createCriterion("Die Methode [[[processGraph]]] der Klasse [[[BellmanFordPathCalculator]]] funktioniert vollständig korrekt", 1,
        () -> BellmanFordCalculatorTest.class.getMethod("testProcessGraph", JsonParameterSet.class));

    public static final Criterion H3_4 = createCriterion("Die Methode [[[hasNegativeCycle]]] der Klasse [[[BellmanFordPathCalculator]]] funktioniert vollständig korrekt", 1,
        () -> BellmanFordCalculatorTest.class.getMethod("testHasNegativeCycle", JsonParameterSet.class));

    public static final Criterion H3_5 = createCriterion("Die Methode [[[calculatePath]]] der Klasse [[[BellmanFordPathCalculator]]] funktioniert vollständig korrekt", 1,
        () -> BellmanFordCalculatorTest.class.getMethod("testCalculatePath", JsonParameterSet.class));

    public static final Criterion H3 = createParentCriterion("3", "Bellman-Ford", H3_1, H3_2, H3_3, H3_4, H3_5);

    public static final Criterion H4_1 = createCriterion("Die Methode [[[init]]] der Klasse [[[PrimMSTCalculator]]] funktioniert vollständig korrekt", 1,
        () -> PrimCalculatorTest.class.getMethod("testInit", JsonParameterSet.class));

    public static final Criterion H4_2 = createCriterion("Die Methode [[[extractMin]]] der Klasse [[[PrimMSTCalculator]]] funktioniert vollständig korrekt", 2,
        () -> PrimCalculatorTest.class.getMethod("testExtractMin", JsonParameterSet.class));

    public static final Criterion H4_3 = createCriterion("Die Methode [[[processNode]]] der Klasse [[[PrimMSTCalculator]]] funktioniert vollständig korrekt", 1,
        () -> PrimCalculatorTest.class.getMethod("testProcessNode", JsonParameterSet.class));

    public static final Criterion H4_5 = createCriterion("Die Methode [[[calculateMST]]] der Klasse [[[PrimMSTCalculator]]] funktioniert vollständig korrekt", 2,
        () -> PrimCalculatorTest.class.getMethod("testCalculateMSTEdges", JsonParameterSet.class));

    public static final Criterion H4_4 = createCriterion("Die Methode [[[calculateMST]]] der Klasse [[[PrimMSTCalculator]]] funktioniert vollständig korrekt", 1,
        () -> PrimCalculatorTest.class.getMethod("testCalculateMST", JsonParameterSet.class));

    public static final Criterion H4 = createParentCriterion("4", "Prim", H4_1, H4_2, H4_3, H4_4, H4_5);

    @Override
    public Rubric getRubric() {
        return Rubric.builder()
            .title("P3")
            .addChildCriteria(H1, H2, H3, H4)
            .build();
    }

}
