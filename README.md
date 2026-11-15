# Grupo1_Practica6
Este repositorio contiene los diagramas y los paquetes de clases del sistema "Food4All".

## Conexión con Neon

La aplicación usa PostgreSQL y toma la configuración desde `DATABASE_URL`, que
Neon entrega para el proyecto. También acepta `PGHOST`, `PGPORT`, `PGDATABASE`,
`PGUSER` y `PGPASSWORD` por separado. La aplicación no contiene credenciales.

1. Copia `.env.example` como `.env` y reemplaza sus valores con la cadena de
   conexión de Neon, o configura `DATABASE_URL` como variable de entorno de
   VS Code.
2. Desde la raíz del proyecto, ejecuta:

   ```text
   mvn compile
   java -cp "target/classes;$env:USERPROFILE\.m2\repository\org\postgresql\postgresql\42.7.7\postgresql-42.7.7.jar" pkDataAccess.pkDAO.SchemaInitializer
   ```

   El segundo comando crea las tablas de `storage/Food4All.sql` en Neon.
3. Inicia la aplicación desde VS Code. Los DAO usan automáticamente la misma
   configuración de Neon.

No subas `.env` al repositorio ni pegues la cadena de conexión en el código.
