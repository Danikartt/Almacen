public class Paquete implements Comparable<Paquete> {
    private final boolean urgente;
    private final int id;
    private final long createdAtNanos;

    public Paquete(boolean urgente, int id) {
        this.urgente = urgente;
        this.id = id;
        this.createdAtNanos = System.nanoTime();
    }

    public boolean isUrgente() {
        return urgente;
    }

    public int getId() {
        return id;
    }

    public long getCreatedAtNanos() {
        return createdAtNanos;
    }

    @Override
    public int compareTo(Paquete other) {
        int byUrgency = Boolean.compare(other.isUrgente(), this.urgente); // urgentes primero
        if (byUrgency != 0) return byUrgency;

        int byCreated = Long.compare(this.createdAtNanos, other.createdAtNanos); // FIFO dentro del tipo
        if (byCreated != 0) return byCreated;

        return Integer.compare(this.id, other.id); // desempate estable
    }

    @Override
    public String toString() {
        return "Paquete -> " + id + ", urgente=" + urgente;
    }
}
