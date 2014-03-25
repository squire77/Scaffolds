c = new CppClass( 'MyClass' )
m = c.AddMethod( 'm1' )
p = new CppProject( 'myproject' )
p.AddClass( c, 'myclass' )

p.writeAll( 'c:\\groovytest' )