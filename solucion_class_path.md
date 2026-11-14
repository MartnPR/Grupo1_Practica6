# Solución del problema de classpath

## Problema

Maven busca las fuentes Java bajo `src/main/java`. La estructura correcta del
proyecto es:

```text
src/main/java/
├── pkBusinessLogic/
└── pkDataAccess/
```

Además, la declaración `package` de cada archivo debe coincidir con su ruta.
Si falta la declaración o usa otro nombre, VS Code no puede resolver
correctamente las clases y puede mostrar que solo se detectarán errores
sintácticos.

## Corrección aplicada

Se corrigieron las declaraciones de paquete de todas las fuentes:

```java
package pkBusinessLogic;
```

```java
package pkDataAccess;
```

Las interfaces ubicadas en la subcarpeta correspondiente usan:

```java
package pkDataAccess.pkIDataAccess;
```

También se agregaron los imports de las entidades `pkDataAccess` usados por
las interfaces. Un subpaquete no hereda automáticamente las clases de su
paquete padre.

Los imports de `CalcularEstadistica` y `GeneradorGrafico` ahora apuntan a
`pkDataAccess.Factura` y `pkDataAccess.Reporte`, en lugar de referenciar los
paquetes inexistentes `datos` y `entidades`.

## Pasos en VS Code

1. Abrir la carpeta `Grupo1_Practica6`, que contiene `pom.xml`.
2. Ejecutar **Java: Clean Java Language Server Workspace** desde la paleta de
   comandos.
3. Confirmar la limpieza y reiniciar VS Code.
4. En la vista Maven, ejecutar **Reload Project**.
5. Verificar que `src/main/java` aparece como raíz de fuentes.

No se debe agregar `pkBusinessLogic` o `pkDataAccess` como raíces
independientes. La raíz correcta es `src/main/java`.

## Validación

Desde la carpeta que contiene `pom.xml`, ejecutar:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
mvn clean compile
```

También se puede comprobar la raíz efectiva con:

```powershell
mvn help:evaluate "-Dexpression=project.build.sourceDirectory" -q "-DforceStdout"
```

El resultado debe terminar en `src\main\java`. Las comillas son necesarias en
PowerShell para que los argumentos `-D...` lleguen completos a Maven.

Si la compilación muestra errores de DAO o entidades faltantes, ya no se trata
de un problema de classpath: Maven está encontrando correctamente las fuentes
y esos tipos deben agregarse al paquete correspondiente o importarse desde una
clase existente.

## Regla para futuras clases

Una clase ubicada en:

```text
src/main/java/pkDataAccess/Factura.java
```

debe comenzar con:

```java
package pkDataAccess;
```

Una interfaz ubicada en:

```text
src/main/java/pkDataAccess/pkIDataAccess/IDataHelperSQL.java
```

debe comenzar con:

```java
package pkDataAccess.pkIDataAccess;
```

Además, cada clase o interfaz pública debe estar en un archivo cuyo nombre
coincida exactamente con el nombre del tipo público.
