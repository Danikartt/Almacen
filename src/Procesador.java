public class Procesador {
    private int numero;
    private int capacidad; // Número máximo de paquetes procesables a la vez

    public Procesador(int numero, int capacidad) {
        this.numero=numero;
        this.setCapacidad(capacidad);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }



    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public synchronized void procesar(Paquete paquete) throws InterruptedException {
        System.out.println("Procesador "+numero+" procesando " + paquete);
        Thread.sleep(1000); // Simula procesamiento
        System.out.println(paquete + " procesado y saliendo de estación");
    }
}
