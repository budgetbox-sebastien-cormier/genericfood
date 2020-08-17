# budgetbox

Requirements:

  - Java 8
  - Maven
  - a running MySQL server


### Clone the projet

```sh
git clone https://github.com/xavierfacq/budgetbox.git
cd budgetbox
```


### Configure your MySQL

by editing the following file (Spring application.properties)

```sh
nano src/main/resources/application.properties

mysql.host = your_ip like 127.0.0.1
mysql.port = 3306
mysql.user = your_username
mysql.password = your_password or null
```


### How to build

```sh
mvn clean package
```

### This Spring boot Application can be run with the following command.

```sh
java -jar target/generic-food-0.0.1-SNAPSHOT.jar
```


### Load data into DB

As a multipart POST call, with an attached file named 'inputFile' 

```sh
POST http://localhost:8080/products/v1.0/init
```




Add interface



