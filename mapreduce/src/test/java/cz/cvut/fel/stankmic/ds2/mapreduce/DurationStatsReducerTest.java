package cz.cvut.fel.stankmic.ds2.mapreduce;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class DurationStatsReducerTest {

    @Test
    public void parsesAndSortsDurationsCorrectly() {
        // Arrange
        DurationStatsReducer durationStatsReducer = new DurationStatsReducer();
        Iterable<Text> values = ImmutableList.of(
                new Text("0,0"),
                new Text("-8,0"),
                new Text("-8,2"),
                new Text("0,12"),
                new Text("2,18")
        );

        // Act
        List<Duration> durations = durationStatsReducer.parseAndSortDurations(values);

        // Assert
        assertThat(durations, hasSize(5));
        assertThat(durations.get(0), is(Duration.ofSeconds(0)));
        assertThat(durations.get(1), is(Duration.ofSeconds(8)));
        assertThat(durations.get(2), is(Duration.ofSeconds(10)));
        assertThat(durations.get(3), is(Duration.ofSeconds(12)));
        assertThat(durations.get(4), is(Duration.ofSeconds(16)));
    }

    @Test
    public void countsMedianFromSortedListOfOneElement() {
        // Arrange
        DurationStatsReducer durationStatsReducer = new DurationStatsReducer();

        // Act
        long median = durationStatsReducer.median(ImmutableList.of(Duration.ofHours(1)));

        // Assert
        assertThat(median, is(Duration.ofHours(1).getSeconds()));
    }

    @Test
    public void countsMedianFromSortedListOfOddElements() {
        // Arrange
        DurationStatsReducer durationStatsReducer = new DurationStatsReducer();

        // Act
        long median = durationStatsReducer.median(ImmutableList.of(Duration.ofHours(1), Duration.ofHours(2), Duration.ofHours(3)));

        // Assert
        assertThat(median, is(Duration.ofHours(2).getSeconds()));
    }

    @Test
    public void countsMedianFromSortedListOfTwoElements() {
        // Arrange
        DurationStatsReducer durationStatsReducer = new DurationStatsReducer();

        // Act
        long median = durationStatsReducer.median(ImmutableList.of(Duration.ofHours(10), Duration.ofHours(20)));

        // Assert
        assertThat(median, is(Duration.ofHours(15).getSeconds()));
    }

    @Test
    public void countsMedianFromSortedListOfEvenElements() {
        // Arrange
        DurationStatsReducer durationStatsReducer = new DurationStatsReducer();

        // Act
        long median = durationStatsReducer.median(ImmutableList.of(Duration.ofHours(10), Duration.ofHours(20), Duration.ofHours(30), Duration.ofHours(40)));

        // Assert
        assertThat(median, is(Duration.ofHours(25).getSeconds()));
    }

    @Test
    public void reducesValuesCorrectly() throws Exception {
        // Arrange
        Text key = new Text("key");
        Iterable<Text> values = ImmutableList.of(
                new Text("0,0"),
                new Text("0,12"),
                new Text("2,18")
        );
        Context context = Mockito.mock(Context.class);
        DurationStatsReducer durationStatsReducer = new DurationStatsReducer();

        // Act
        durationStatsReducer.reduce(key, values, context);

        // Assert
        long count = 3;
        long average = (0+12+16)/count;
        long median = 12-0;
        Mockito.verify(context).write(
                key,
                new Text(String.format("COUNT: %1$d, AVG: %2$s, MEDIAN: %3$s",
                        count,
                        DurationFormatUtils.formatDuration(Duration.ofSeconds(average).toMillis(), "d H:mm:ss"),
                        DurationFormatUtils.formatDuration(Duration.ofSeconds(median).toMillis(), "d H:mm:ss")
                ))
        );
    }
}