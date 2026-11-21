import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Laberinto {
    private static final String VALIDOS = "+-| #ES";

    private final char[][] celdas;
    private final int filas;
    private final int columnas;
    private final Grafo grafo;

    private Laberinto(char[][] celdas, Grafo grafo) {
        this.celdas = celdas;
        this.filas = celdas.length;
        this.columnas = celdas[0].length;
        this.grafo = grafo;
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public char getCelda(int f, int c) { return celdas[f][c]; }
    public Grafo getGrafo() { return grafo; }

    public static boolean esTransitable(char ch) {
        return ch == ' ' || ch == '#' || ch == 'E' || ch == 'S';
    }

    // Si algo falla lanza LaberintoException con el motivo.
    // El menu debe atraparla y conservar el laberinto anterior.
    public static Laberinto cargar(String ruta) throws LaberintoException {
        List<String> lineas = leerLineas(ruta);
        char[][] celdas = validarForma(lineas);
        int[][] es = ubicarEntradaSalida(celdas);
        Grafo g = construirGrafo(celdas, es[0], es[1]);
        return new Laberinto(celdas, g);
    }

    private static List<String> leerLineas(String ruta) throws LaberintoException {
        Path p;
        try {
            // Quita comillas por si pegas la ruta con "Copiar como ruta"
            p = Paths.get(ruta.trim().replaceAll("^\"|\"$", ""));
        } catch (InvalidPathException e) {
            throw new LaberintoException("La ruta no es valida.");
        }
        if (!Files.exists(p)) {
            throw new LaberintoException("El archivo no existe.");
        }
        if (!Files.isRegularFile(p)) {
            throw new LaberintoException("La ruta no es un archivo.");
        }
        if (!Files.isReadable(p)) {
            throw new LaberintoException("No se puede leer el archivo.");
        }
        List<String> lineas;
        try {
            // readAllLines no recorta espacios, conserva los iniciales y finales
            lineas = Files.readAllLines(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new LaberintoException("Error al leer el archivo. " + e.getMessage());
        }
        if (lineas.isEmpty()) {
            throw new LaberintoException("El archivo esta vacio.");
        }
        return lineas;
    }

    private static char[][] validarForma(List<String> lineas) throws LaberintoException {
        int columnas = lineas.get(0).length();
        if (columnas == 0) {
            throw new LaberintoException("El renglon 1 esta vacio.");
        }
        char[][] celdas = new char[lineas.size()][columnas];
        for (int i = 0; i < lineas.size(); i++) {
            String l = lineas.get(i);
            if (l.length() != columnas) {
                throw new LaberintoException("El renglon " + (i + 1) + " mide " + l.length()
                        + " y deberia medir " + columnas + ".");
            }
            for (int j = 0; j < columnas; j++) {
                char ch = l.charAt(j);
                if (VALIDOS.indexOf(ch) < 0) {
                    String nombre = ch == '\t' ? "tabulador" : "'" + ch + "'";
                    throw new LaberintoException("Caracter no valido " + nombre
                            + " en renglon " + (i + 1) + ", columna " + (j + 1) + ".");
                }
                celdas[i][j] = ch;
            }
        }
        return celdas;
    }

    private static int[][] ubicarEntradaSalida(char[][] cel) throws LaberintoException {
        int filas = cel.length;
        int cols = cel[0].length;
        int numE = 0, numS = 0;
        int[] e = null, s = null;
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < cols; c++) {
                if (cel[r][c] == 'E') { numE++; e = new int[]{r, c}; }
                if (cel[r][c] == 'S') { numS++; s = new int[]{r, c}; }
            }
        }
        // Marcas explicitas
        if (numE > 0 || numS > 0) {
            if (numE != 1 || numS != 1) {
                throw new LaberintoException("Con marcas explicitas debe haber exactamente una E y una S. "
                        + "Hay " + numE + " E y " + numS + " S.");
            }
            return new int[][]{e, s};
        }
        // Perimetro. Recorrer por renglones visita cada esquina una sola vez
        List<int[]> borde = new ArrayList<>();
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < cols; c++) {
                boolean enBorde = r == 0 || r == filas - 1 || c == 0 || c == cols - 1;
                if (enBorde && esTransitable(cel[r][c])) {
                    borde.add(new int[]{r, c});
                }
            }
        }
        if (borde.size() != 2) {
            throw new LaberintoException("Sin marcas E y S, el borde debe tener exactamente dos "
                    + "posiciones transitables y tiene " + borde.size() + ".");
        }
        return new int[][]{borde.get(0), borde.get(1)};
    }

    private static Grafo construirGrafo(char[][] cel, int[] e, int[] s) {
        int filas = cel.length;
        int cols = cel[0].length;
        Grafo g = new Grafo();

        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < cols; c++) {
                if (esTransitable(cel[r][c])) {
                    g.agregarVertice(new Vertice(r * cols + c, r, c));
                }
            }
        }

        // Orden obligatorio, arriba, derecha, abajo, izquierda
        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < cols; c++) {
                if (!esTransitable(cel[r][c])) continue;
                Vertice v = g.getVertices().obtener(r * cols + c);
                for (int k = 0; k < 4; k++) {
                    int nr = r + dr[k];
                    int nc = c + dc[k];
                    if (nr >= 0 && nr < filas && nc >= 0 && nc < cols && esTransitable(cel[nr][nc])) {
                        g.conectar(v, g.getVertices().obtener(nr * cols + nc), 1);
                    }
                }
            }
        }

        g.setEntrada(g.getVertices().obtener(e[0] * cols + e[1]));
        g.setSalida(g.getVertices().obtener(s[0] * cols + s[1]));
        return g;
    }
}