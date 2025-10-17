package cs489.lab11;

 
public class App 
{
    public static void main( String[] args )
    {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(App.class.getName());
        int[][] aIn = new int[][]{ new int[]{1,3}, new int[]{0}, new int[]{4,5,9} };
        int[] aOut = ArrayFlattener.flattenArray(aIn);

        // Print the flattened array in the requested format
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < aOut.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(aOut[i]);
        }
        sb.append("]");
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(sb.toString());
        }
    }
}
