#!/bin/bash

function super_node_port() {
    echo '5002'
}

function multicast_port() {
    echo '5003'
}

function super_node_ip_1() {
    echo '172.18.0.22'
}

function super_node_ip_2() {
    echo '172.18.0.23'
}

function multicast_ip() {
    echo '224.0.0.0'
}

function super_node_name_1() {
    echo 'super-node-1'
}

function super_node_name_2() {
    echo 'super-node-2'
}

function run_super_node() {
    ip="$1"
    name="$2"
    docker run --net t1-distribuida --ip "$ip" --name "$name" --rm -d distribuida/t1 supernode "$ip" $(super_node_port) $(multicast_ip) $(multicast_port)
}

run_super_node $(super_node_ip_1) $(super_node_name_1)
run_super_node $(super_node_ip_2) $(super_node_name_2)
