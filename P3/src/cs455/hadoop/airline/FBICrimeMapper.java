//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class FBICrimeMapper extends Mapper<LongWritable, Text, Text, Text> {

    //Parses the q7CrimeData file.
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] values = value.toString().split(":");

        try{
            //Write c:Carrier Code, Carrier Name.
            String noCommas = values[1].replace(",", "");
            context.write(new Text("f:" + noCommas), new Text(values[0]));
        }
        catch(Exception e){
            //Bad data, pass it.
        }

    }
}