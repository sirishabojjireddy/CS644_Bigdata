package com.spark.hackathon
import org.apache.spark.sql.SparkSession
case class Flight_Data1(Year: String, Month: String, DayofMonth: String, DayOfWeek: String, DepTime: String,
        CRSDepTime: String, ArrTime: String, CRSArrTime: String, UniqueCarrier: String, 
        FlightNum: String, TailNum: String, ActualElapsedTime: String, CRSElapsedTime: String, 
        AirTime: String, ArrDelay: String, DepDelay: String, Origin: String, 
        Dest: String, Distance: String, TaxiIn: String, TaxiOut: String, Cancelled: String, 
        CancellationCode: String, Diverted: String, CarrierDelay: String, 
        WeatherDelay: String, NASDelay: String, SecurityDelay: String, LateAircraftDelay: String)

        object exercise2 {
    def main(args: Array[String]) {


        val spark = SparkSession.builder().appName("Exercise2").master("local[*]").getOrCreate()

                spark.sparkContext.setLogLevel("ERROR")
                val Flight_Data_Load = spark.read.format("csv").option("delimiter", ",").option("header", "true").option("inferSchema", "true").load("file:///Users/SirishaBojjireddy/Document/courses/CS644_bigdata/finalproject/input/")
                import spark.implicits._

                val Flight_Data_DF = Flight_Data_Load.toDF("Year", "Month", "DayofMonth", "DayOfWeek", "DepTime", "CRSDepTime", "ArrTime", "CRSArrTime", "UniqueCarrier", "FlightNum", "TailNum", "ActualElapsedTime", "CRSElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Origin", "Dest", "Distance", "TaxiIn", "TaxiOut", "Cancelled", "CancellationCode", "Diverted", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay")
                	//*********Dataframe to DataSet Conversion*********************//
                	val Flight_Data_DS = Flight_Data_DF.as[Flight_Data1]
                        //        ********Creation of Table*******************************//
                        Flight_Data_DS.createOrReplaceTempView("Flight_Data")
                        val s = spark.sql("select * from Flight_Data").show(50, false)
                        //****************The 3 airlines with the highest and lowest probability respectively, for being onschedule*******************************//
                        val Top3_Airline_Probability = spark.sql("select FlightNum, (sum(ArrDelay)/Avg(ArrDelay))*min(ArrDelay), (sum(ArrDelay)/Avg(ArrDelay))*max(ArrDelay) from Flight_Data where ArrDelay <=10 group by FlightNum").show(3, false)
			//****************The 3 airports with the longest and shortest average taxi time per flight (both in and out) respectively, for being onschedule*******************************//
                        val Top3_Taxi_Time = spark.sql("select FlightNum, sum(case when TaxiIn='NA' then 1 else TaxiIn end), sum(case when TaxiOut='NA' then 1 else TaxiOut end) from Flight_Data  group by FlightNum, case when TaxiIn='NA' then 'No Data' else sum(TaxiIn) end having TaxiIn!=NA and TaxiOut!=NA and TaxiIn =(select min(TaxiIn) from Flight_Data) and TaxiOut =(select min(TaxiOut) from Flight_Data)").show(3, false)
                        //val Top3_Taxi_Time = spark.sql("select FlightNum, sum(case when TaxiIn='NA' then 1 else TaxiIn end) from Flight_Data  group by FlightNum having TaxiIn!=NA and TaxiOut!=NA and TaxiIn =(select min(TaxiIn) from Flight_Data)").show(3, false)

			//****************The most common reason for flight cancellations *******************************//
			val Common_Flight_Cancellation = spark.sql("select CancellationCode,count(CancellationCode) from Flight_Data where CancellationCode!='NA' group by CancellationCode").show(3,false)

    }

}