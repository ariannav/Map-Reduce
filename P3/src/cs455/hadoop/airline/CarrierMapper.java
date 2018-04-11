//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class CarrierMapper extends Mapper<LongWritable, Text, Text, Text> {

    //Parses the carriers file.
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] values = value.toString().split(",");

        //Output from last job, can print in current form.
        if(values.length == 1){
            values = value.toString().split("\t");
            //Writes carrier code and corresponding values. 
            context.write(new Text(values[0]), new Text(values[1]));
            return;
        }

        //Input from carriers.csv
        if(values[0].equals("Code")){
            return; //Skip this line, it is the first line of the file.
        }

        //Write c:Carrier Code, Carrier Name.
        context.write(new Text("c:" + values[0].substring(1,values[0].length()-1)), new Text(values[1].substring(1,values[1].length()-1)));
    }
}