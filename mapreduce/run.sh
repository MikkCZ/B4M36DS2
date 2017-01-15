#!/bin/bash
LOGIN="stankmic"
#mvn clean package
mkdir classes
javac -classpath /usr/local/hadoop/share/hadoop/common/hadoop-common-2.7.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.3.jar -d classes/ src/main/java/cz/cvut/fel/stankmic/ds2/mapreduce/AmusementParkRunner.java src/main/java/cz/cvut/fel/stankmic/ds2/mapreduce/DurationStatsReducer.java src/main/java/cz/cvut/fel/stankmic/ds2/mapreduce/VisitorInfoMapper.java
jar -cvf stankmic-mapreduce.jar -C classes/ .
hadoop fs -mkdir "/user/$LOGIN/stankmic-input-data/"
hadoop fs -copyFromLocal ./src/main/resources/data.csv "/user/$LOGIN/stankmic-input-data/data.csv"
#hadoop jar ./target/amusement-park-1.0.jar "/user/$LOGIN/stankmic-input-data/data.csv" "/user/$LOGIN/stankmic-output-data/"
hadoop jar ./stankmic-mapreduce.jar cz.cvut.fel.stankmic.ds2.mapreduce.AmusementParkRunner "/user/$LOGIN/stankmic-input-data/data.csv" "/user/$LOGIN/stankmic-output-data/"
hadoop fs -copyToLocal "/user/$LOGIN/stankmic-output-data/part-r-00000" ./result.txt
hadoop fs -rmr "/user/$LOGIN/stankmic-input-data/"
hadoop fs -rmr "/user/$LOGIN/stankmic-output-data/"
less result.txt

