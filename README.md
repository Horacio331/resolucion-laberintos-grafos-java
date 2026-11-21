# Resolución de laberintos mediante grafos en Java

Proyecto final de Fundamentos 3, Universidad de Sonora.

El programa carga un laberinto desde un archivo de texto, lo representa como un grafo y lo resuelve con DFS, BFS y Dijkstra. Muestra la exploración paso a paso en consola y compara los resultados de los tres algoritmos.

## Autor

Horacio Duran Salido. El proyecto pedía equipos de dos personas, pero lo realicé de forma individual, así que todas las responsabilidades son mías, desde el diseño de las clases y los algoritmos hasta las pruebas y la documentación.

Fecha de entrega, 21/11/2026.

## Requisitos

- Java 21 o superior, JDK completo para poder compilar.
- Sin dependencias externas, solo la biblioteca estándar de Java.

## Compilar y ejecutar

Desde la carpeta raíz del proyecto.

```
javac -d bin src/*.java
java -cp bin Main
```

También se puede abrir la carpeta en VS Code con el Extension Pack for Java y ejecutar `Main.java`.

## Formato del archivo del laberinto

Cada línea del archivo es un renglón. El número de renglones y columnas puede variar y no tiene que ser cuadrado. Los caracteres válidos son los siguientes.

- `+` esquina o intersección de paredes.
- `-` pared horizontal.
- `|` pared vertical.
- Espacio, posición transitable.
- `#`, posición transitable marcada, que se trata igual que un espacio.
- `E` y `S`, entrada y salida explícitas.

Ejemplo.

```
+--+--+--+
|E       |
+--+  +  +
|     |  S
+--+--+--+
```

La entrada y la salida se definen de una de estas dos formas.

1. Marcas explícitas. Si aparece una `E` o una `S`, debe haber exactamente una de cada una.
2. Perímetro. Si no hay marcas, debe haber exactamente dos posiciones transitables en el borde. Se recorre de arriba abajo y de izquierda a derecha, la primera es la entrada y la segunda la salida.

El programa rechaza los archivos que no existen, que no se pueden leer, que están vacíos, que tienen renglones de distinta longitud, que contienen caracteres no válidos, incluidas las tabulaciones, o que no cumplen las reglas de entrada y salida, y explica el motivo. Los espacios iniciales y finales de cada línea se conservan. Un laberinto sin solución es válido, se explora y se avisa que no existe camino.

## Menú

1. Cargar laberinto. Pide la ruta del archivo. Si la carga falla se conserva el laberinto anterior, y si funciona se descartan los resultados anteriores.
2. Resolver con DFS, con animación paso a paso.
3. Resolver con BFS, con animación paso a paso.
4. Resolver con Dijkstra, el camino más corto, con animación paso a paso.
5. Mostrar las tres soluciones, sin animación, con el camino marcado, los vértices visitados, la longitud y el tiempo de cada una.
6. Salir.

Las opciones 2 a 5 necesitan un laberinto cargado. Si se escribe una opción no válida se avisa y se vuelve a mostrar el menú.

En la animación se puede avanzar con Enter en cada paso o escribir una pausa en milisegundos.

## Símbolos

- `+`, `-` y `|`, paredes.
- Espacio, posición libre.
- `.`, posición visitada.
- `@`, vértice actual.
- `*`, camino final.
- `E`, entrada.
- `S`, salida.

## Diseño

- `Vertice`, una posición transitable con identificador, fila y columna. El identificador es fila por número de columnas más columna.
- `Arcos`, una conexión con sus dos extremos y su costo, que siempre es 1.
- `Vertices`, administra los vértices con un `Hashtable<Integer, Vertice>`.
- `Grafo`, guarda las conexiones e implementa `DFS()`, `BFS()` y `caminoMasCorto()`.
- `Laberinto`, lee y valida el archivo, y construye el grafo.
- `Resultado`, guarda una copia de lo que devolvió cada algoritmo, incluido el tiempo.
- `Visualizador`, dibuja la animación y la comparación en consola.
- `Main`, el menú.

### Alternativa de adyacencia

Se eligieron listas ligadas de adyacencia. Cada vértice tiene una `LinkedList` de arcos y, como un vértice en un laberinto tiene como máximo cuatro vecinos, las listas ocupan mucho menos memoria que una matriz de adyacencia, que crecería con el cuadrado del número de vértices. El grafo es no dirigido, así que cada conexión se guarda en las listas de ambos vértices.

### Orden de exploración

DFS y BFS exploran los vecinos en este orden, arriba, derecha, abajo e izquierda. En DFS los vecinos se meten a la pila al revés para que el primero en salir sea el de arriba. El orden no depende de la Hashtable.

### Criterio de desempate en Dijkstra

La cola de prioridad elige primero el vértice con menor distancia y, si hay empate, el de menor identificador.

## Tiempos

Los tiempos se miden con `System.nanoTime()` y se muestran en nanosegundos y en milisegundos. Incluyen la búsqueda y la reconstrucción del camino, y excluyen la lectura del archivo, la construcción del grafo, la impresión, las pausas y la animación. Si se reutiliza un resultado, se muestra el tiempo guardado. Los tiempos varían entre corridas y las primeras ejecuciones suelen ser más lentas porque Java todavía está calentando.

## Pruebas

La carpeta `ejemplos` contiene laberintos de prueba válidos e inválidos, entre ellos rectangulares, con ciclos, sin solución, con marcas explícitas, con entrada y salida por el perímetro, con un renglón de solo espacios, y archivos inválidos como uno con dos entradas. En todos los laberintos con solución, BFS y Dijkstra obtienen la misma longitud mínima, mientras que DFS puede dar un camino más largo.

## Extras implementados

- Tiempos de solución con `System.nanoTime()`.

## Declaración de uso de IA

Se usó Claude, de la empresa Anthropic, modelo Claude Sonnet 5.5, en las siguientes tareas.

Con ayuda de la IA.

- Generación del código base de las clases, la lectura y validación del archivo, los tres algoritmos, el menú, la animación y los tiempos con System.nanoTime.
- Orientación sobre la organización del repositorio y el uso de Git y GitHub, y para resolver problemas durante el desarrollo.
- Sugerencias de laberintos de prueba.
- Borrador de este README.

Trabajo propio.

- Instalación y configuración del entorno, el repositorio y el .gitignore.
- Creación de los archivos de ejemplo, válidos e inválidos, a partir de los casos sugeridos.
- Ejecución de todas las pruebas, revisión de los resultados y corrección de errores o cambios necesarios.
- Commits, ramas, issues y pull requests en GitHub.
- Revisión de los cambios antes de integrarlos.