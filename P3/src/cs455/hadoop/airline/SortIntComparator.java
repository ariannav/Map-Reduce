//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class SortIntComparator extends WritableComparator {

    //Constructor.
    protected SortIntComparator() {
        super(IntWritable.class, true);
    }

    @SuppressWarnings("rawtypes")

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
        IntWritable i1 = (IntWritable)w1;
        IntWritable i2 = (IntWritable)w2;

        return -1 * i1.compareTo(i2);
    }
}