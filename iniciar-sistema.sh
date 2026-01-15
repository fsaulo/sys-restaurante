#!/bin/bash
# Pega a pasta onde o próprio script está
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# Usa o Java do sistema com as libs que estão dentro do projeto
java --module-path "$DIR/lib/javafx/javafx-sdk-21.0.2/lib" \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/javafx-maven-setup-1.0-SNAPSHOT-jar-with-dependencies.jar
