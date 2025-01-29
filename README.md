# Document Scan Demo Project
This demo project showcases communication between two SpringBoot applications using Kafka.

## Sender-App
Simple app for emitting Kafka messages on the `demo-document-checks-v1` topic.

## Scanner-App
Consumes Kafka messages from the `demo-document-checks-v1` topic and processes them.
Performs two styles checks:
- *File Pre Processing Filters* analysing the given file before parsing the file's content
- *Document Content Checks* parsing the file content

The result of each filter or check will be emitted as CheckResultEvent on the `demo-document-check-results-v1` topic

## How to build
Simple run `./mvnw clean package && docker-compose up --build`

## How to use 
Call the REST endpoint on the Sender-App:

`curl -X POST "http://localhost:8090/check/send?url=Testdaten_Rechnungseinreichung.pdf&fileType=application/pdf"`

## Disclaimer
This project is just for showcasing some SpringBoot mechanics. **It is by no means production ready code.** 