// create project
p = new CppProject( 'HelloService' )
main = new CppMain()
p.createMainFile( main, 'main' )

// add includes to main.cpp
main.projIncludes += 'hello_h.h'
main.sysIncludes += [ 'stdlib.h', 'stdio.h', 'string.h' ]

// generate project contents
p.writeAll( 'c:/groovytest' )