import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Transportador {

    private int idTrans;
    private int capacidad;
    private final Semaphore semaforo;
    private final long transporteMs;

    public Transportador(int idTrans, int capacidad) {
        this(idTrans, capacidad, 700);
    }

    public Transportador(int idTrans, int capacidad, long transporteMs) {
        this.idTrans = idTrans;
        this.capacidad = capacidad;
        this.transporteMs = transporteMs;
        this.semaforo = new Semaphore(capacidad, true);
    }

    public int getIdTrans() {
        return idTrans;
    }

    public void setIdTrans(int idTrans) {
        this.idTrans = idTrans;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void moverPaqueteAsync(Paquete paquete, ScheduledExecutorService scheduler, Runnable onDelivered) throws InterruptedException {
        semaforo.acquire();
        System.out.println("Transportador " + idTrans + " moviendo " + paquete);

        scheduler.schedule(() -> {
            try {
                System.out.println("Transportador " + idTrans + " entregó " + paquete);
                onDelivered.run();
            } finally {
                semaforo.release();
            }
        }, transporteMs, TimeUnit.MILLISECONDS);
    }
}