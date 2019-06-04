# medical-plan
big data and indexing can handle any json data schema

demo1
parse the usercase.txt to plan-schema
Have one post methods for all payloads. 

*The flow is like this:

<br>Read post request body
<br>Initialize a json parser
<br>Use the json parser to Parse the payload into a Java map
<br>Validate against json schemas
<br>Then store into key value store

APIs:
<br>POST plan/add
<br>GET plan/all
<br>GET plan/{id}  
<br>DELETE /plan/{id}

