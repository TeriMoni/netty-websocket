dbpath=/data/mongodb/db
logpath=/data/mongodb/log/mongodb.log
port=27017
fork=true
pidfilepath=/data/mongodb/master.pid  
directoryperdb=true  
logappend=true   
oplogSize=10000 



./mongod -f /opt/mongodb/conf/mongodb.conf --replSet mongoTest

cfg={ _id:"mongoTest", members:[ {_id:0,host:'192.10.15.24:27017',priority:2}, {_id:1,host:'192.10.15.23:27017',priority:1},   
{_id:2,host:'192.10.15.22:27017',arbiterOnly:true}] }; 


192.10.15.23 solr-cloud-master
192.10.15.24 solr-cloud-slave1
192.10.15.22 solr-cloud-slave2

scp -r /opt/solrCloud/zookeeper-3.4.12/  solr-cloud-slave1:/opt/solrCloud/
scp -r /data/zookeeper/  solr-cloud-slave1:/data/




./zkcli.sh -zkhost  192.10.15.22:2181,192.10.15.23:2181,192.10.15.24:2181 -cmd upconfig -confdir /opt/solrhome/appclient_core/ -confname appclient_core
./zkcli.sh -zkhost  192.10.15.21:2181,192.10.15.22:2181,192.10.15.23:2181,192.10.15.24:2181 -cmd upconfig -confdir /opt/solrhome/appdevice_core/ -confname appdevice_core
./zkcli.sh -zkhost  192.10.15.21:2181,192.10.15.22:2181,192.10.15.23:2181,192.10.15.24:2181 -cmd upconfig -confdir /opt/solrhome/appclient_program_core/ -confname appclient_program_core

./zkcli.sh -zkhost  192.10.15.22:2181,192.10.15.23:2181,192.10.15.24:2181 -cmd upconfig -confdir /opt/solrhome/applib_core/ -confname applib_core


http://192.10.15.22:8983/solr/admin/collections?action=CREATE&name=appclient_core&numShards=1&replicationFactor=2&collection.configName=appclient_core&collection=appclient

http://192.10.15.22:8983/solr/admin/collections?action=CREATE&name=appdevice_core&numShards=3&replicationFactor=3&maxShardsPerNode=3&collection.configName=appdevice_core&collection=appdevice

http://192.10.15.22:8983/solr/admin/collections?action=CREATE&name=appclient_program_core&numShards=3&replicationFactor=3&maxShardsPerNode=3&collection.configName=appclient_program_core&collection=appclient_program

http://192.10.15.22:8983/solr/admin/collections?action=CREATE&name=applib_core&numShards=3&replicationFactor=3&maxShardsPerNode=3&collection.configName=applib_core&collection=applib

insert