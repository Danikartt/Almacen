public class CarretillaAutonoma {
    private int idCarretilla;

    public CarretillaAutonoma(int idCarretilla) {
        super();
        this.idCarretilla = idCarretilla;
    }

    public int getIdCarretilla() {
        return idCarretilla;
    }

    public void setIdCarretilla(int idCarretilla) {
        this.idCarretilla = idCarretilla;
    }

    public synchronized void trasladarPaquete(Paquete paquete, String destino) throws InterruptedException {
        System.out.println("Carretilla "+ idCarretilla+ " trasladando " + paquete + " para " + destino);
        Thread.sleep(1000); // Simula tiempo de traslado
        System.out.println(paquete + " entregado en " + destino);
    }
}
