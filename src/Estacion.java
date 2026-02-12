import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Estacion {
    private final int idEstacion;
    private final Transportador transportador;

    private final BlockingQueue<Paquete> colaLocal = new LinkedBlockingQueue<>();
    private final ExecutorService procesadores; // 2 procesadores internos
    private final AtomicInteger inFlight = new AtomicInteger(0);

    private final BlockingQueue<Paquete> colaGlobal; // para derivar en mantenimiento / carrera
    private final CountDownLatch latchProcesados;
    private volatile boolean enMantenimiento;

    public Estacion(int idEstacion,
                    int numProcesadores,
                    Transportador transportador,
                    BlockingQueue<Paquete> colaGlobal,
                    CountDownLatch latchProcesados) {
        this.idEstacion = idEstacion;
        this.transportador = transportador;
        this.colaGlobal = colaGlobal;
        this.latchProcesados = latchProcesados;
        this.enMantenimiento = false;

        this.procesadores = Executors.newFixedThreadPool(numProcesadores);
        startWorkers(numProcesadores);
    }

    public int getIdEstacion() {
        return idEstacion;
    }

    public Transportador getTransportador() {
        return transportador;
    }

    public boolean aceptaTrabajo() {
        return !enMantenimiento;
    }

    public int getLoad() {
        return inFlight.get() + colaLocal.size();
    }

    public void enqueue(Paquete paquete) {
        colaLocal.offer(paquete);
        System.out.println(paquete + " listo para ser procesado en Estación " + idEstacion);
    }

    public synchronized void iniciarMantenimiento() {
        enMantenimiento = true;
        System.out.println("\nEstación " + idEstacion + " iniciando mantenimiento (se para y deriva cola)...\n");
    }

    public synchronized void finalizarMantenimiento() {
        enMantenimiento = false;
        System.out.println("\nEstación " + idEstacion + " finalizó mantenimiento.\n");
    }

    public List<Paquete> derivarColaLocal() {
        List<Paquete> drained = new ArrayList<>();
        colaLocal.drainTo(drained);
        return drained;
    }

    public boolean tienePaquetesEnProceso() {
        return inFlight.get() > 0;
    }

    public void shutdown() {
        procesadores.shutdownNow();
    }

    private void startWorkers(int n) {
        for (int i = 1; i <= n; i++) {
            final int procId = i;
            procesadores.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Paquete paquete = colaLocal.take();

                        // Si entró mantenimiento entre medias, se devuelve a cola global para reasignación
                        if (enMantenimiento) {
                            colaGlobal.offer(paquete);
                            Thread.sleep(25);
                            continue;
                        }

                        inFlight.incrementAndGet();
                        try {
                            System.out.println("Estación " + idEstacion + " / Procesador " + procId + " procesando " + paquete);
                            Thread.sleep(1000); // procesamiento fijo
                            System.out.println(paquete + " completó procesamiento en Estación " + idEstacion);
                            latchProcesados.countDown();
                        } finally {
                            inFlight.decrementAndGet();
                        }
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
}