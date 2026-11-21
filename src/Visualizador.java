import java.util.List;
import java.util.Scanner;

public class Visualizador {
    private static final String LEYENDA =
            "Leyenda. '+' '-' '|' paredes, ' ' libre, '.' visitado, '@' vertice actual, "
            + "'*' camino final, 'E' entrada, 'S' salida";

    private final Laberinto lab;
    private final Scanner sc;
    private long tiempoNanos = -1;

    public Visualizador(Laberinto lab, Scanner sc) {
        this.lab = lab;
        this.sc = sc;
    }

    // Tiempo guardado del algoritmo que se va a mostrar. -1 si no hay.
    public void setTiempoNanos(long nanos) {
        this.tiempoNanos = nanos;
    }

    // Opciones 2, 3 y 4. Se llama justo despues de correr el algoritmo,
    // asi que el camino y el indicador de solucion se consultan del grafo.
    public void despliegaLaberinto(List<Vertice> ordenVisita) {
        Grafo g = lab.getGrafo();
        int pausa = pedirPausa();
        int n = ordenVisita.size();

        for (int i = 0; i < n; i++) {
            limpiar();
            System.out.print(dibujar(ordenVisita, i + 1, ordenVisita.get(i), null));
            System.out.println(LEYENDA);
            System.out.println("Paso " + (i + 1) + " de " + n + ", vertice actual " + ordenVisita.get(i));
            esperar(pausa);
        }

        // Pantalla final, distingue el camino de las demas posiciones exploradas
        limpiar();
        List<Vertice> camino = g.haySolucion() ? g.getCamino() : null;
        System.out.print(dibujar(ordenVisita, n, null, camino));
        System.out.println(LEYENDA);
        System.out.println("Vertices visitados, " + n);
        if (g.haySolucion()) {
            System.out.println("Camino encontrado, " + g.longitudCamino() + " movimientos.");
        } else {
            System.out.println("No existe camino entre la entrada y la salida.");
        }
        if (tiempoNanos >= 0) {
            System.out.println("Tiempo de busqueda, " + Resultado.textoTiempo(tiempoNanos));
        }
        System.out.print("Enter para volver al menu. ");
        sc.nextLine();
    }

    // Opcion 5. Sin animacion, solo el camino marcado o el aviso.
    public void muestraResultado(Resultado r) {
        System.out.println("=== " + r.getNombre() + " ===");
        if (r.haySolucion()) {
            System.out.print(dibujar(r.getVisita(), 0, null, r.getCamino()));
            System.out.println(LEYENDA);
        } else {
            System.out.println("No existe camino entre la entrada y la salida.");
        }
        System.out.println("Vertices visitados, " + r.getVisita().size());
        if (r.haySolucion()) {
            System.out.println("Longitud del camino, " + r.longitud() + " movimientos.");
        }
        System.out.println("Tiempo de busqueda, " + Resultado.textoTiempo(r.getNanos()));
        System.out.println();
    }

    // Arma el dibujo. Orden de capas, paredes, visitados, camino, entrada y salida, actual.
    private String dibujar(List<Vertice> visita, int hasta, Vertice actual, List<Vertice> camino) {
        int f = lab.getFilas();
        int c = lab.getColumnas();
        char[][] m = new char[f][c];
        for (int i = 0; i < f; i++) {
            for (int j = 0; j < c; j++) {
                char ch = lab.getCelda(i, j);
                m[i][j] = ch == '#' ? ' ' : ch;
            }
        }
        for (int i = 0; i < hasta; i++) {
            Vertice v = visita.get(i);
            m[v.getFila()][v.getColumna()] = '.';
        }
        if (camino != null) {
            for (Vertice v : camino) {
                m[v.getFila()][v.getColumna()] = '*';
            }
        }
        Grafo g = lab.getGrafo();
        m[g.getEntrada().getFila()][g.getEntrada().getColumna()] = 'E';
        m[g.getSalida().getFila()][g.getSalida().getColumna()] = 'S';
        if (actual != null) {
            m[actual.getFila()][actual.getColumna()] = '@';
        }
        StringBuilder sb = new StringBuilder();
        for (char[] fila : m) {
            sb.append(new String(fila)).append('\n');
        }
        return sb.toString();
    }

    private int pedirPausa() {
        System.out.print("Pausa en milisegundos entre pasos, o Enter vacio para avanzar con Enter en cada paso. ");
        String t = sc.nextLine().trim();
        if (t.isEmpty()) return -1;
        try {
            return Math.max(0, Integer.parseInt(t));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void esperar(int pausa) {
        if (pausa < 0) {
            System.out.print("Enter para el siguiente paso. ");
            sc.nextLine();
        } else {
            try {
                Thread.sleep(pausa);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void limpiar() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}