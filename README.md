Recommendation-Service
======================

In this repository person to person recommender algorithms and services is implemented 

This is ongoing prototype implementation for Expert Recommendation Serivce.
Project is under continuous development and may not compile.

Latest Updates: Current version mines the data from stackexchange fitness data
to retreive experts from the data for the given query.

Usage: (Dependancy, y.jar is required for visualization, which can be added from BSCW of i5 chair)

1. Clone the project, run it in eclipse.
2. Create a jar of the project and run as below,
java -jar YOUR_JAR_FILE_NAME "YOUR_QUERY_HERE"

yFliles are used for visualization and in this current release output will be as shown below.
![Alt text](https://github.com/rwth-acis/Recommendation-Service/blob/master/src/res/network.jpg "Experts and their neighbors")
Red nodes indicates identified experts, Green nodes indicate people related to experts.
