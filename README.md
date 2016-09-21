# dhis2-android-datacapture
Android application for DHIS 2 for capture and validation of routine data.

Get the APK from the release page:

https://github.com/dhis2/dhis2-android-datacapture/releases

#Project structure

A guide to the project structure

##Packages
####/io
| Packages 			| Description 			|
| ----------------- | --------------------- |
| **/io**  				| Handling data in terms of parsing and representation |
| /io/handlers 	| Contains a class that handles the import summary response from DHIS2 & a class that handles the user account info stored in a text file. (User credentials aren't stored in this file) |
| /io/holders 		| Model objects of info about the form and dataset |
| /io/json 			| Handles json manipulations |
| /io/models 		| Model ojects of data found in DHIS2 |

####/network

| Packages 			| Description 			|
| ----------------- | --------------------- |
| **/network**			| HTTP requests, network exceptions, response models, urls and network utilities |

####/processors

| Packages 			| Description 			|
| ----------------- | --------------------- |
| /proccessors		| CRUD operations on data |

####/ui
| Packages 			| Description 			|
| ----------------- | --------------------- |
| **/ui** 				| All things relating to the user interface. This includes activities, fragments, views, adapters for data, and models |
| /ui/activities	| All the apps activities	|
| /ui/adapters 		| Bridges between UI components and the data source. They deliver data to respective UI components |
| ui/adapters/dataEntry/rows | Contains adapters for each of the different datatypes in DHIS2 |
| /ui/fragments 	| Parts of an activities UI |
| /ui/models		| Representation of data. Contains on Picker class |
| /ui/views			| UI view components |

####/utils

| Packages 			| Description 			|
| ----------------- | --------------------- |
| /utils 			| Utilities. These are classes that are meant to provide services other classes. |
| /utils/date 		| Date utilities.		|
