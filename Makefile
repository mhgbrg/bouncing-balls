default:
	javac -d out -classpath out/commons-math3-3.6.1.jar src/*.java
	cp MainApplet.html out/
	cp lib/* out/
	appletviewer out/MainApplet.html
