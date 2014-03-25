
main  = new CppMain()

p = new CppProject('myproject')
p.buildFile.name='Makefile'
cmain = p.createMainFile(main, 'main')
chead = p.createHeaderFile('MyClass.h')
csrc = p.createSourceFile('MyClass.cpp')

c1 = new CppClass('MyClass')
ctor = c1.addMethod('MyClass')
ctor.body.addLine('var1 = new MyClass();')
dtor = c1.addMethod('~MyClass')
dtor.body.addLine('delete var1;')
c1.addMethod('my-method1')
c1.ownedPointers += [ 'var1' : 'MyClass' ]

chead.classes += c1
csrc.classes += c1

