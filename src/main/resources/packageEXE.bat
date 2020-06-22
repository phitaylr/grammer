cd ..\..\..\target
jpackage --name grammer --input ./ --main-jar .\grammer-1.0-SNAPSHOT.jar --main-class org.trojancs.grammer.GUIStarter --win-shortcut --win-menu --win-dir-chooser
WARNING: Using incubator modules: jdk.incubator.jpackage