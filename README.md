RemoteCall IntelliJ Plugin
===================

IntelliJ IDEA plugin for remote call some procedures

Not Supported Anymore
=====================

This plugin is not supported anymore. For now the recommended way to open files by HTTP requests in using built-in [IntelliJ Rest API](http://develar.org/idea-rest-api/#api-Platform-file).


Supported procedures
--------------------------------

At the moment it can open files on http GET-request to localhost:8091 with "message" parameter looking like "FileName.java:89" or "any/path/FileName.java:89".
In this case plugin will try to find the most appropriate file basing on the specified path in opened projects and navigate to it. Also you can specify target column by following message: "FileName.java:89:21".

By default sending requests is permitted from localhost only. You can change it in Settings | Remote Call.
    
Listening port also can be configured in Settings.

Install
---------

You should install plugin using IntelliJ IDEA plugins manager.

Also you can download archive from [downloads page](https://github.com/zolotov/RemoteCall/archives/master) and extract it to IntelliJ plugins directory

Usage
---------

In my case I use this plugin for navigating by stacktrace which showing in application for watching logs. In the page of this application I insert following js code, that converts filenames (in div with stacktrace) to open-in-ide-link:

```javascript
function highlight(stackTraceDiv) {
//replace filenames to link
    var text = stackTraceDiv.html();
	var highlighted = text.replace(/[0-9a-z_A-Z\-\.\/]+:\d+/g, '<a class="ide-link" href="/?message=$&">$&</a>');
	stackTraceDiv.html(highlighted);

//bind links click event
	$('a.ide-link').click(function(e) {
		e.preventDefault();
		var url = $(this).attr("href");
		$.getJSON('http://localhost:8091' + url + '&callback=?', function(json) {
			//do nothing
		});

	});
}
```

[Vaughan Rouesnel](https://github.com/vjpr) has proposed following use cases:

- printing hyperlinks to source code in the WebKit Dev Console logger. 
- printing hyperlinks to source code in test reports or in browser-test-runners like Jasmine test runner.

Also, you can open files in IDE from your local applications using sockets, telnet, curl etc. Or you can simple open url http://localhost:8091?message=FileName.java:80 in browser ;-)
