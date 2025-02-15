#!/bin/bash

grep -R -o -E -h "[a-zA-Z.-]+@([a-zA-Z-]+.)+[a-zA-Z]+" /etc/* | sort -u | tr '\n' ',' > etc_emails.lst



