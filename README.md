# ESnack
Demo SpringBoot and MySql RESTFul Application
## Requirements
- git
- docker
- docker-compose
## Deployment
### Example 1
**docker-compose up**
### Example 2
**MYSQL_EXTERNAL_PORT="*3308*" BACKEND_EXTERNAL_PORT="*8082*" docker-compose up**

## Usage
### Base Address Example 1
[http://*localhost*:8081/eSnack]
### Base Address Example 2
[http://*localhost*:8082/eSnack]

## Stop and Clean
1. Ctrl+C
2. **docker-compose down**
3. **sudo docker volume prune**
4. **sudo docker system prune**