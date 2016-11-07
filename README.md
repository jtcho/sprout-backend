Sprout Backend
==============

A lightweight framework for collaborative text-editing.

Built using Play Framework and Gradle.

## Getting Started

After cloning the repository, `cd` into the project directory.

_For the time being, this codebase relies on Amazon AWS DynamoDB. You can run the code on your local machine if you also have a version of DynamoDB hosted locally, as well. See the DynamoDB section for further instructions._

To launch the application, run `gradle run`. 

To launch the application with hotswapping, run `gradle -t run`.

To generate project metadata for IntelliJ Idea, run `gradle idea`.

To clean, run `gradle clean`.

## Debugging

If you are using an IDE like IntelliJ or Eclipse, you can attach a debugger to the local server and set up breakpoints.

To launch the application in debug mode, run `gradle run '-Pdebug'`. You should see the output `Listening for transport dt_socket at address: 5006`.

From your IDE, start your debug process.

Make any `curl` or other requests to interact with the server that will trigger your breakpoints.

### Attaching Debugger in IntelliJ

To attach the debugger, you'll need to set up a run/debug configuration.

In IntelliJ, create a new **Remote** configuration and ensure that the Host is set to `localhost` and the Port to `5006`. Also ensure that Transport is set to `Socket` and the Debugger mode is `Attach`.

Save, and start the debug configuration.

## Setting up PostgreSQL Locally

PostgreSQL may be installed using `brew`.
```
brew install postgresql
```

Afterwards, PostgreSQL may be started using the command:
```
pg_ctl -D /usr/local/var/postgres start
```
