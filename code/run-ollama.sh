#!/bin/bash

echo "Starting Ollama server..."
ollama serve &
ollama run llama3.2:3b


echo "Waiting for Ollama server to be active..."
while [ "$(ollama list | grep 'NAME')" == "" ]; do
  sleep 1
done
