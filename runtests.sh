#!/bin/sh

nohup Xvfb :99 2>&1 1>/dev/null  &
DISPLAY=:99 mvn integration-test
