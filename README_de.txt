FTS - Getting Started
---------------------

Derzeit ist das Projekt nur aus Eclipse heraus startbar.
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

Das Programm starten, wenn Eclipse fragt dann ist es
das Main.java

