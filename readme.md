This is the implementation of my graduation project which was published in the following publications:

* Reasoning about preferences in BDI agent systems. Simeon Visser, John Thangarajah, James Harland, AAMAS 2011: 1139-1140
* Reasoning about Preferences in Intelligent Agent Systems. Simeon Visser, John Thangarajah, James Harland, IJCAI 2011: 426-431

No efforts were made to turn this academic code into production code.

README
------

* Create a new GraduationProject in Eclipse.

* Configure the Build Path and add all the .jar files from the /jadex-0.96/lib/ directory.

* Add the .java files into the project which results in the following Java modules:

    - bienvenu (implements Bienvenu et al. formulas)
    - goalplanner (implements goal-plan tree logic)
    - holiday (implements the paper holiday agent example)
    - summary (implements the resource/effect summary logic)

* Create an agent .jar for the Jadex platform by doing:

    jar cvf agent.jar -C /home/simeon/workspace/GraduationProject/bin . holiday.agent.xml paper.agent.xml

* Run the Jadex platform using:

    java -classpath /home/simeon/jadex-0.96/lib/jadex_rt.jar:/home/simeon/jadex-0.96/lib/jadex_standalone.jar jadex.adapter.standalone.Platform

* See the below for instructions on how to run the agents.

* After a run you'll see the printed goal-plan tree in the console as well
as any properties, consumed resources and successful / unsuccessful plans.


Requirements
------------
- Jadex 0.96

Overview
--------

The code consists of two projects:

- SummaryAgent096: The agent in Jadex 0.96 that utilizes the preferences
- GoalPlanner: The code that constructs and annotates the goal-plan tree

Running
-------

The files jadex_rt.jar and jadex_standalone.jar need to be included in the
classpath of Java. The command to run should look something like:

    java -classpath H:\jadex-0.96\lib\jadex_rt.jar;H:\jadex-0.96\lib\jadex_standalone.jar jadex.adapter.standalone.Platform

In the top-left part of the Jadex window, right-click and select 'Add path'.
Navigate to the agent.jar file and add that file to the Jadex platform. It is now
possible to create agents that are specified in .agent.xml files. Double-click on
a .agent.xml filename and an agent of that type will be created, see the bottom-left
part of the Jadex window.

For practical reasons, we have opted to only start executing the agent after a
message has been sent to it. This allows us to launch debuggers and other monitoring
tools before the agent actually starts executing. This can be done using one of
the buttons in the top-right.

To actually start an agent, double-click on the agent itself to launch it. You
can then click the envelop (Conversation Center) at the top-right and then
double-click on the agent's name in the list on the left. This puts the agent
in the list of Receivers on the right. Now, click 'Send' to send any message
and after this, the agent will start executing.

The command window now shows:

- the preference formulas,
- the annotated goal-plan tree,
- the plans that have been executed,
- the received values of properties,
- the total resource usage,
- the properties of the root node.

Preference specification
------------------------

Preferences can be specified as Java code in MasterPlan.java
by instantiating objects to construct the desired preference formulas.

Each agent.xml file has a PingPlan which is the plan that triggers the
agent to start executing after a message has been sent to it. You can set this
to PingPlanOne, PingPlanTwo or PingPlanThree to let the agent use a different
set of preferences.

Some work has been done to move the preference specification to the agent.xml
itself in <parameterset name="preferences"></parameterset> and with a language
grammar specification in Prefs.g using ANTLR 3.2 but that is not in use.

Modifying the code
------------------

If you modify the code, the Java code needs to be compiled to .class and
a .jar file needs to be constructed. For this, you can use to following command
and adapt the paths where needed:

    jar cvf agent.jar -C H:\workspace\SummaryAgent096\bin\ . -C H:\workspace\GoalPlanner\bin\ .

This .jar file should include the code of both the Jadex 0.96 agent and the
code that constructs and annotates the goal-plan tree. Furthermore, it should include
the .agent.xml files. Please review the structure of the provided agent.jar file to
see how the files should be organized.
