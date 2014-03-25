//
//	In the server case, the project is passed as a parameter to the ReactorPattern and 
//	Protocol. In this case, the classes and interfaces are generated directly to the project.
//
//	Code Snippets that are added to a program may require code "at the front" to initialize
//	some variables and some code "at the end" to cleanup. In this case, current line is set
//	"in the middle" to allow newly generated code be put in between initialization and cleanup 
//  code.
//

//*** SERVER ****************************************************************

// Create the Project
// ----------------------------------------------------------------
p = new JavaProject( 'org.periquet.net.testserver' )


// Doug Schmidt's Reactor Pattern
// ---------------------------------------------------------------
reactorPat = new ReactorPattern( p, 'server' ) // server sub-package

// create the reactor itself
reactorClass = reactorPat .createReactor( 'MyReactor' )

// handler classes are created to handle application functionality
loggerClass = reactorPat .createEventHandler( 'MyLogger' )

// acceptor classes are created as *factory objects* for application handlers
// when a connection comes in, a new application handler is created and registered with the reactor
accepterClass = reactorPat .createAccepter( 'MyLogAccepter' , 'MyLogger', 3000 )

// reactor class has a main() method where code snippets can be added
accepter = accepterClass .newInstance( 'logAccepter' ) // represents local instance
reactorBody = reactorClass.main.body
reactorBody.addLines( accepter.registerWithReactorCG( ) ) // accesses 'Reactor.getInstance()'


// Application Specific Functionality
// ------------------------------------------------------------------
prot = new Protocol( p, 'protocol' ) // protocol sub-package

// create serializable message class
logRecordClass = prot.createMessage( 'LogRecord' )
logRecordClass.addParameter( 'text', 'String' )

// generate code into log event handler method body
logRecord = logRecordClass .newInstance( 'logRecord' ) // represents local instance
handlerBody = loggerClass.handleEvent.body
handlerBody.addLines( logRecord.readMessageCG() ) // read from 'inputStream'
handlerBody.addLines( 'System.out.println( logRecord.text );' )


// // Enable viewing of a reactor class
// ------------------------------------------------------------------
jc  = reactorClass