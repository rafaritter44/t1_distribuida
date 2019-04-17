#!/bin/bash

function bake_image() {
    docker build -t distribuida/t1 .
}

function create_network() {
    docker network create --subnet=172.18.0.0/16 t1-distribuida
}

bake_image
create_network
