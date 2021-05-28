package com.test.influxtest;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
//import humidfromtemp.Class1;
import Online_Novelty.Class1;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// @SpringBootApplication
public class InfluxtestApplication {

    public static void main(String[] args) {
        // SpringApplication.run(InfluxtestApplication.class, args);

        final String serverURL = "http://influxdb:8086", username = "femiot", password = "mcia1234"; //FOR DOCKER
        // final String serverURL = "http://localhost:8086", username = "femiot", password = "mcia1234"; //FOR IntelliJ
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL,username,password);

        String databaseName = "pruebadb";
        influxDB.setDatabase(databaseName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        String lastacquired = "'1970-01-01T00:00:00.000Z'";

        boolean var = true;
        while (var) {

            //influxDB.write(Point.measurement("data3")
            //        .time(time, TimeUnit.MILLISECONDS)
            //        .tag("user", "gorka")
            //        .addField("temperature", temperature)
            //        .build());

            QueryResult queryResult = influxDB.query(new Query("SELECT * FROM dataset_vibracio WHERE time > "+lastacquired));

            // List<String> columns =  ((queryResult.getResults().get(0)).getSeries().get(0)).getColumns();

            if((queryResult.getResults().get(0)).getSeries() != null ){

                List<List<Object>> values =  ((queryResult.getResults().get(0)).getSeries().get(0)).getValues();


                List<Double> X_RMS = new ArrayList<>();
                List<Double> Y_RMS = new ArrayList<>();
                List<Double> Z_RMS = new ArrayList<>();
                List<Double> X_MEAN = new ArrayList<>();
                List<Double> Y_MEAN = new ArrayList<>();
                List<Double> Z_MEAN = new ArrayList<>();
                List<Double> X_MAX = new ArrayList<>();
                List<Double> Y_MAX = new ArrayList<>();
                List<Double> Z_MAX = new ArrayList<>();
                List<Double> X_MIN = new ArrayList<>();
                List<Double> Y_MIN = new ArrayList<>();
                List<Double> Z_MIN = new ArrayList<>();
                List<Double> VALID = new ArrayList<>();
                List<Long> times = new ArrayList<>();

                DateFormat formatter;
                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

                for (List<Object> next : values){
                    String temporal = (String) next.get(0);
                    temporal = temporal.substring(0,temporal.length()-1);
                    if(temporal.length() == 19){
                        temporal = temporal.concat(".");
                    }
                    while(temporal.length() < 23) {
                        temporal = temporal.concat("0");
                    }
                    try{
                        times.add((formatter.parse(temporal)).getTime()); // Add 1 hour for timezone if used in intellij
                    } catch (ParseException e){
                        e.printStackTrace();
                        var = false;
                    }

                    X_MAX.add((Double) next.get(2));
                    X_MEAN.add((Double) next.get(3));
                    X_MIN.add((Double) next.get(4));
                    X_RMS.add((Double) next.get(5));
                    Y_MAX.add((Double) next.get(6));
                    Y_MEAN.add((Double) next.get(7));
                    Y_MIN.add((Double) next.get(8));
                    Y_RMS.add((Double) next.get(9));
                    Z_MAX.add((Double) next.get(10));
                    Z_MEAN.add((Double) next.get(11));
                    Z_MIN.add((Double) next.get(12));
                    Z_RMS.add((Double) next.get(13));



                }



                int length = X_RMS.size();

                lastacquired = "'";
                lastacquired = lastacquired.concat((String) values.get(length-1).get(0));
                lastacquired = lastacquired.concat("'");

                Object[] Objresult;

                try {
                    Class1 noveltyclass = new Class1();
                    for (int i=0; i < length ; i++) {

                        Objresult = noveltyclass.Online_Novelty(1, X_RMS.get(i), X_MEAN.get(i), X_MAX.get(i), X_MIN.get(i), Y_RMS.get(i), Y_MEAN.get(i), Y_MAX.get(i), Y_MIN.get(i), Z_RMS.get(i), Z_MEAN.get(i), Z_MAX.get(i), Z_MIN.get(i));
                        MWNumericArray temp = (MWNumericArray) Objresult[0];
                        VALID.add(temp.getDouble());

                        influxDB.write(Point.measurement("dataset_vibracio")
                                .time(times.get(i), TimeUnit.MILLISECONDS)
                                .tag("user", "josep")
                                .addField("VALID", VALID.get(i))
                                .build());

                    }


                } catch (MWException e) {
                    e.printStackTrace();
                    var = false;
                }
            }

            try {
                Thread.sleep(5_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                var = false;
            }
        }

        influxDB.close();

    }

}
