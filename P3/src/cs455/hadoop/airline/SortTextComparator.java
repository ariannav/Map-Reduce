//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import java.lang.Integer;

public class SortTextComparator extends WritableComparator {
    @SuppressWarnings("rawtypes")

    //Constructor.
    protected SortTextComparator() {
        super(Text.class, true);
    }

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
        Text i1 = (Text)w1;
        Text i2 = (Text)w2;

        if(i1.toString().charAt(0) == 't' && i2.toString().charAt(0) == 't'){
            int num1 = Integer.parseInt(i1.toString().substring(2));
            int num2 = Integer.parseInt(i2.toString().substring(2));
            return -1 * Integer.compare(num1, num2);
        }

        //Reverse sorted order.
        return i1.compareTo(i2);
    }
}