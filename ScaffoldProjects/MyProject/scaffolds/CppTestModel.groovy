c = new CppClass( 'MyClass' )
c.cppSysIncludes += 'stdio.h'
c.cppSysIncludes += 'string.h'
c.cppProjIncludes += 'mystuff.h'
c.cppProjIncludes += 'myother.h'
c.baseClasses += 'MyBase'
c.baseClasses += 'MyBase2'
c.variables += [ 'aNum' : 'TInt', 'aStr' : 'TDesC&']

m2 = c.AddMethod( 'myMethod2' )
m = c.AddMethod( 'myMethod' )
m.parameters += [ 'aNum' : 'TInt', 'aStr' : 'TDesC&']
m.body.statements += 'this is a test'
m.isStatic = true
m.isConst = true
m2.isConst = true

// test bi-directional paths
b = m.body
b.statements += 'this is another test'
b.declaration.clazz.name = 'DiffClass'  // test refactoring

