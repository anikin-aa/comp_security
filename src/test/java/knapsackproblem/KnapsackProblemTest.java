package knapsackproblem;

import algorithms.CipherInterface;
import algorithms.knapsackproblem.KnapsackProblemImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KnapsackProblemTest {

    @Test
    public void KnapsackTest() throws Exception {
        CipherInterface knapsack = new KnapsackProblemImpl("2,7,11,21,42,89,180,354,456");
        String textToEncrypt = "KnappsackTest";
        String encrypted = knapsack.encrypt(textToEncrypt);
        assertEquals(encrypted, knapsack.decrypt(encrypted));
    }
}
