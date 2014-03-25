
jc = new JavaClass( 'MyClass' )
jc.setMain( true )

m1 = jc.addMethod( 'm1' )
m2 = jc.addPackageMethod( 'm2' )
m2.isFinal = true
m2.isStatic = true
m3 = jc.addProtectedMethod( 'm3' )
m3.isStatic = true
m4 = jc.addPrivateMethod( 'm4' )
m4.isFinal = true

jc.superClass = 'MySuper'
jc.interfaces += 'IMyFace1'
jc.interfaces += 'IMyFace2'

var = jc.addVariable( 'name', 'int' )

jc.imports += 'java.io.*'
jc.imports += 'java.net.*'

jp = new JavaProject( 'mypackage.test' )
jp.createClassFile( jc )
jp.writeAll( 'c:/groovytest' )


 
 