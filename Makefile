FILE_PATH ?= "PN_Case_Study_-_Attachment.csv"
CHUNK_SIZE ?= 1000

build-api:
	cd api/ && mvn clean compile install -DskipTests

build-images: build-api
	docker-compose build

run: build-images
	docker-compose up --remove-orphans

upload: 
	javac Uploader.java && java Uploader $(FILE_PATH) $(CHUNK_SIZE)
