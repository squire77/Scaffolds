c = new CppClass( 'MyClass' )
c.addMethod( 'm1' )
c.addProtectedMethod( 'm2' )
c.addSlot( 's' )

c.baseClasses += 'QObject'
c.variables += [ 'name' : 'int' ]
c.frontMacros += 'Q_OBJECT'

c.headerSysIncludes += 'hbaction.h'
c.sourceProjIncludes += 'stdio.h'

p = new CppProject( 'MyProject' )
p.createMainFile( new CppMain(), 'main' )
p.createClassFiles( c, 'myclass' )

p.writeAll( 'c:/groovytest' )

