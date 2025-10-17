package cs489.lab11;
 
public class ArrayFlattener {

 
    public static int[] flattenArray(int[][] input) {
        if (input == null) {
            return null;
        }

        // First pass: compute total length safely (skip null rows)
        int total = 0;
        for (int i = 0; i < input.length; i++) {
            if (input[i] != null) {
                total += input[i].length;
            }
        }

        int[] result = new int[total];
        int idx = 0;
        for (int i = 0; i < input.length; i++) {
            int[] row = input[i];
            if (row == null) {
                continue;
            }
            for (int j = 0; j < row.length; j++) {
                result[idx++] = row[j];
            }
        }
        return result;
    }
}
