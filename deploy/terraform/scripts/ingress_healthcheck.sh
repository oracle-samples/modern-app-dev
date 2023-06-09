#!/bin/bash
#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

set -e
while true 
do
    IP=`kubectl get ingress nginx-ingress --namespace=uho -ojsonpath='{.status.loadBalancer.ingress[0].ip}'`
    if [[ $IP =~ ^[0-9] ]]
    then
        echo $IP > ingress_ip
        exit 0
    fi
    echo "looping until ingress-lb is created"
    sleep 5
done
exit 0
