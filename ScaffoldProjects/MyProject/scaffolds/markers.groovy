

p = new CppProject( 'myproj' )
f = p.sourceDir.addFile( 'test.cpp' )
f.addMarker( 'marker1' )
p.writeAll( 'C:/groovytest' )

Map<String, List<String>> markers = [:]
markers += [ 'marker1' : [ 'this is', 'a test', 'of writing lines' ] ]

f.update('C:/groovytest/myproj/src', markers)



 
 