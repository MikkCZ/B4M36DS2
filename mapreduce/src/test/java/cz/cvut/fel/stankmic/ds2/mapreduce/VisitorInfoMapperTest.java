package cz.cvut.fel.stankmic.ds2.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.junit.Test;
import org.mockito.Mockito;

public class VisitorInfoMapperTest {

    @Test
    public void emitsRightValues() throws Exception {
        // Arrange
        Object key = null;
        Text value = new Text("John Doe,Gotham,-1763942400,127699200,Bob Kane told me");
        Context context = Mockito.mock(Context.class);
        VisitorInfoMapper visitorInfoMapper = new VisitorInfoMapper();

        // Act
        visitorInfoMapper.map(key, value, context);

        // Assert
        Mockito.verify(context).write(new Text("CITY:Gotham"), new Text("-1763942400,127699200"));
        Mockito.verify(context).write(new Text("FOUND:Bob Kane told me"), new Text("-1763942400,127699200"));
    }

}