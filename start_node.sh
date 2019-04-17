#!/bin/bash

function node_port() {
    echo '5001'
}

function super_node_port() {
    echo '5002'
}

function node_ip_1() {
    echo '172.18.0.24'
}

function node_ip_2() {
    echo '172.18.0.25'
}

function node_ip_3() {
    echo '172.18.0.26'
}

function super_node_ip_1() {
    echo '172.18.0.22'
}

function super_node_ip_2() {
    echo '172.18.0.23'
}

function node_name_1() {
    echo 'node-1'
}

function node_name_2() {
    echo 'node-2'
}

function node_name_3() {
    echo 'node-3'
}

node="$1"
case $node in
    1)
        ip=$(node_ip_1)
        name=$(node_name_1)
        super_node_ip=$(super_node_ip_1)
        ;;
    2)
        ip=$(node_ip_2)
        name=$(node_name_2)
        super_node_ip=$(super_node_ip_2)
        ;;
    3)
        ip=$(node_ip_3)
        name=$(node_name_3)
        super_node_ip=$(super_node_ip_2)
        ;;
    *)
        echo 'Usage: ./start_node.sh <1|2|3>'
        exit 1
        ;;
esac
docker run --net t1-distribuida --ip "$ip" --name "$name" --rm -it distribuida/t1 node "$ip" $(node_port) "$super_node_ip" $(super_node_port)
