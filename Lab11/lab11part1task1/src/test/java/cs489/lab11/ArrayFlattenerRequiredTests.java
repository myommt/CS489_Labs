package cs489.lab11;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Required tests for ArrayFlattener as per assignment:
 *  a) Legit 2-D array [[1,3],[0],[4,5,9]]
 *  b) Null input
 */
public class ArrayFlattenerRequiredTests {

    @Test
    public void testFlattenLegitArray() {
        int[][] input = new int[][]{ new int[]{1,3}, new int[]{0}, new int[]{4,5,9} };
        int[] expected = new int[]{1,3,0,4,5,9};
        int[] actual = ArrayFlattener.flattenArray(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testFlattenNullInput() {
        int[] actual = ArrayFlattener.flattenArray(null);
        assertNull(actual);
    }
}
