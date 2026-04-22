#!/bin/bash

# Убить старый процесс на порту 8080
kill -9 $(lsof -t -i:8080) 2>/dev/null

# === ВСТАВЬ СВОИ ТОКЕНЫ НИЖЕ ===
export TELEGRAM_BOT_TOKEN=8545778654:AAGY0DcVeHRlkchb10cNy1jqJAT5tvO9Ibg
export ADMIN_BOT_TOKEN=8544818775:AAFFUK1nZbJdklhD1ddCwHX_8bD1ZhgCSf8
# ================================

nohup java -jar target/tour-search-api-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

echo "Бот запущен. Проверь через 15 сек: tail -30 app.log"
