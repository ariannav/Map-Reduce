//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class PlaneMapper extends Mapper<LongWritable, Text, Text, Text> {

    //Parses the carriers file.
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] values = value.toString().split(",");

        if(values.length > 9){
            //If it is not from the plane-data, but from Q5 output.
            values = value.toString().split("\t");
            context.write(new Text(values[0]), new Text(values[1]));
        }
        else{
            if(values[0].equals("tailnum")){
                //First line of plane data file.
                return;
            }

            try{
                //Outputs the tailnum, and the manufactured year.
                context.write(new Text("n:" + values[0]), new Text(values[8]));
            }
            catch(Exception e){
                //Skip this line.
                return;
            }
        }
    }
}