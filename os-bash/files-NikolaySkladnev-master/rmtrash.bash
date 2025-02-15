#!/bin/bash

if [ -z "$1" ]; then
  echo "Ошибка: необходимо указать имя файла для удаления."
  exit 1
fi

filename=$1
trash_dir="$HOME/.trash"
log_file="$HOME/.trash.log"

if [ ! -f "$filename" ]; then
  echo "Ошибка: файл $filename не существует в текущем каталоге."
  exit 1
fi

if [ ! -d "$trash_dir" ]; then
  mkdir -p "$trash_dir" || { echo "Ошибка: не удалось создать каталог $trash_dir."; exit 1; }
fi

counter=1
while [ -e "$trash_dir/$counter" ]; do
  ((counter++))
done

link="$trash_dir/$counter"
ln "$filename" "$link" || { echo "Ошибка: не удалось создать жесткую ссылку для файла $filename."; exit 1; }
rm "$filename" || { echo "Ошибка: не удалось удалить файл $filename."; exit 1; }
echo "$(realpath "$filename"):$link" >> "$log_file" || { echo "Ошибка: не удалось записать в лог файл $log_file."; exit 1; }
echo "Файл $filename успешно перемещен в корзину."
