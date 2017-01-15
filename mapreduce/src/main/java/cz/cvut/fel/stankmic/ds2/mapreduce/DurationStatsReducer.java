package cz.cvut.fel.stankmic.ds2.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DurationStatsReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        List<Duration> sortedDiffs = parseAndSortDurations(values);

        long count = sortedDiffs.size();
        long average = (long) sortedDiffs.stream().mapToLong(Duration::getSeconds).average().getAsDouble();
        long median = median(sortedDiffs);

        context.write(key, new Text(String.format("COUNT: %1$d, AVG: %2$d, MEDIAN: %3$d", count, average, median)));
    }

    List<Duration> parseAndSortDurations(Iterable<Text> values) {
        return StreamSupport.stream(values.spliterator(), false)
                .map(Text::toString)
                .map(s -> s.split(","))
                .map(timestamps -> {
                    Instant purchase = Instant.ofEpochSecond(Long.valueOf(timestamps[0]));
                    Instant visit = Instant.ofEpochSecond(Long.valueOf(timestamps[1]));
                    return Duration.between(purchase, visit);
                })
                .sorted()
                .collect(Collectors.toList());
    }

    long median(List<Duration> sortedDurations) {
        long median;
        int count = sortedDurations.size();
        int pivot = count/2;
        if (count%2 != 0) { // odd
            median = sortedDurations.get(pivot).getSeconds();
        } else { // even
            median = (sortedDurations.get(pivot).getSeconds() + sortedDurations.get(pivot-1).getSeconds()) / 2;
        }
        return median;
    }
}
