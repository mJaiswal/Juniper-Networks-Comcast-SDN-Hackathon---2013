#!/bin/bash
for i in {1..20}
do
echo "My PID  = $$"
rm ~/group1_test/eclipse_setup.tar.gz
wget ftp://group1:Group1@172.16.0.101/group1_test/eclipse_setup.tar.gz
echo "Completed Download: $i" 
sleep 5
done
bye
