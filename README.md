# 스프링부트 JPA 블로그 V3

### 1. 의존성
- devtools
- spring web (mvc)
- mustache
- jpa
- mariaDB
- lombok
- security
- validation

### 2. DB 설정
```sql
CREATE USER 'green'@'%' IDENTIFIED BY 'green1234';
CREATE DATABASE greendb;
GRANT ALL PRIVILEGES ON greendb.* TO 'green'@'%';
```