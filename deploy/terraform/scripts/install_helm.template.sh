#!/bin/bash
#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

if [ ! -f .helm_completed ]; then
  sudo yum install -y helm

  echo "source <(helm completion bash)" >> ~/.bashrc
  echo "alias h='helm'" >> ~/.bashrc
  echo "helm completed"
  touch .helm_completed
fi