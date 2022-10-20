project-hibernate-test
=========
### Description
- Сделать fork из репозитория https://github.com/vasylmalik/project-hibernate-1
- Скачать свою версию проекта к себе на компьютер.
- Добавить зависимости в pom.xml:
  - mysql: mysql-connector-java: 8.0.30
  - org.hibernate: hibernate-core-jakarta: 5.6.11.Final
- Сделать maven билд (mvn clean install). Для разнообразия используем Java версии 1.8.
- Добавить конфигурацию запуска через Идею.
- В Workbench выполнить скрипт создания схемы rpg:```CREATE SCHEMA `rpg` ;```
- Опционально. Если хочешь посмотреть какое ожидается поведение, можешь в классе com.game.service.PlayerService в параметре конструктора значение аннотации @Qualifier изменить с «db» на «memory». В этом случае Spring будет использовать в качестве реализации интерфейса IPlayerRepository класс PlayerRepositoryMemory. После теста не забудь изменить значение аннотации @Qualifier обратно на «db».
- Расставить все необходимые аннотации в ентити-классе com.game.entity.Player. Таблица должна называться «player», схема «rpg». Для енамов используй @Enumerated(EnumType.ORDINAL) кроме аннотации @Column. Напомню, что длина поля name должна быть до 12 символов, поля title – до 30 символов. Абсолютно все поля не должны быть null.
- В классе PlayerRepositoryDB добавь private final поле SessionFactory sessionFactory, в конструкторе класса инициализируй это поле. Проперти используй как в обычных задачах (работать будем с БД MySQL версии 8). Из интересного – добавь
```properties.put(Environment.HBM2DDL_AUTO, "update");```  
Это позволит не создавать таблицу вручную (или через выполнения sql скрипта).
- Реализуй все методы класса. Для разнообразия давай поступим так:
  - Метод getAll реализуй через NativeQuery
  - Метод getAllCount реализуй через NamedQuery
  - В методе beforeStop вызови у sessionFactory метод close. За счет наличия аннотации над методом @PreDestroy, Spring вызовет этот метод перед остановкой приложения, и это позволит валидно освободить все ресурсы системы.
  - Реализация остальных методов на твое усмотрение. Но не забывай о транзакциях и коммитах для методов, которые как либо изменяют содержимое БД.
- Запусти приложение. Если все сделал правильно – ты получишь рабочее приложение. Вот только данных там никаких нет, так что выполни через Workbench скрипт init.sql (из ресурсов), чтоб они появились. После этого в браузере нажми F5 и проверяй, что все методы ты реализовал правильно.
- Было бы интересно посмотреть, какие именно запросы выполняет Hibernate, поэтому добавим логирование запросов. Для этого добавь в pom.xml зависимость p6spy:p6spy:3.9.1. В папке ресурсов создай файл spy.properties, в котором укажи:
```
driverlist=com.mysql.cj.jdbc.Driver
dateformat=yyyy-MM-dd hh:mm:ss a
appender=com.p6spy.engine.spy.appender.StdoutLogger
logMessageFormat=com.p6spy.engine.spy.appender.MultiLineFormat
```
- И в конструкторе класса PlayerRepositoryDB измени две опции:
```
properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
```
- Теперь в выводе сервера по каждому запросу ты будешь видеть 2 строки. Первая – какой стейтмент подготовлен, второй – запрос с вставленными параметрами.