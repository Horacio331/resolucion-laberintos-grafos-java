import java.util.*;

public class Grafo {
    private final Vertices vertices = new Vertices();
    // Lista ligada de adyacencia por vertice. Se guarda el orden de insercion.
    private final Hashtable<Integer, LinkedList<Arcos>> adyacencia = new Hashtable<>();

    private Vertice entrada;
    private Vertice salida;

    // Resultado de la ultima ejecucion
    private List<Vertice> camino = new LinkedList<>();
    private boolean haySolucion = false;

    public void agregarVertice(Vertice v) {
        vertices.agregar(v);
        adyacencia.put(v.getId(), new LinkedList<>());
    }

    // Solo agrega el arco en la lista de "a". Para que sea no dirigido,
    // el cargador llama esto desde cada celda hacia sus vecinos.
    public void conectar(Vertice a, Vertice b, int costo) {
        adyacencia.get(a.getId()).add(new Arcos(a, b, costo));
    }

    public LinkedList<Arcos> vecinos(Vertice v) {
        return adyacencia.get(v.getId());
    }

    public void setEntrada(Vertice e) { this.entrada = e; }
    public void setSalida(Vertice s) { this.salida = s; }
    public Vertice getEntrada() { return entrada; }
    public Vertice getSalida() { return salida; }
    public Vertices getVertices() { return vertices; }
    public List<Vertice> getCamino() { return camino; }
    public boolean haySolucion() { return haySolucion; }

    // Reinicia el estado antes de cada algoritmo
    protected void reiniciarResultado() {
        camino = new LinkedList<>();
        haySolucion = false;
    }

    // Reconstruye el camino de salida a entrada con los predecesores
    protected void reconstruirCamino(Hashtable<Integer, Integer> pred) {
        LinkedList<Vertice> c = new LinkedList<>();
        Integer actual = salida.getId();
        while (actual != null) {
            c.addFirst(vertices.obtener(actual));
            actual = pred.get(actual);
        }
        camino = c;
        haySolucion = true;
    }

    // Longitud en movimientos = vertices del camino menos uno
    public int longitudCamino() {
        return haySolucion ? camino.size() - 1 : -1;
    }

    public List<Vertice> DFS() {
        reiniciarResultado();
        List<Vertice> orden = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        Hashtable<Integer, Integer> pred = new Hashtable<>();
        Deque<int[]> pila = new ArrayDeque<>();

        // Cada elemento de la pila es {vertice, quien lo metio}
        pila.push(new int[]{entrada.getId(), -1});

        while (!pila.isEmpty()) {
            int[] tope = pila.pop();
            int id = tope[0];
            if (visitados.contains(id)) continue;

            // Visitar es entrar al vertice
            visitados.add(id);
            if (tope[1] != -1) pred.put(id, tope[1]);
            orden.add(vertices.obtener(id));

            if (id == salida.getId()) {
                reconstruirCamino(pred);
                break;
            }

            // Se meten al reves para que salga primero el de arriba
            Iterator<Arcos> it = adyacencia.get(id).descendingIterator();
            while (it.hasNext()) {
                int vecino = it.next().getDestino().getId();
                if (!visitados.contains(vecino)) {
                    pila.push(new int[]{vecino, id});
                }
            }
        }
        return orden;
    }

    public List<Vertice> BFS() {
        reiniciarResultado();
        List<Vertice> orden = new ArrayList<>();
        Set<Integer> descubiertos = new HashSet<>();
        Hashtable<Integer, Integer> pred = new Hashtable<>();
        Queue<Integer> cola = new LinkedList<>();

        cola.add(entrada.getId());
        descubiertos.add(entrada.getId());

        while (!cola.isEmpty()) {
            int id = cola.poll();

            // Visitar es sacarlo de la cola
            orden.add(vertices.obtener(id));

            if (id == salida.getId()) {
                reconstruirCamino(pred);
                break;
            }

            for (Arcos a : adyacencia.get(id)) {
                int vecino = a.getDestino().getId();
                if (descubiertos.add(vecino)) {
                    pred.put(vecino, id);
                    cola.add(vecino);
                }
            }
        }
        return orden;
    }

    // Dijkstra. Desempate, menor distancia y despues menor id.
    public List<Vertice> caminoMasCorto() {
        reiniciarResultado();
        List<Vertice> orden = new ArrayList<>();
        Hashtable<Integer, Integer> dist = new Hashtable<>();
        Hashtable<Integer, Integer> pred = new Hashtable<>();
        Set<Integer> definitivos = new HashSet<>();

        // Cada elemento es {distancia, id}
        PriorityQueue<int[]> cola = new PriorityQueue<>((x, y) ->
                x[0] != y[0] ? Integer.compare(x[0], y[0]) : Integer.compare(x[1], y[1]));

        dist.put(entrada.getId(), 0);
        cola.add(new int[]{0, entrada.getId()});

        while (!cola.isEmpty()) {
            int id = cola.poll()[1];
            // Si ya quedo definitivo, esta entrada de la cola es vieja
            if (definitivos.contains(id)) continue;

            // Visitar es seleccionarlo definitivamente por su menor distancia
            definitivos.add(id);
            orden.add(vertices.obtener(id));

            if (id == salida.getId()) {
                reconstruirCamino(pred);
                break;
            }

            for (Arcos a : adyacencia.get(id)) {
                int vecino = a.getDestino().getId();
                if (definitivos.contains(vecino)) continue;
                int nueva = dist.get(id) + a.getCosto();
                Integer actual = dist.get(vecino);
                if (actual == null || nueva < actual) {
                    dist.put(vecino, nueva);
                    pred.put(vecino, id);
                    cola.add(new int[]{nueva, vecino});
                }
            }
        }
        return orden;
    }
}