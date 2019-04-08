# Brisbane bike stations clustering
Clustering bike stations coordinates  with Gaussian mixtures.

Since the data dimension is small, I thought this would be a better choice than the regular K-mean approach.

### Prerequisites

Obviously, have JVM, Scala, Spark and Maven installed.
Eventually eclipse for building or browsing code.

### Installing

Run 'mvn install' inside downloaded repo so as to make a uber jar.

## Running the tests
To process given json data sample
you can run this command (cf runSparkJob.sh):

spark-submit --class cluster.Brisbane --master local[*] --name "Brisbane bike station clustering" target/cluster-0.0.1-SNAPSHOT-uber.jar \
"Brisbane_CityBike.json" \
4 \
"clustered_ids" 

Which means:
run a spark job on local cluster ( --master local[*] ) so as to find 4 cluster indices for bike stations, and write results in clustered_ids folder.

So change --master option if you want to use YARN or Mesos cluster for example ..

The results will be displayed line by line this way:
"$station_id \t $cluster_index"



## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Scala] 2.11.11
* [Spark] 2.2.0
* Java SE 1.8


## Backlog

* The two ways coordinates are stored in json have been handled but i did not take care of partial
data such as station 7:

{
    "id": 7,
    "name": "7 - MARGARET STREET / EDWARD STREET",
    "address": "Margaret St / Edward St",
    "latitude": -27.47148,
    "longitude": "not relevant"
  }
 and 
 {
    "id": 7,
    "name": "7 - MARGARET STREET / EDWARD STREET",
    "address": "Margaret St / Edward St",
    "latitude": "not relevant",
    "longitude": 153.029647
  }
 
Of course, I could have merged those kind of data so as to have valid station, but i would have had to group by ids,
and the question is; in real case scenario, what shoud I do if we have more than two partial data ?
Take the first valud latitude and longitude? Average all the valid data ?
 
 So I decide to eliminate those datas ...
 
* In production, result shoud be stored according to the date and in different folder 
* Use Plotly's Scala graphing library so as to pretty print clusters ....
