# medical-plan
big data and indexing can handle any json data schema

demo1
parse the usercase.txt to plan-schema
Have one post methods for all payloads. 

The flow is like this:

Read post request body
Initialize a json parser
Use the json parser to Parse the payload into a Java map
Validate against json schemas
Then store into key value store

APIs:
POST plan/add
GET plan/all
GET plan/{id}  
DELETE /plan/{id}

