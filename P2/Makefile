#Makefile: Arianna Vacca

#Target
all: compile
	@echo -e 'Done!'

clean:
	@echo -e 'Cleaning up...'
	@rm -rf cs455/scaling/server/*.class
	@rm -rf cs455/scaling/client/*.class
	@echo -e 'Done!'

compile:
	@echo -e 'Compiling Server and Client code...'
	@javac -d . cs455/scaling/server/*.java
	@javac -d . cs455/scaling/client/*.java

