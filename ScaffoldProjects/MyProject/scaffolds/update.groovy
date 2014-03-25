c = new CppClass( 'MyClass' )
c.addMethod( 'm1' )
c.addMethod( 'm2' )
c.addPrivateMethod( 'privMethod1' )
c.addProtectedMethod( 'protMethod1' )

p = new CppProject( 'newproj' )
p.createClassFiles( c, 'myclass' )

//f = p.sourceDir.findFile( 'myclass.cpp' )
//f.addMarker( 'new-public-methods' )
//p.writeAll( 'c:/groovytest' )

p.updateAll( 'c:/groovytest' )





