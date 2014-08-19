#!/bin/bash
while :
do
	echo "My PID  = $$"
	rm ~/Hackathon/eclipse_setup.tar.gz
	wget ftp://group1:Group1@192.168.201.10/group1_test/eclipse_setup.tar.gz
	echo "Completed Download: $i" 
	sleep 2
done

