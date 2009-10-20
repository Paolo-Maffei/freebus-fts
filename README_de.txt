FTS - Getting Started
=====================

FTS kann entweder direkt oder aus Eclipse heraus gestartet
werden.

Im Prinzip sind alle benötigten Libraries im FTS Zip-Archiv
enthalten. Nicht enthalten ist GTK, das für die Ausführung
unter Linux benötigt wird. Es sollte aber auf Deinem Linux
System installiert sein.


FTS direkt starten
------------------

Unter Windows eine Verknüpfung von fts.cmd erstellen.
Dort bei "Ausführen in Verzeichnis" das Verzeichnis eintragen
in dem FTS installiert ist. Über die Verknüpfung FTS starten. 

Unter Linux fts.sh einfach aufrufen.


FTS aus Eclipse starten
-----------------------

Also Eclipse starten, das Projekt in den Workspace importieren.

Dann muss noch die plattformabhängige Library für RXTX
eingestellt werden. dazu gehen wir so vor:

Auf dem Projekt im Eclipse rechtsklicken und Properties
auswählen (Alt+Enter). Dort "Java Build Path" / "Libraries"
Die Gruppe unter RXTXcomm.jar öffnen, dort auf
"Native library location" klicken. Rechts den "Edit..."
Button klicken.

In dem Dialog auf "Workspace..." klicken, und in
fts/contrib/rxtx das Verzeichnis aussuchen das Deiner
Architektur entspricht, also z.B. "win32".

Das wars, Ok/Ok/Fertig.

Das Programm starten, falls Eclipse fragt: es ist Main.java
