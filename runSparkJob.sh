spark-submit --class cluster.Brisbane --master local[*] --name "Brisbane bike station clustering" target/cluster-0.0.1-SNAPSHOT-uber.jar \
"Brisbane_CityBike.json" \
3 \
"clustered_ids" 
