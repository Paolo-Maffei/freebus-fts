Freebus Tools Software Coding Guidelines
========================================

Bitte die folgenden Guidelines möglichst einhalten.

Den Eclipse Stil FTS_Eclipse_Format.xml installieren und verwenden.
Wer das absolut nicht will soll bitte zumindest innerhalb von "fremden"
Klassen diesen Stil verwenden und keinen gemischt formattierten Code
erzeugen.

Kommentieren: jede Klasse, jede Methode. @inheritDoc bei abgeleiteten
Methoden ist ausreichend sofern nichts Spezielles zu sagen ist.

Einchecken: bitte in der Commit Message die getane Arbeit so beschreiben
dass andere Entwickler rein aus dieser Message verstehen können was
getan wurde. Zum Verstehen der Commit Message sollte es nicht notwendig
sein nachzusehen welche Dateien geändert wurden. 


Unterprojekte von FTS
---------------------

* freebus-fts-parent
  Das Parent-Projekt. Darf keinen Code enthalten.


* freebus-fts-common
  Gemeinsamer Code. Darf keine andere FTS Unterprojekte
  verwenden und darf keine Datenbank-Zugriffe enthalten.


* freebus-fts-knxcomm
  Bus-Kommunikation. Darf außer freebus-fts-common keine
  andere FTS Unterprojekte verwenden und darf keine
  Datenbank-Zugriffe enthalten.


* freebus-fts-persistence
  Datenbank-Zugriffe und Modell-Klassen von FTS.
  Darf außer freebus-fts-common keine andere FTS Unterprojekte
  verwenden.


* freebus-fts
  FTS GUI, quasi das Haupt Projekt. Darf alle oben genannten
  Unterprojekte einziehen.

  Darf keine direkten Datenbank-Zugriffe enthalten. Datenbank-Zugriffe
  sollen über freebus-fts-persistence erfolgen.

  Darf keine direkten Bus-Zugriffe enthalten. Bus-Zugriffe sollen
  über freebus-fts-knxcomm erfolgen.


* freebus-fts-tools
  Enthält Verschiedene Tools rund um FTS. Jedes Tool soll
  in einem eigenen Unterprojekt unter freebus-fts-tools
  angelegt werden.
