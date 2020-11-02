# Budgetbox

Requirements:

  - Java 8
  - Maven
  - a running MySQL server 8


### Clone the projet

```sh
git clone https://github.com/budgetbox-sebastien-cormier/genericfood.git
cd budgetbox
```


### Configure for your running MySQL server 8

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


### Load data into your MySQL (option 1 with MySQL Workbench)

Using MySQL Workbench:
  - Connect to your server
  - Open menu 'Server' / 'Data import'
  - Select the file in the subdirectory 'mysql' named 'budgetbox-structure-and-data.sql'
  - Import structure and data


### Load data into your MySQL (option 2 in command line)

```sh
mysql -h your_server -u root -p < mysql/budgetbox-structure-and-data.sql
```



### Start the Spring boot Application by running with the following command.

```sh
java -jar target/generic-food-0.0.1-SNAPSHOT.jar
```

### Query sample

```sh
GET http://localhost:8080/products/v1.0/?keywords=onion&includes=groups,subgroups
```

```sh
GET http://localhost:8080/products/v1.0/929
```


### Small frontend

You can also navigate through products using the very small frontend developed with Thymeleaf.

```sh
http://localhost:8080/
```

```sh
http://localhost:8080/product?id=1552
```


