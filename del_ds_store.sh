#!/bin/bash

echo "Delete '.DS_Store' files"

find . -name ".DS_Store" -print -delete
