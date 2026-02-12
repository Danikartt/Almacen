import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Dispatcher implements Runnable {
    private final BlockingQueue<Paquete> colaGlobal;
    private final List<Estacion> estaciones;
    private final ScheduledExecutorService scheduler;
    private final AtomicInteger inTransit;

    public Dispatcher(BlockingQueue<Paquete> colaGlobal,
                      List<Estacion> estaciones,
                      ScheduledExecutorService scheduler,
                      AtomicInteger inTransit) {
        this.colaGlobal = colaGlobal;
        this.estaciones = estaciones;
        this.scheduler = scheduler;
        this.inTransit = inTransit;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Paquete paquete = colaGlobal.take();

                Estacion destino = estaciones.stream()
                        .filter(Estacion::aceptaTrabajo)
                        .min((a, b) -> Integer.compare(a.getLoad(), b.getLoad()))
                        .orElse(null);

                if (destino == null) {
                    colaGlobal.offer(paquete);
                    Thread.sleep(50);
                    continue;
                }

                inTransit.incrementAndGet();
                destino.getTransportador().moverPaqueteAsync(paquete, scheduler, () -> {
                    destino.enqueue(paquete);
                    inTransit.decrementAndGet();
                });
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}