import java.util.ArrayList;
import java.util.List;

// Guarda una copia del resultado de un algoritmo, porque el Grafo
// solo conserva la ultima ejecucion.
public class Resultado {
    private final String nombre;
    private final List<Vertice> visita;
    private final List<Vertice> camino;
    private final boolean haySolucion;

    public Resultado(String nombre, List<Vertice> visita, Grafo g) {
        this.nombre = nombre;
        this.visita = new ArrayList<>(visita);
        this.haySolucion = g.haySolucion();
        this.camino = new ArrayList<>(g.getCamino());
    }

    public String getNombre() { return nombre; }
    public List<Vertice> getVisita() { return visita; }
    public List<Vertice> getCamino() { return camino; }
    public boolean haySolucion() { return haySolucion; }

    // Movimientos = vertices del camino menos uno. -1 si no hay solucion.
    public int longitud() {
        return haySolucion ? camino.size() - 1 : -1;
    }
}