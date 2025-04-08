# atlas-32-server-java

## Start mosquitto broker

docker run -it --rm \
-p 1883:1883 \
eclipse-mosquitto \
mosquitto -c /mosquitto-no-auth.conf