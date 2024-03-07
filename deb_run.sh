#!/bin/bash

echo config file: $1

#echo contents of volume/
cd volume
ls -l

cd ..

echo the user is: "$USER"

echo create ramdisk with $2 GB size
mkdir -p ./tmp
sudo chmod 777 ./

# Comment the following two lines to disable the ramdisk creation.
# This reduces the RAM usage but increases the analysis time.
sudo mount -v -t tmpfs -o size=$(($2))G ramdisk ./tmp
mount | tail -n 1

echo preparing analysis
ls -l

source env/bin/activate
python3 -W ignore driftool/main.py $1