import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Almacen {

    public static void main(String[] args) throws InterruptedException {
        final int NUM_ESTACIONES = 3;
        final int PROCESADORES_POR_ESTACION = 2;
        final int CAPACIDAD_CINTA = 5;

        final int TOTAL_PAQUETES = 50;
        final double PROB_URGENTE = 0.20; // 20% urgentes, 80% normales

        final long MANTENIMIENTO_CADA_MS = 90_000; // 1.5 min
        final long DURACION_MANTENIMIENTO_MS = 30_000; // 30s

        System.out.println("Apertura del almacén\n");

        BlockingQueue<Paquete> colaGlobal = new PriorityBlockingQueue<>();
        List<Estacion> estaciones = new ArrayList<>();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
        AtomicInteger inTransit = new AtomicInteger(0);

        CountDownLatch procesados = new CountDownLatch(TOTAL_PAQUETES);

        // Crear estaciones (cada una con su cinta) y 2 procesadores internos
        for (int i = 1; i <= NUM_ESTACIONES; i++) {
            Transportador cinta = new Transportador(i, CAPACIDAD_CINTA); // 1 cinta por estación
            Estacion estacion = new Estacion(i, PROCESADORES_POR_ESTACION, cinta, colaGlobal, procesados);
            estaciones.add(estacion);
        }

        // Generación de paquetes (50, 20/80)
        Thread generador = new Thread(() -> {
            Random rnd = new Random();
            for (int id = 1; id <= TOTAL_PAQUETES; id++) {
                boolean urgente = rnd.nextDouble() < PROB_URGENTE;
                Paquete p = new Paquete(urgente, id);
                colaGlobal.offer(p);
                System.out.println("Paquete generado: " + p);
                try {
                    Thread.sleep(250); // llegada
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "generador");

        // Dispatcher
        Thread dispatcher = new Thread(new Dispatcher(colaGlobal, estaciones, scheduler, inTransit), "dispatcher");

        // Mantenimiento: cada 90s elige una estación, la para y deriva su cola local; a los 30s la reanuda
        scheduler.scheduleAtFixedRate(() -> {
            Random rnd = new Random();
            Estacion estacion = estaciones.get(rnd.nextInt(estaciones.size()));

            estacion.iniciarMantenimiento();

            List<Paquete> derivados = estacion.derivarColaLocal();
            for (Paquete p : derivados) {
                colaGlobal.offer(p);
            }
            if (!derivados.isEmpty()) {
                System.out.println("Derivados " + derivados.size() + " paquetes desde Estación " + estacion.getIdEstacion() + " a cola global.");
            }

            scheduler.schedule(estacion::finalizarMantenimiento, DURACION_MANTENIMIENTO_MS, TimeUnit.MILLISECONDS);
        }, MANTENIMIENTO_CADA_MS, MANTENIMIENTO_CADA_MS, TimeUnit.MILLISECONDS);

        generador.start();
        dispatcher.start();

        procesados.await(); // espera a que se procesen los 50

        dispatcher.interrupt();
        scheduler.shutdownNow();
        for (Estacion e : estaciones) e.shutdown();

        System.out.println("------------------------------");
        System.out.println("Almacén vacío. Fin de Programa");
    }
}
