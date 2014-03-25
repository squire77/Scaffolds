
/***
NOTE

- In the client case, the main client class is created directly. In this case, 
the class must be manually generated into the project.

- Looking at the client code, it seems like just as much code is written for
code generation as it takes to write the client code itself. However, the generated 
code may become increasingly complex without modifying the generation script. Also, 
minor modifications can be made to the script to generate C++ or Symbian code.
**/

//*** CLIENT *****************************************************************

// Create the Project
// ----------------------------------
jp = new JavaProject( 'org.periquet.net.testclient' )

// this protocol should really be a shared project with the server
prot = new Protocol( jp, 'protocol' )
logRecordClass = prot.createMessage( 'LogRecord' ) 
logRecordClass.addParameter( 'text', 'String' )


// Create a Test Client
//-----------------------------------
jc = new JavaClass( 'MyClient' )
jc.setMain( true )

pat= new ReactorPattern( jp, '' ) // no sub-package
connectorClass = pat.createConnector( 'MyConnector', '127.0.0.1', 3000 )
connector = connectorClass.newInstance( 'myconn' )
jc.main.body.addLines( connector .connectCG() )

// generate code to send a new LogRecord message
logRecord = logRecordClass.newInstance( 'logRecord' )
logRecord.setParameter( 'text', 'Hello world!' )
jc.main.body.addLines( logRecord.writeMessageCG() )


// Write the Project
// ----------------------------------
jp.createClassFile( jc ) // no sub-package
jp.writeAll( 'c:/groovytest' )