Totally and Causally Ordered Group Messenger with a Local Persistent Key-Value Table

Update

You might run into a debugging problem if you're reinstalling your app from Eclipse. This is because your content provider will still retain previous values even after reinstalling. This won't be a problem if you uninstall explicitly before reinstalling; uninstalling will delete your content provider storage as well. In order to do this automatically from Eclipse, please refer to the following post on StackOverflow:
http://stackoverflow.com/a/11106324
Introduction

The teaching staff hopes you had fun working on PA1! If you got frustrated, we feel for you and believe us, we were there too. While it is expected to be frustrated in the beginning, we promise you, it will get better and you will enjoy more and more as you do it. You might even start enjoying reading the Android documentation because it *is* actually the single best place to get great information about Android. We do hope, though, that you now understand a bit more about what it means to write networked apps on Android.
Now back to the assignment: this assignment builds on the previous simple messenger and points to the next assignment. You will design a group messenger that preserves total ordering as well as causal ordering of all messages. In addition, you will implement a key-value table that each device uses to individually store all messages on its local storage, which should prep you for the next assignment. Once again, please follow everything exactly. Otherwise, it might result in getting no point for this assignment.
The rest of the description can be long. Please don’t “tl;dr”! Please read to the end first and get the overall picture. Then please revisit as you go!
Step 0: Importing the project template

Unlike the previous assignment, we will have strict requirements for the UI as well as a few other components. In order to provide you more help in meeting these requirements, we have a project template you can import to Eclipse.
Download the project template zip file to a directory.
Import it to your Eclipse workspace.
Open Eclipse.
Go to “File” -> “Import”
Select “General/Existing Projects into Workspace” (Caution: this is not “Android/Existing Android Code into Workspace”).
In the next screen (which should be “Import Projects”), do the following:
Choose “Select archive file:” and select the project template zip file that you downloaded.
Click “Finish.”
At this point, the project template should have been imported to your workspace.
You might get an error saying “Android requires compiler compliance level...” If so, right click on “GroupMessenger” from the Package Explorer, choose “Android Tools” -> “Fix Project Properties” which will fix the error.
Try running it on an AVD and verify that it’s working.
Use the project template for implementing all the components for this assignment.
Step 1: Writing a Content Provider

Your first task is to write a content provider. This provider should be used to store all messages, but the abstraction it provides should be a general key-value table. Before you start, please read the following to understand the basics of a content provider: http://developer.android.com/guide/topics/providers/content-providers.html
A typical content provider needs to support (basic) SQL statements. However, you do not need to do it for this course. You will use a content provider as a table storage that stores (key, value) pairs.
The following are the requirements for your provider:
You should not set any permission to access your provider. This is very important since if you set a permission to access your content provider, then our testing program cannot test your app. The current template takes care of this; so as long as you do not change the template, you will be fine.
Your provider’s URI should be “content://edu.buffalo.cse.cse486586.groupmessenger.provider”, which means that any app should be able to access your provider using that URI. To simplify your implementation, your provider does not need to match/support any other URI pattern. This is already declared in the project template’s AndroidManifest.xml.
Your provider should have two columns.
The first column should be named as “key” (an all lowercase string without the quotation marks). This column is used to store all keys.
The second column should be named as “value” (an all lowercase string without the quotation marks). This column is used to store all values.
All keys and values that your provider stores should use the string data type.
Your provider should only implement insert() and query(). All other operations are not necessary.
Since the column names are “key” and “value”, any app should be able to insert a <key, value> pair as in the following example:
ContentValues keyValueToInsert = new ContentValues();
// inserting <”key-to-insert”, “value-to-insert”>
keyValueToInsert.put(“key”, “key-to-insert”);
keyValueToInsert.put(“value”, “value-to-insert”);
Uri newUri = getContentResolver().insert(
    providerUri,    // assume we already created a Uri object with our provider URI
    keyValueToInsert
);
If there’s a new value inserted using an existing key, you need to keep only the most recent value. You should not preserve the history of values under the same key.
Similarly, any app should be able to read a <key, value> pair from your provider with query(). Since your provider is a simple <key, value> table, we are not going to follow the Android convention; your provider should be able to answer queries as in the following example:
Cursor resultCursor = getContentResolver().query(
    providerUri,    // assume we already created a Uri object with our provider URI
    null,                // no need to support the projection parameter
    “key-to-read”,    // we provide the key directly as the selection parameter
    null,                // no need to support the selectionArgs parameter
    null                 // no need to support the sortOrder parameter
);
Thus, your query() implementation should read the selection parameter and use it as the key to retrieve from your table. In turn, the Cursor returned by your query() implementation should include only one row with two columns using your provider’s column names, i.e., “key” and “value”. You probably want to use android.database.MatrixCursor instead of implementing your own Cursor.
Your provider should store the <key, value> pairs using one of the data storage options. The details of possible data storage options are in http://developer.android.com/guide/topics/data/data-storage.html. You can choose any option; however, the easiest way to do this is probably use the internal storage with the key as the file name and the value stored in the file.
After implementing your provider, you can verify whether or not you are meeting the requirements by clicking “PTest” provided in the template. You can take a look at OnPTestClickListener.java to see what tests it does.
If your provider does not pass PTest, there will be no point for this portion of the assignment.
Step 2: Implementing Total-Causal Ordering

The final step is supporting both total and causal ordering. You will need to design an algorithm that does this and implement it. The requirements are:
Your app should multicast every user-entered message to all app instances (including the one that is sending the message). In the rest of the description, “multicast” always means sending a message to all app instances.
Your app should use B-multicast. It should not implement R-multicast.
You need to come up with an algorithm that provides a total-causal ordering. After all, this is the main point of this assignment!
As with PA1, we have fixed the ports & sockets.
Your app should open one server socket that listens on 10000.
You need to use run_avd.py and set_redir.py to set up the testing environment.
The grading will use 5 AVDs. The redirection ports are 11108, 11112, 11116, 11120, and 11124.
You should just hard-code the above 5 ports and use them to set up connections.
Please use the code snippet provided in PA1 on how to determine your local AVD.
emulator-5554: “5554”
emulator-5556: “5556”
emulator-5558: “5558”
emulator-5560: “5560”
emulator-5562: “5562”
Every message should be stored in your provider individually by all app instances. Each message should be stored as a <key, value> pair. The key should be the final delivery sequence number for the message (as a string); the value should be the actual message (again, as a string). The delivery sequence number should start from 0 and increase by 1 for each message.
For your debugging purposes, you can display all the messages on the screen. However, there is no grading component for this.
Testing

We have testing programs to help you see how your code does with our grading criteria. If you find any rough edge with the testing programs, please report it on Piazza so the teaching staff can fix it. The instructions are the following:
Download a testing program for your platform. If your platform does not run it, please report it on Piazza.
Windows: We’ve tested it on 32- and 64-bit Windows 8.
Linux: We’ve tested it on 32- and 64-bit Ubuntu 12.04.
OS X: We’ve tested it on 32- and 64-bit OS X 10.9 Mavericks.
Before you run the program, please make sure that you are running five AVDs. python run_avd.py 5 will do it.
Please also make sure that you have installed your GroupMessenger on all the AVDs.
Run the testing program from the command line.
On your terminal, it will give you your partial and final score, and in some cases, problems that the testing program finds.
Submission

We use the CSE submit script. You need to use either “submit_cse486” or “submit_cse586”, depending on your registration status. If you haven’t used it, the instructions on how to use it is here: https://wiki.cse.buffalo.edu/services/content/submit-script
You need to submit one file described below. Once again, you must follow everything below exactly. Otherwise, you will get no point on this assignment.
Your entire Eclipse project source code tree zipped up in .zip: The name should be GroupMessenger.zip. Please do not change the name. To do this, please do the following
Open Eclipse.
Go to “File” -> “Export”.
Select “General -> Archive File”.
Select your project. Make sure that you include all the files and check “Save in zip format”.
Please do not use any other compression tool other than zip, i.e., no 7-Zip, no RAR, etc.
Deadline: 3/7/14 (Friday) 1:59:59pm

The deadline is firm; if your timestamp is 2pm, it is a late submission.
Grading

This assignment is 15% of your final grade. The breakdown for this assignment is:
5% if the content provider behaves correctly
5% if total ordering is preserved
(additional) 5% if total and causal ordering is preserved

