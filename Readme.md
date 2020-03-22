TASKS DESCRIPTION
==============================================
Task 1. External sorting
------------------------
`Main objective`: sort file which is too large to fit into RAM. Definition of external sorting.
`Task`: Sort the file comparing it line by line with String Comparison.

Task 2. Create threadpool
-------------------------
`Main objective`: implement a custom thread pool without using java.util.concurrent framework.
Implement a service that accepts runnable with a delay (myThreadPool.submit(myRunnable, 10000)) after which this runnable should be executed. You can just use a fixed thread count or support a configurable one.

For all versions of the tasks:
------------------------------
1. Tests should be written.
2. There should be no thread leaks.
3. There should be no double processing and lost tasks.
4. Application should be able to work on -Xmx256m

IMPLEMENTATION DESCRIPTION
===============================================
There are 2 packages in the project: **task1** and **task2**.
Each task can be launched by running **Main** class in appropreate package:
_com.griddynamics.task1.Main_
_com.griddynamics.task2.Main_

Each main method contains two options to run application in two different ways.

Options for Task 1:
-------------------
-l which means to run app with split big file by lines number limit
-m which means to run app with split big file by available memory size limit

### Example of command for execution:
mvn exec:java -Dexec.mainClass="com.griddynamics.task1.Main" -Dexec.args="-l"

Options for Task 2:
-------------------
-a which means to run app with shutdown threadpool after tasks completion
-b which means to run app with shutdown threadpool before tasks completion

### Example of command for execution:
mvn exec:java -Dexec.mainClass="com.griddynamics.task2.Main" -Dexec.args="-a"
