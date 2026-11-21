import java.util.ArrayList;
import java.util.List;

// Guarda una copia del resultado de un algoritmo, porque el Grafo
// solo conserva la ultima ejecucion.
public class Resultado {
    private final String nombre;
    private final List<Vertice> visita;
    private final List<Vertice> camino;
    private final boolean haySolucion;
    private final long nanos;

    public Resultado(String nombre, List<Vertice> visita, Grafo g, long nanos) {
        this.nombre = nombre;
        this.visita = new ArrayList<>(visita);
        this.haySolucion = g.haySolucion();
        this.camino = new ArrayList<>(g.getCamino());
        this.nanos = nanos;
    }

    public String getNombre() { return nombre; }
    public List<Vertice> getVisita() { return visita; }
    public List<Vertice> getCamino() { return camino; }
    public boolean haySolucion() { return haySolucion; }
    public long getNanos() { return nanos; }

    // Movimientos = vertices del camino menos uno. -1 si no hay solucion.
    public int longitud() {
        return haySolucion ? camino.size() - 1 : -1;
    }

    public static String textoTiempo(long nanos) {
        return String.format("%d ns (%.3f ms)", nanos, nanos / 1_000_000.0);
    }
}