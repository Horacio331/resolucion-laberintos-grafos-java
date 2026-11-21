public class Arcos {
    private final Vertice origen;
    private final Vertice destino;
    private final int costo;

    public Arcos(Vertice origen, Vertice destino, int costo) {
        this.origen = origen;
        this.destino = destino;
        this.costo = costo;
    }

    public Vertice getOrigen() { return origen; }
    public Vertice getDestino() { return destino; }
    public int getCosto() { return costo; }

    // Dado un extremo, regresa el otro. Sirve porque el grafo es no dirigido.
    public Vertice getOtroExtremo(Vertice v) {
        return v.getId() == origen.getId() ? destino : origen;
    }
}