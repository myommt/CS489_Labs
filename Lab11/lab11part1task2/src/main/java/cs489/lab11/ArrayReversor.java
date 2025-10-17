package cs489.lab11;

public class ArrayReversor {
    private final IArrayFlattenerService flattener;

    public ArrayReversor(IArrayFlattenerService flattener) {
        this.flattener = flattener;
    }

    public int[] reverseArray(int[][] input) {
        if (input == null) {
            return null;
        }

        int[] flat = flattener.flattenArray(input);
        if (flat == null) {
            return null;
        }

        int n = flat.length;
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            out[i] = flat[n - 1 - i];
        }
        return out;
    }

    @Override
    public String toString() {
        return "ArrayReversor using flattener=" + (flattener == null ? "null" : flattener.getClass().getName());
    }
}
