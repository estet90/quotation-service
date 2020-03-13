##  QUOTATION SERVICE

### 1. Требования
Java 11+, Gradle 6.0+, Ubuntu

### 2. Сборка
`gradle build`

### 3. Запуск
#### 3.1 Запуск средствами gradle
Выполнить из корня приложения команду `gradle run`
Приложение будет запущено на порту 8000. Его можно поменять в файле `/quotation-service/src/main/resources/application.properties`
либо указать при запуске свой файл с настройками. Сделать это можно командой `gradle run --args='--config=путь_до_файла_с_настройками'`
#### 3.2 Запуск собранного дистрибутива
Для запуска необходимо, чтобы в системе была определена переменная `JAVA_HOME`.
Архивы с приложением после сборки будут находиться в папке `/quotation-service/build/distributions`.
После распаковки запустить приложение можно из папки `bin` командой `./quotation-service` либо `./quotation-service --config=путь_до_файла_с_настройками` 

### 4. API
`GET /quotation-service/api/v1/elsvs` - получение всех elvl  
`GET /quotation-service/api/v1/elsvs/{isin}` - получение elvl по isin  
`POST /quotation-service/api/v1/elsvs` - добавление/обновление elvl  
`GET /quotation-service/api/v1/swagger` - контракт в формате openapi