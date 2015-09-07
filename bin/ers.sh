#!/bin/bash
if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters, Provide dataset name and format of the file(XM/CSV)"
else
	id=$(curl -X POST http://localhost:8080/ers/datasets/$1/prepare)
	echo $id
	curl -X POST http://localhost:8080/ers/datasets/$id/parse?type=$2

	curl -X POST http://localhost:8080/ers/datasets/$id/indexer
fi
