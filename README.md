# Shibi
Flexible Mobility service with gRPC and REST. Based on OEPTF (Open European Public Transit Format).

## General
Shibi is a Kotlin based service to query results from [public-transport-enabler](https://github.com/schildbach/public-transport-enabler)
and output them in the OEPTF format with gRPC and REST for transmitting data.

This service follows one pattern: Everything inside is Protobuf !

We use Protobuf for handling with the received data. 
This pattern enables high performance data handling and allows others to 
work with it!

## Run Shibi

Running Shibi as Service is easy:

`./gradlew run shibiRun`

Building a (tested) jar is nearly similar:

`./gradlew run shibiJar`

For your own Docker container, you can use `docker-compose up -d`.

**ATTENTION:** Do not use task `run` and task `jar` as they won't generate the required files.

## Ports

Shibi exposes two ports: `5080 (REST)` and `5111 (gRPC)`. To change them, set an env variable with `HTTP_PORT`
or respectively `GRPC_PORT` to a different port.

## Requests with Shibi

Shibi follows the [OEPTF Standard](https://github.com/ZweiDev/open-european-public-transit-format). 
Every path and every pattern should work with Shibi too !

## Responses with Shibi

The Shibi service opens two servers with different outputs:

- REST (JSON, `application/json`)
- gRPC (Protobuf, `application/x-protobuf`)

You can find the Proto files in `src/main/proto`.

## Licensing

The whole source code is licensed with GPL-3. One exception is for `src/main/proto`, as the files are BSD-3-Clause licensed.

For more information, look at the [licensing exceptions](./LICENSING_EXCEPTIONS).
