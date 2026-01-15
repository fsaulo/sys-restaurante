#!/bin/bash

# 1. Obtém o caminho absoluto de onde a pasta está agora
DIR_ATUAL=$(pwd)

# 2. Garante que o script de inicialização tenha permissão de execução
chmod +x iniciar-sistema.sh

# 3. Cria uma cópia do template substituindo a palavra-chave pelo caminho real
sed "s|CAMINHO_PROJETO|$DIR_ATUAL|g" SysRestaurante.desktop.template > SysRestaurante.desktop

# 4. Move o atalho para a área de trabalho do usuário atual
cp SysRestaurante.desktop ~/Área\ de\ Trabalho/ 2>/dev/null || cp SysRestaurante.desktop ~/Desktop/

# 5. Dá permissão de execução para o atalho na área de trabalho
chmod +x ~/Área\ de\ Trabalho/SysRestaurante.desktop 2>/dev/null || chmod +x ~/Desktop/SysRestaurante.desktop

echo "Sucesso! O atalho foi criado na sua Área de Trabalho."
