# Storage Problem

## Building the project

Type the following in a terminal

`make run`

This will start the api and the db containers.

## API

The Api needs to upload a file that can be big in size. For this, a simplified version
of the S3 Amazon Service https://docs.aws.amazon.com/AmazonS3/latest/dev/mpuoverview.html is implemented.
The file can be uploaded in multiple chunks.

The Api consists of three endpoints:

1. Upload file chunk:

   `PUT http:/localhost:8080/promotions/upload`

   The following is an example of two lines of the file sent over. Important to note the `\n` at the end of every line.

   `Body:`

   `{ "chunkNumber": 1, "content": "d018ef0b-dbd9-48f1-ac1a-eb4d90e57118,60.683466,2018-08-04 05:32:31 +0200 CEST\ne2649ca5-7e05-4d53-a8ff-919917a4922e,66.640497,2018-08-22 18:34:11 +0200 CEST\n" }`

2. Finish upload: this tells the api to gather all chunks and put them together, plus import the final file into the promotions table

   `POST http://localhost:8080/promotions/finish-upload`

3. Get Promotion:

   `GET http://localhost:8080/promotions/1`

## Upload file

There is a class called Uploader in the project that showcases how to upload a big file in multiple chunks.

To use this class, after having started the api, run the following in a different terminal window:

`make upload`

This by default will use the csv file at the root of this project and a boundary of a 1000 lines for each chunk.

This command also takes a FILE_PATH and CHUNK_SIZE arguments:

`make FILE_PATH=path_to_file CHUNK_SIZE=5000 upload`
