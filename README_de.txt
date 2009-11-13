FTS - Getting Started
=====================

FTS kann entweder direkt, oder aus Eclipse heraus gestartet
werden.

Zur Ausführung braucht man eine Java Runtime, die man zum Beispiel
hier bekommt: http://www.java.com/de/download

Im Prinzip sind alle weiteren Libraries im FTS Zip-Archiv
enthalten bzw im Subversion eingecheckt. Nicht enthalten ist GTK,
das für die Ausführung unter Linux benötigt wird. Es sollte aber
auf Deinem Linux System installiert sein.

ACHTUNG: 

Eine der verwendeten Bibliotheken (TopLink) hat Probleme wenn der
Pfad zum FTS Verzeichnis Leerzeichen enthält. Die müssen dann
leider entfernt werden.


FTS direkt starten
------------------

Direkt starten funktioniert aus dem Subversion heraus nur
wenn man zuerst mit Maven2 das entsprechende JAR baut.
Dazu aufrufen: mvn package

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
