#!/bin/bash

function remove_image() {
    docker rmi distribuida/t1
}

function remove_network() {
    docker network rm t1-distribuida
}

remove_image
remove_network
