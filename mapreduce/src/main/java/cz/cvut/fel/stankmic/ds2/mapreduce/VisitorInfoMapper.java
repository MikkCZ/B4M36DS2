package cz.cvut.fel.stankmic.ds2.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VisitorInfoMapper extends Mapper<Object, Text, Text, Text> {
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] visitorInfo = value.toString().split(",");
        String name = visitorInfo[0];
        String city = visitorInfo[1];
        Long purchaseTimestamp = Long.valueOf(visitorInfo[2]);
        Long visitTimestamp = Long.valueOf(visitorInfo[3]);
        String found = visitorInfo[4];

        String outputVal = Stream.of(purchaseTimestamp, visitTimestamp).map(String::valueOf).collect(Collectors.joining(","));

        context.write(new Text("CITY:" + city), new Text(outputVal));
        context.write(new Text("FOUND:" + found), new Text(outputVal));
    }
}
