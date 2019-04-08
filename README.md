# BrisbaneBikeStationClustering
Clustering bike station coordinates  with Gaussian mixtures

After having made a uber jar with maven build,
you can run this command (cf runSparkJob.sh):

spark-submit --class cluster.Brisbane --master local[*] --name "Brisbane bike station clustering" target/cluster-0.0.1-SNAPSHOT-uber.jar \
"Brisbane_CityBike.json" \
3 \
"clustered_ids" 

Which means:
run a spark job on local cluster ( --master local[*]) so as to find 3 cluster indices for bike stations, and write results in clustered_ids folder.



