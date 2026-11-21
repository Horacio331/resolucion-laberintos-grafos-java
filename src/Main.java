import java.util.List;
import java.util.Scanner;

public class Main {
    private static Laberinto lab = null;
    private static Resultado rDfs = null;
    private static Resultado rBfs = null;
    private static Resultado rDij = null;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int op;
        do {
            mostrarMenu();
            op = leerOpcion(sc);
            switch (op) {
                case 1 -> cargar(sc);
                case 2 -> {
                    if (hayLaberinto()) {
                        rDfs = correrDFS();
                        animar(sc, rDfs);
                    }
                }
                case 3 -> {
                    if (hayLaberinto()) {
                        rBfs = correrBFS();
                        animar(sc, rBfs);
                    }
                }
                case 4 -> {
                    if (hayLaberinto()) {
                        rDij = correrDijkstra();
                        animar(sc, rDij);
                    }
                }
                case 5 -> mostrarTodas(sc);
                case 6 -> System.out.println("Hasta luego.");
                default -> System.out.println("Opcion no valida. Escribe un numero del 1 al 6.");
            }
        } while (op != 6);
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== LABERINTO =====");
        System.out.println("1. Cargar laberinto");
        System.out.println("2. Resolver laberinto con DFS");
        System.out.println("3. Resolver laberinto con BFS");
        System.out.println("4. Resolver laberinto con Dijkstra (camino mas corto)");
        System.out.println("5. Mostrar las tres soluciones");
        System.out.println("6. Salir");
        System.out.print("Opcion. ");
    }

    private static int leerOpcion(Scanner sc) {
        String t = sc.nextLine().trim();
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean hayLaberinto() {
        if (lab == null) {
            System.out.println("Primero carga un laberinto con la opcion 1.");
            return false;
        }
        return true;
    }

    private static void cargar(Scanner sc) {
        System.out.print("Ruta del archivo. ");
        String ruta = sc.nextLine();
        try {
            Laberinto nuevo = Laberinto.cargar(ruta);
            lab = nuevo;
            // Una carga valida descarta los resultados anteriores
            rDfs = null;
            rBfs = null;
            rDij = null;
            System.out.println("Laberinto cargado, " + lab.getFilas() + " renglones x "
                    + lab.getColumnas() + " columnas.");
        } catch (LaberintoException e) {
            System.out.println("No se pudo cargar. " + e.getMessage());
            if (lab != null) {
                System.out.println("Se conserva el laberinto anterior.");
            }
        }
    }

    private static void animar(Scanner sc, Resultado r) {
        Visualizador viz = new Visualizador(lab, sc);
        viz.setTiempoNanos(r.getNanos());
        viz.despliegaLaberinto(r.getVisita());
    }

    private static void mostrarTodas(Scanner sc) {
        if (!hayLaberinto()) return;
        // Solo se ejecutan los que faltan, los demas se reutilizan con su tiempo guardado
        if (rDfs == null) rDfs = correrDFS();
        if (rBfs == null) rBfs = correrBFS();
        if (rDij == null) rDij = correrDijkstra();

        Visualizador viz = new Visualizador(lab, sc);
        viz.muestraResultado(rDfs);
        viz.muestraResultado(rBfs);
        viz.muestraResultado(rDij);
    }

    // El tiempo cubre solo la busqueda y la reconstruccion del camino
    private static Resultado correrDFS() {
        Grafo g = lab.getGrafo();
        long t0 = System.nanoTime();
        List<Vertice> visita = g.DFS();
        long t = System.nanoTime() - t0;
        return new Resultado("DFS", visita, g, t);
    }

    private static Resultado correrBFS() {
        Grafo g = lab.getGrafo();
        long t0 = System.nanoTime();
        List<Vertice> visita = g.BFS();
        long t = System.nanoTime() - t0;
        return new Resultado("BFS", visita, g, t);
    }

    private static Resultado correrDijkstra() {
        Grafo g = lab.getGrafo();
        long t0 = System.nanoTime();
        List<Vertice> visita = g.caminoMasCorto();
        long t = System.nanoTime() - t0;
        return new Resultado("Dijkstra", visita, g, t);
    }
}