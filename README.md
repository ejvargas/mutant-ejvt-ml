# mutant-ejvt-ml

Este proyecto es la solución propuesta para el análisis de secuencias de ADN, en la cual se recibe como parámetro un array de Strings que representan cada fila de una tabla de (NxN) con la secuencia del ADN. Las letras de los Strings solo pueden ser: (A,T,C,G), las cuales representa cada base nitrogenada del ADN.


## Condiciones/Requerimientos
### Funcionales
1. Crear un programa con un método o función que reciba como parametro el Array de Strings y devuelva si un humano es mutante.
2. El array de strings representa una tabla de (**NxN**)
3. Las letras de los Strings solo pueden ser: **(A,T,C,G)**
4. Sabrás si un humano es mutante, Si encuentras **más de 1 secuencia de cuatro letras iguales**, de forma oblicua, horizontal o vertical

##### Ejemplo
* Para el array:

				String[] dna = {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};

* La función isMutant(dna) devuelve "**true**".
* Desarrolla el algoritmo de la manera más eficiente posible.

### Técnicos
Estos están más desarrollados en el archivo envido, en resumen son los siguientes:
* Un programa que que cumpla con el método pedido
* Crear una API REST con 2 recursos: `/mutant` y `/stats`
* Hostear API en un cloud computing libre
* Anexar una base de datos, la cual guarde los ADN’s verificados con la API
* Test-Automáticos, Code coverage > 80%.



## Solución
### Funcional
La solución al cálculo de las secuencias la abordé de 2 formas:

**Propuesta 1.** Recorrer la matriz en cada una de las posiciones y verificar el vector de cada una de sus direcciones y sentidos.
Implicaciones:
1. Habrían posiciones que no permiten el cálculo de todos sus vectores (zona naranja), por lo tanto dependiendo de la posición, algunos vectores no podrían calcularse. **Por ejemplo**: para la posición [1,1], solo se pueden calcular los vectores con sentido hacia el Este, Sur-Este y Sur.
2. Habría que implementar un mecanismo de duplicados, ya que trata cada posicion en la matriz de forma independiente.

**Propuesta 2.** Recorrer la matriz por filas (horizontalmente), por columnas (verticalmente), por sus oblicuas de pendiente positiva y sus oblicuas de pendiente negativa. De esta forma podría cubrir todos los sentidos que solicita el requerimiento

![Propuesta #1 vs Propuesta #2](./docs/images/Pro1vsPro2.png)


Dudas Generadas

>> Una cadena de 5+ se considera como 1 secuencia o como 2+?


**Elegí irme con la Propuesta #2**, cómo la implementé?:

1. Elaboré 4 funciones que recorren la matriz NxN, en cada una de las direcciones identificadas (horizontales, verticales, oblicuas con pendiente positiva y oblicuas con pendiente negativa). **Clase MutantAnalyzer.java**

2. Cada una de las funciones lo que hace es recorrer el array parámetro, toma la cadena de la posicion **fila** y la subcadena de la posicion **columna** y en su recorrido va guardando su anterior posición; cuando encuentra 4 de ellas  (`secuenciaDeIguales`), la acumula como una secuencia encontrada (`contadorSecuencia`). Cuando termina el recorrido, devuelve el conteo de secuencias encontradas. **Funciones recorrerHorizontal(), recorrerVertical(), recorrerOblicuasPositivas(), recorrerOblicuasNegativas()**

3. La **función isMutant** lo que hace es recibir el parámetro (Array) o tomarlo del constructor de la clase (para el caso en que sea invocado desde el API); hace cada uno de los recorridos, suma lo que devolvió cada uno de esos recorridos y basado en el número mínimo de secuencias para ser mutante (2) en la varibale (`numeroDeSecuenciasParaSerMutante`) responde falso o verdadero. Por último imprime por consola la matriz y el resumen de lo que encontró en cada recorrido. **Método mostrarResumen()**.

4. Antes de hacer los recorridos se valida que la matriz esté correctamente formada:
	1. No esté vacía o nula
	2. Filas (# de cadenas dentro del array) y columnas (# de caracteres dentro de cada cadena dentro del array) sean la misma cantidad.
	3. Se toma como referencia el tamaño de la primera cadena del array. Si es de 4 caracteres, se asume que la matriz es de 4x4. **Método paramValidations()**.


### Decisiones Técnicas
##### Nivel 1
* Una Clase hecha en lenguaje Java (8), construida en un proyecto Maven. **Clase MutantAnalyzer.java**
* Utilización de librería slf4j


##### Nivel 2
* La implementación de una clase de tipo Spring Boot Application. **Clase ApiService.java**
* Los métodos de esa clase exponen cada uno de los recursos REST que requerimos: 
	
+ Método mutant (*/mutant*) - Interfaz REST para extender la funcionalidad de la clase *MutantAnalyzer.java*. El parámetro es una estructura JSON con una pareja "adn" y como valor, el array que se le va a pasar a MutantAnalyzer. La respuesta es HTTP 200-OK en caso de ser mutante y 403-Forbidden en caso de no serlo. Además devuelve en el cuerpo de la respuesta REST, una estructura JSON con el siguiente formato
	

```
{
    "isMutant" = true,
    "horizontales" = 1,
    "verticales" = 1,
    "oblicuas_positivas" = 0,
    "oblicuas_negativas" = 1
}
```


+ Método statistics (*/stats*) - Interfaz REST para obtener la cantidad de analisis que se han hecho con resultados humanos, mutantes y su proporción. La respuesta es HTTP 200-OK con la siguiente estructura


```
{
    "count_mutant_dna": 8,
    "count_human_dna": 3,
    "ratio": 2.67
}
```


+ Método random (*/random*)* - Interfaz REST que genera una estructura JSON con secuencias aleatorias de ADN, para que puedan ser utilizadas en el recurso Mutant. El valor por defecto es 4, pero si se establece el parámetro "tamanho", se devolverá una matriz de ese tamanho


```
{
    "dna": [
        "GGAGGGCG",
        "GTGTCGAA",
        "GCGCTTCT",
        "CAAATATC",
        "GTTGCCTG",
        "ATAGGCGC",
        "AGCTGGCA",
        "AAGGAGTT"
    ]
}
```

* La clase **co.com.ejvt.ml.mutant.Utilidades.java** contiene un método utilitario con el objetivo de extraer el array de strings de la estructura JSON. Función **getJsonArray**.

* El Proyecto se encuentra alojado en **GitHub**

* Elegí **Heroku** como plataforma para ejecutar mi proyecto en producción, ya que me permite:
	* Provisionar fácilmente un contenedor 
	* Me entrega una base de datos PostgreSQL, lista para su uso
	* Vinculación del código fuente en **GitHub**, para despliegues automáticos

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

* Para los casos de prueba automáticos utilicé JUnit 5 y JaCoCo plugin. En la ruta "/src/test/java" se encuentra la clase "**co.com.ejvt.ml.mutant.ApiServiceTests.java**", al ejecutarla arroja cobertura de código del 86%

* Para la identificación de vulnerabilidades, usé **SonarQube 9.1.0**, pasando on calificación AAA y cobertura de código del 86%

![SonarQube Report](./docs/images/Sonar_last_test.png)

##### Nivel 3

* La base de datos utilizada es una BD ** PostgreSQL v13**. El script con la creación de la Base de datos se encuentra en la ruta **/scripts/DDL Mutant Database.sql**
![Model Entidad-Relación Mutant BD](./docs/images/ER_BD_Mutant.png)

* En la tabla **STATS** se encuentran los registros de las cadenas de ADN analizadas. Cada que se ejecuta un análisis, se ejecuta un **UPSERT** en la tabla, basado en el hash generado para una cadena única. Si la secuencia de ADN no se ha registrado, se inserta toda la información y el campo conteo se establece en 1. Si ya se encuentra registrada en la tabla (comparacion con el cambio **adnkey**), se actualiza el campo **conteo**

* La clase **co.com.ejvt.ml.mutant.Utilidades.java** contiene un método utilitario con el objetivo de calcular el Hash de una cadena en SHA256. Función **stringInSHA**

* La clase **co.com.ejvt.ml.mutant.BDAccess.java** contiene los métodos de Creacion/Actualización de Análisis hechos **guardarAnalisisADN** y la consulta de Estadisticas **getStatistics** utilitario con el objetivo de calcular el Hash de una cadena en SHA256. Función **stringInSHA**

###### Performance Fixes

* Establecí que se utilice el mismo pool de conexiones para varias llamados a BD y evitar abrir una conexion por cada invocación. Además, reduje el parámetro `setMaxLifetime` de la conexión a 1000ms, para asegurar que el pool no se rebose

* Inicialmente, para la extraccion de estadisticas, estaba consultando la BD 2 veces: una para el conteo de mutantes y otra para el de humanos. Lo unifiqué en un solo script que une ambas consultas. Métodos utilizados previamente: getSumMutants() y getSumHumans()









## Quieres desplegar el proyecto por en propia cuenta Heroku?
Debes contar con una cuenta de Heroku (Hobby)

#### Configurando
1. Haz clic aquí [![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

![Creación de App Heroku](./docs/images/install_heroku_1.png)

2. Escribe un nombre para tu app Heroku que esté disponible.
3. Dar clic en el botón **Deploy App**

![Fin Despliegue](./docs/images/install_heroku_2.png)

4. Dar clic en el botón **Manage App**, en la pestaña **Settings** y en el botón **Reveal Config Vars**

![Modificación de Variables](./docs/images/install_heroku_3.png)

5. Modificar la conexion por defecto a la BD PostgreSQL
	* Crea una nueva variable (KEY) llamada  **JDBC_DATABASE_URL**
	* Copia el VALOR de la variable **DATABASE_URL** y pégalo en la variable **JDBC_DATABASE_URL**. Debes modificar la estructura para cambiar el protocolo “postgres://” por “postgresql://”. Es decir, debe quedar con la siguiente estructura:

```	
	  postgresql://USUARIO:PASSWORD@HOST:5432/DB_NAME
```
![Modificación de Variables 2](./docs/images/install_heroku_4.png)

6. [Paso Opcional] Si quieres tener habilitado el módulo de estadísticas, deberás ejecutar el Script **scripts/DDL Mutant Database.sql** para crear la tabla en la base de datos que Heroku aprovisionó para ti, usando los datos de conexión de la variable **JDBC_DATABASE_URL**.

## Quieres Probar el API?
#### Probando con Postman
Necesitas descargar [**Postman**](https://www.postman.com/downloads/)

1. Descarga esta colección Postman del directorio [**postman/ejvt-ml-mutant.postman_collection.json**](https://github.com/ejvargas/mutant-ejvt-ml/tree/main/postman/ejvt-ml-mutant.postman_collection.json)
2. Impórtala en tu Postman
3. Modifica la colección con el dominio que desees utilizar. El tuyo, si hiciste los pasos de la sección **Quieres desplegar el proyecto por en propia cuenta Heroku?** o el mío que es el que viene por defecto (**ejvtmutant-pro.herokuapp.com**)

![Modificación Variable Dominio](./docs/images/install_postman_1.png)

4. Ejecuta el *Request* **ADN Mutant -prod** con un JSON válido. *Si quieres generar uno de prueba, mira el **paso 7***

![Ejecución Prueba Mutante](./docs/images/install_postman_2.png)

5. Ejecuta el *Request* **ADN Human -prod** con un JSON válido. *Si quieres generar uno de prueba, mira el **paso 7***

![Ejecución Prueba Mutante](./docs/images/install_postman_3.png)

6. Ejecuta el *Request* **Statistics -prod**

![Ejecución Prueba Mutante](./docs/images/install_postman_5.png)

7. Ejecuta el *Request* **Generate Random ADN -prod**. Puedes establecer el tamaño de la matriz cambiandole la cantidad al parámetro **"tamanho"**

![Ejecución Prueba Mutante](./docs/images/install_postman_5.png)



#### Probando con cUrl

1. Para probar el recurso REST **/mutant**, con una secuencia de ADN de Mutante, puedes utilizar este comando cUrl:

```
curl --location --request POST 'https://ejvtmutant-pro.herokuapp.com/mutant' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dna": [
        "ATGCGA",
        "CAGTGC",
        "TTATGT",
        "AGAAGG",
        "CCCCTA",
        "TCACTG"
    ]
}'
```

2. Para probar el recurso REST **/mutant**, con una secuencia de ADN de Mutante, puedes utilizar este comando cUrl:

```
curl --location --request POST 'https://ejvtmutant-pro.herokuapp.com/mutant' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dna": [
        "TTGCGA",
        "CAGTGC",
        "TTATGT",
        "AGAAGG",
        "CACCTA",
        "TCACTG"
    ]
}'
```

3. Para probar el recurso REST **/stats** puedes utilizar este comando cUrl:

```
curl --location --request GET 'https://ejvtmutant-pro.herokuapp.com/stats'
```

4. Para probar el recurso REST **/random** puedes utilizar este comando cUrl:

```
curl --location --request GET 'https://ejvtmutant-pro.herokuapp.com/random?tamanho=6'
```