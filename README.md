RemoteCall IntelliJ Plugin
===================

IntelliJ IDEA plugin for remote call some procedures

Supported procedures
--------------------------------

At the moment it can open the files on http GET-request to localhost:8091 with "message" parameter looking like "FileName.java:89".
Send requests permitted only from localhost.

Install
---------

You should download archive from [downloads page](https://github.com/Zolotov/RemoteCall/archives/master) and extract it to IntelliJ plugins directory

Usage
---------

In my case I use this plugin for navigating by stacktrace which showing in application for watching logs. In the page of this application i insert following js code, that converts filenames (in div with stacktrace) to open-in-ide-link: [https://gist.github.com/905279]()

<script src="https://gist.github.com/905279.js?file=open_in_ide.js"></script>

Also, you can open files in IDE from your local applications using sockets, telnet, curl etc.

Build
---------
To build the plugin use IntelliJ IDEA.

You can use gradle for compile and testing. In this case you should create gradle.properties file from example and set path_to_idea property.

    $ echo "path_to_idea=/Applications/IntelliJ IDEA 10.app/" > gradle.properties
    $ gradle test

