package cs489.lab11;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(value = MockitoJUnitRunner.class)
public class ArrayReversorTest {

    @Mock
    IArrayFlattenerService mockFlattener;

    @Test
    public void testReverseArray_withLegitInput() {
       
        ArrayReversor reversor = new ArrayReversor(mockFlattener);
        int[][] input = new int[][] { {1,3}, {0}, {4,5,9} };
        // The flattener would return flattened array [1,3,0,4,5,9]
        when(mockFlattener.flattenArray(input)).thenReturn(new int[] {1,3,0,4,5,9});

        

        // Act
        int[] out = reversor.reverseArray(input);

        // Assert
        assertArrayEquals(new int[] {9,5,4,0,3,1}, out);
        // verify that flattener was called once with the input
        verify(mockFlattener, times(1)).flattenArray(input);
        verifyNoMoreInteractions(mockFlattener); 
    }

    @Test
    public void testReverseArray_withNullInput() {
        // Arrange
    // No need to manually mock, @Mock will handle it

        ArrayReversor reversor = new ArrayReversor(mockFlattener);

        // Act
        int[] out = reversor.reverseArray(null);

        // Assert
        // Expect null output when input is null
        org.junit.Assert.assertNull(out);
        // Depending on implementation, flattener should not be called when input is null
        verify(mockFlattener, never()).flattenArray(any(int[][].class));
        verifyNoMoreInteractions(mockFlattener);
    }
}
