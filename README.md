# loanapplicationsystem

Download & Start Single Node Elasticsearch With Docker

docker run --name elasticsearch6 -p 9200:9200 -p 9300:9300 \
	-e "discovery.type=single-node" \
	-e ES_JAVA_OPTS="-Xms2096m -Xmx2096m" \
	-e "bootstrap.memory_lock=true" --ulimit memlock=-1:-1 --ulimit nofile=65536:65536 \
	-e "network.host=0.0.0.0" \
	-e "script.allowed_types: inline" \
	-e "cluster.name=local-elasticsearch" \
	elasticsearch:6.4.2

#api 	
Run LoanApplicationSystem class for starting server.

#front-end
sudo apt install nodejs
sudo apt install npm
ng serve 
	
	

