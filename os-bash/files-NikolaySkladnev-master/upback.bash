#!/bin/bash

RESTORE_DIR="$HOME/restore"

mkdir -p "$RESTORE_DIR" || { echo "Ошибка: не удалось создать каталог $RESTORE_DIR"; exit 1; }

latest_backup=$(find "$HOME" -maxdepth 1 -type d -name "Backup-*" | sort -r | head -n 1)

if [ -z "$latest_backup" ]; then
    echo "Ошибка: актуальный каталог резервного копирования не найден."
    exit 1
fi

for file in "$latest_backup"/*; do
    [ -f "$file" ] || continue 
    name=$(basename "$file")
    
    if [[ ! "$name" =~ [a-zA-Z0-9]+\.[0-9]{4}-[0-9]{2}-[0-9]{2}$ ]]; then
        cp "$file" "$RESTORE_DIR" || { echo "Ошибка: не удалось скопировать $name"; exit 1; }
    fi
done

echo "Восстановление завершено."
