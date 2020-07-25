build-api:
	cd api/ && mvn clean compile install -DskipTests

build-images: build-api
	docker-compose build

run: build-images
	docker-compose up --remove-orphans