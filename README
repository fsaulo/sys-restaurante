## Sys Restaurante

#### Compile & Build

###### 1. Linux (**BASH**)

- Make a text file for referencing all java classes
```$bash
$ find src/ -type f -name *.java > out/dev/class
```

- Compile using the reference file
```$bash
$ javac --module-path $PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.swing @out/dev/class -d out/dev/bin
```

- Copy all fxml files to output directory
```
$ find src/ -type f -name *.fxml -exec cp {} out/dev/bin/org/sysRestaurante/gui \;
```

- Apply styles
```
$ cp -r src/org/sysRestaurante/gui/css out/dev/bin/org/sysRestaurante/gui/
$ cp -r src/org/sysRestaurante/gui/icons out/dev/bin/org/sysRestaurante/gui/
```

- Run
```$bash
$ java -cp .:$SQLITE:out/dev/bin --module-path $PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.swing org.sysRestaurante.gui.MainGUI
```

------

###### 2. Windows (**PowerShell**)
