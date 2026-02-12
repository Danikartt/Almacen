Almacén – Simulación de procesamiento de paquetes con concurrencia en Java

Este proyecto implementa la simulación de un almacén automatizado que recibe, distribuye y procesa paquetes utilizando colas de prioridad, hilos, estaciones de trabajo, cintas transportadoras y mantenimiento programado.
El objetivo es modelar un flujo realista de logística interna con interrupciones, prioridades y múltiples procesadores trabajando en paralelo.

- ¿Qué hace este programa?
El sistema simula un almacén con:

50 paquetes generados (20% urgentes, 80% normales)

3 estaciones de trabajo, cada una con:

Una cinta transportadora con capacidad limitada

2 procesadores internos que consumen paquetes

Una cola global prioritaria donde llegan todos los paquetes

Un dispatcher que distribuye paquetes desde la cola global hacia las estaciones

Un sistema de mantenimiento automático que:

Cada 90 segundos detiene una estación aleatoria

Devuelve sus paquetes a la cola global

La reanuda tras 30 segundos

La simulación termina cuando los 50 paquetes han sido procesados.

- Tecnologías y conceptos utilizados
Java

Multithreading

PriorityBlockingQueue

ScheduledExecutorService

CountDownLatch

AtomicInteger

Simulación de procesos concurrentes

- Estructura del proyecto
(Basado en la clase principal que me pasaste; ajusta si tienes más clases)

Clase	Descripción
Almacen:	Clase principal. Configura el sistema, crea estaciones, inicia generador, dispatcher y mantenimiento.
Paquete:	Representa un paquete. Tiene prioridad (urgente o normal) e ID.
Estacion:	Cada estación tiene procesadores internos y una cinta transportadora. Procesa paquetes.
Transportador:	Cola interna con capacidad limitada para cada estación.
Dispatcher:	Toma paquetes de la cola global y los distribuye a estaciones disponibles.

- Cómo ejecutar la simulación
Clonar el repositorio:

bash
git clone https://github.com/Danikartt/TU_REPO.git
Abrir el proyecto en IntelliJ IDEA o cualquier IDE Java

Ejecutar la clase:

Código
Almacen.main()
Observar la salida por consola:

Generación de paquetes

Asignación a estaciones

Procesamiento

Mantenimientos automáticos

Finalización del sistema

- Parámetros configurables
Dentro de main() puedes modificar:

java
final int NUM_ESTACIONES = 3;
final int PROCESADORES_POR_ESTACION = 2;
final int CAPACIDAD_CINTA = 5;

final int TOTAL_PAQUETES = 50;
final double PROB_URGENTE = 0.20;

final long MANTENIMIENTO_CADA_MS = 90_000;
final long DURACION_MANTENIMIENTO_MS = 30_000;
Puedes ajustar:

Número de estaciones

Capacidad de cintas

Cantidad de paquetes

Probabilidad de urgentes

Frecuencia y duración del mantenimiento

- Objetivo del ejercicio
Este proyecto sirve como práctica para:

Programación concurrente en Java

Sincronización entre hilos

Gestión de colas de prioridad

Simulación de sistemas reales

Diseño de arquitecturas multicomponente

- Licencia
Uso libre para fines educativos.
