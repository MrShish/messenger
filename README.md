Requirements

1. Docker
2. Java 8 JVN, http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
3. mvn 3, sorry I couldn't get this going smoothly with docker-compose :(
   (brew install mvn)

The idea is to use the scalability and fault tolarance of cassandra and make use of a eventlog that persist
the immutable state of the message during its lifetime.


Setup:

0. Build with mvn
`mvn clean install`

1. setup cassandra (this should boot upp everything, but im curently a docker noob):
`docker-compose up`

When its up:

Login to cqlsh by running this commmand

`docker-compose exec  cassandra-node1 cqlsh -u cassandra -p cassandra`


1c. Then run these:


`CREATE KEYSPACE IF NOT EXISTS messenger WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '1' };`

```
CREATE TABLE messenger.message_log (    
    uuid timeuuid,
    message_uuid timeuuid,
    channel text,        
    created timestamp,
    content text,
    operation text,
    PRIMARY KEY (channel, uuid)
) WITH CLUSTERING ORDER BY (uuid ASC);
```
```
CREATE TABLE messenger.message (            
    uuid timeuuid,
    log_uuid timeuuid,
    channel text,        
    created timestamp,
    content text,
    is_deleted boolean,
    PRIMARY KEY (channel, uuid)
) WITH CLUSTERING ORDER BY (uuid ASC);
```

done!
exit the shell ctrl-c


3. run the app
`java -jar target/messenger-jar-with-dependencies.jar`


4a. create some messages
post to /messages/:channel with a message param

`curl -X POST  "http://localhost:3030/messages/mrshish?message=hello!"`
`curl -X POST  "http://localhost:3030/messages/mrshish?message=bye!" `

4b. get the messages log (max 100 at the time)
`curl -X GET "http://localhost:3030/messages/mrshish"`

```
{
  "messageLog" : [ {
    "index" : "8cf0ee82-fa56-11e6-8768-d92f3d48d3f4",
    "messageUuid" : "8cf0c771-fa56-11e6-8768-2f1eb9365922",
    "channel" : "mrshish",
    "created" : "2017-02-24T06:00:32.011+0000",
    "message" : "hello!",
    "operation" : "CREATE"
  }, {
    "index" : "951a5154-fa56-11e6-8768-cd7ff848ed74",
    "messageUuid" : "951a5153-fa56-11e6-8768-05ead0c08a0f",
    "channel" : "mrshish",
    "created" : "2017-02-24T06:00:45.704+0000",
    "message" : "hello!",
    "operation" : "CREATE"
  } ],
  "lastLogIndex" : "951a5154-fa56-11e6-8768-cd7ff848ed74"
}
```

4c get message from index
`http://localhost:3030/messages/mrshish?lastLogIndex=2998ff2c-fa57-11e6-8768-0f36baf4bd45`

4d get message from index, with limit
`http://localhost:3030/messages/mrshish?lastLogIndex=2998ff2c-fa57-11e6-8768-0f36baf4bd45&limit=10`


4d delete one or more messages.

curl -X DELETE "http://localhost:3030/messages/mrshish?messageUuid=d281d89e-fa3e-11e6-a0b6-6937373f5faf&messageUuid=d39b37e0-fa3e-11e6-a0b6-974d8a5c51bb"


We never delete messages. We mark them deleted. 
The message log will display "create" event of the message, and the delete "event".





