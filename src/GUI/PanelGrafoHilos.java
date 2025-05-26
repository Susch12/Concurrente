package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PanelGrafoHilos extends JPanel {
    private List<String> procesos = new ArrayList<>();
    private List<String> recursos = new ArrayList<>();
    private String colorFlechas = "blanco"; 
    private int procesoActual = -1;
    private boolean productorActivo = false;
    private boolean consumidorActivo = false;
    private String estadoProceso = "";
    private boolean[] filosofosActivos = new boolean[5];
    private boolean[] tenedoresEnUso = new boolean[5];
    private String[] estadosFilosofos = new String[5];
    private Color[] colorFilosofos = new Color[5];
    private Color[] colorFlechasFilosofos = new Color[5]; 
    private String[] estados = new String[5];
    private Point[] puntosFlechasIzq = new Point[5];      // Puntos iniciales flechas izquierdas
    private Point[] puntosFlechasDer = new Point[5];      // Puntos iniciales flechas derechas
    private int sillasOcupadas = 0;
    private volatile boolean dibujando = false; 
    
    

    private String estadoBuffer = "";
    private ArrayList<Integer> clientesEsperandoIds;
    // Métodos para actualizar estados
public void setProcesoActivo(int proceso) {
    this.procesoActual = proceso;
}
public void setNombresProcesos(List<String> nombres) {
    procesos.clear();
    for (String nombre : nombres) {
        procesos.add(nombre);
    }
    repaint();
}

public void setEstadoBuffer(String estado) {
    this.estadoBuffer = estado;
    repaint();
}

   public void setProcesoActivo(int proceso, boolean activo) {
    if (proceso == 0) {
        this.productorActivo = activo;
    } else if (proceso == 1) {
        this.consumidorActivo = activo;
    }
    repaint();
}

    public void setEstadoProceso(String estado) {
    this.estadoProceso = estado;
    repaint();
    }

    PanelGrafoHilos() {
        setBackground(Color.GRAY);
    }

    public void agregarProcesosYRecursos(int nuevosProcesos, int nuevosRecursos) {
        int numActualProcesos = procesos.size();
        int numActualRecursos = recursos.size();

        for (int i = 1; i <= nuevosProcesos; i++) {
            procesos.add("p" + (numActualProcesos + i));
        }

        for (int i = 1; i <= nuevosRecursos; i++) {
            recursos.add("r" + (numActualRecursos + i));
        }
     }
    
    
    public void cargarFlechas(String flechasTexto) {
         String[] flechasArray = flechasTexto.split("\n");
    for (String flecha : flechasArray) {
        // Agregar cada flecha a tu lógica de dibujo, p.ej., creando objetos o líneas
    }
    repaint();  
}


    public boolean hayProcesosYRecursos() {
        return !procesos.isEmpty() && !recursos.isEmpty();
    }

    public int getNumeroProcesos() {
        return procesos.size();
    }

    public int getNumeroRecursos() {
        return recursos.size(); 
    }

    public String obtenerFlechasComoTexto() {
        StringBuilder flechasTexto = new StringBuilder();
        
        int numProcesos = procesos.size();
        int numRecursos = recursos.size();
        
        for (int i = 0; i < numProcesos; i++) {
            int recursoIndex = i % numRecursos;                  
                    
            flechasTexto.append(procesos.get(i))
                        .append(" -> ")
                        .append(recursos.get(recursoIndex))
                        .append("\n");
        }
        
        return flechasTexto.toString(); // Devuelve las flechas como texto
    }
    

    public void setColorFlechas(String color) {
        this.colorFlechas = color;
        repaint();
    }

    public void asignarRecursoAProceso(int indexProceso) {
        this.procesoActual = indexProceso; 
        repaint();
    }

    public void liberarRecurso(int indexProceso) {
        if (indexProceso == procesoActual) {
            this.procesoActual = -1; 
        }
        repaint();
    }
    
    public void setFilosofoActivo(int id, boolean activo) {
        if (id >= 1 && id <= 5) {
            filosofosActivos[id-1] = activo;
            repaint();
        }
    } 

public void setTenedorEnUso(int id, boolean enUso) {
        if (id >= 0 && id < 5) {
            tenedoresEnUso[id] = enUso;
            repaint();
        }
    }
    

public void setEstadoFilosofo(int id, String estado) {
    if (id >= 0 && id < 5) {
        estadosFilosofos[id] = estado;
    }
}

public void setEstadoFilosofo(int id, String estado, Color color) {
        if (id >= 1 && id <= 5) {
            colorFilosofos[id-1] = color;
            repaint();
        }
    }

public void asignarTenedoresFilosofo(int idFilosofo, boolean usando) {
    if (idFilosofo >= 1 && idFilosofo <= 5) {
        // Tenedor izquierdo: (id-1) % 5
        // Tenedor derecho: id % 5
        int tenedorIzq = (idFilosofo - 1) % 5;
        int tenedorDer = idFilosofo % 5;
        
        setTenedorEnUso(tenedorIzq, usando);
        setTenedorEnUso(tenedorDer, usando);
        
        // Actualizar color de flechas
        colorFlechasFilosofos[idFilosofo-1] = usando ? Color.GREEN : Color.BLUE;
        repaint();
    }
}


@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Mostrar estado general si existe
    if (estadoBuffer != null) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString(estadoBuffer, 20, 30);
    }

    // CASO 1: Productor-Consumidor
    if (procesos.size() == 2 && recursos.size() == 1) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int prodX = centerX - 200, prodY = centerY;
        int consX = centerX + 200, consY = centerY;
        int bufX = centerX, bufY = centerY;

        dibujarCirculo(g2, prodX, prodY, 30, (procesoActual == 0 ? Color.GREEN : Color.BLUE), "Productor");
        dibujarCirculo(g2, consX, consY, 30, (procesoActual == 1 ? Color.GREEN : Color.BLUE), "Consumidor");
        dibujarRectangulo(g2, bufX - 40, bufY - 30, 80, 60, Color.LIGHT_GRAY, "Buffer");

        dibujarFlecha(g2, prodX + 30, prodY, bufX - 40, bufY, productorActivo ? Color.GREEN : Color.BLUE);
        dibujarFlecha(g2, consX - 30, consY, bufX + 40, bufY, consumidorActivo ? Color.GREEN : Color.BLUE);
    }

    // CASO 2: Filósofos Comensales
    else if (procesos.size() == 5 && recursos.size() == 5) {
        int centerX = getWidth() / 2, centerY = getHeight() / 2;
        int rFilosofos = Math.min(getWidth(), getHeight()) / 3;
        int rTenedores = rFilosofos - 80;

        Point[] posFilosofos = new Point[5];
        Point[] posTenedores = new Point[5];

        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5;
            int x = centerX + (int)(rFilosofos * Math.cos(angle));
            int y = centerY + (int)(rFilosofos * Math.sin(angle));
            posFilosofos[i] = new Point(x, y);
            Color c = (filosofosActivos[i]) ? Color.GREEN : (colorFilosofos[i] != null ? colorFilosofos[i] : Color.BLUE);
            dibujarCirculo(g2, x, y, 25, c, "F" + (i + 1));
        }

        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 + Math.PI / 5;
            int x = centerX + (int)(rTenedores * Math.cos(angle));
            int y = centerY + (int)(rTenedores * Math.sin(angle));
            posTenedores[i] = new Point(x, y);
            Color c = tenedoresEnUso[i] ? Color.GREEN : Color.LIGHT_GRAY;
            dibujarRectangulo(g2, x - 15, y - 15, 30, 30, c, "T" + (i + 1));
        }

        for (int i = 0; i < 5; i++) {
            Point f = posFilosofos[i];
            Point tIzq = posTenedores[(i + 4) % 5];
            Point tDer = posTenedores[i];
            Color c = (colorFlechasFilosofos[i] != null) ? colorFlechasFilosofos[i] : Color.BLUE;
            dibujarFlecha(g2, f.x, f.y, tIzq.x, tIzq.y, c);
            dibujarFlecha(g2, f.x, f.y, tDer.x, tDer.y, c);
        }
    }

    // CASO 3: Barbero Dormilón
    else if (procesos.size() == 1 && recursos.size() > 0) {
        int centerX = getWidth() / 2;
        int startY = 100;
        int espacio = 60;
        int totalSillas = recursos.size() - 1;
        int startX = centerX - (totalSillas * espacio) / 2;
        int ySillas = startY + 150;

        dibujarCirculo(g2, centerX, startY, 25, (procesoActual == 0 ? Color.GREEN : Color.BLUE), "Barbero");
        dibujarRectangulo(g2, centerX - 25, startY + 60, 50, 40, (procesoActual == 0 ? Color.ORANGE : Color.LIGHT_GRAY), "Silla Corte");
        dibujarFlecha(g2, centerX, startY + 25, centerX, startY + 60, Color.BLACK);

        for (int i = 0; i < totalSillas; i++) {
            int x = startX + (i * espacio);
            boolean ocupada = i < sillasOcupadas;
            String txt = ocupada ? "Cliente " + clientesEsperandoIds.get(i) : "Silla " + (i + 1);
            Color c = ocupada ? Color.ORANGE : Color.LIGHT_GRAY;
            dibujarRectangulo(g2, x, ySillas, 50, 40, c, txt);
        }

        if (totalSillas > 0) {
            dibujarFlecha(g2, centerX, startY + 100, centerX, ySillas - 10, Color.BLACK);
        }
    }

    // CASO 4: Fumadores
    else if (procesos.size() == 4 && recursos.size() == 0 &&
             procesos.contains("Agente") && procesos.stream().anyMatch(p -> p.contains("Fumador"))) {
        int xStart = 100, y = getHeight() / 2;
        int spacing = 150;
        for (int i = 0; i < procesos.size(); i++) {
            Color color = (procesos.get(i).equals("Agente")) ? Color.MAGENTA : Color.CYAN;
            dibujarCirculo(g2, xStart + i * spacing, y, 30, color, procesos.get(i));
        }
    }

    // CASO 5: Lectores y Escritores
    // CASO 5: Lectores y Escritores
    else if (procesos.size() == 3 && recursos.size() == 1 &&
        procesos.contains("Lector 1") && procesos.contains("Lector 2") && procesos.contains("Escritor")) {

        int yProcesos = getHeight() / 2;
        int yRecurso = yProcesos + 100;
        int xStart = 120;
        int spacing = 180;

        Point[] puntos = new Point[3];
        for (int i = 0; i < 3; i++) {
            int x = xStart + i * spacing;
            Color color = procesos.get(i).contains("Lector") ? Color.CYAN : Color.RED;
            dibujarCirculo(g2, x, yProcesos, 25, color, procesos.get(i));
            puntos[i] = new Point(x, yProcesos);
        }

        int xRecurso = getWidth() / 2 - 40;
        int ancho = 80, alto = 40;
        int yRecursoBox = yRecurso;

        dibujarRectangulo(g2, xRecurso, yRecursoBox, ancho, alto, Color.LIGHT_GRAY, recursos.get(0));

        // Flechas desde procesos al recurso compartido
        Point centroRecurso = new Point(xRecurso + ancho / 2, yRecursoBox + alto / 2);
        for (Point p : puntos) {
            Point origen = calcularPuntoPerimetro(p, centroRecurso, 25);    // borde del círculo
            Point destino = calcularPuntoPerimetro(centroRecurso, p, 40);   // borde del rectángulo
            dibujarFlecha(g2, origen.x, origen.y, destino.x, destino.y, Color.BLACK);
        }
    }


}




// Nuevo método para actualizar sillas
public void actualizarSillasEspera(List<Integer> clientesEsperando) {
    this.sillasOcupadas = clientesEsperando.size();
    this.clientesEsperandoIds = new ArrayList<>(clientesEsperando);
    repaint();
}

// Métodos auxiliares
private void dibujarCirculo(Graphics2D g2, int x, int y, int radio, Color fill, String texto) {
    g2.setColor(fill);
    g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);
    g2.setColor(Color.BLACK);
    g2.drawOval(x - radio, y - radio, radio * 2, radio * 2);
    g2.drawString(texto, x - 10, y + 5);
}

private void dibujarRectangulo(Graphics2D g2, int x, int y, int width, int height, Color fill, String texto) {
    g2.setColor(fill);
    g2.fillRect(x, y, width, height);
    g2.setColor(Color.BLACK);
    g2.drawRect(x, y, width, height);
    g2.drawString(texto, x + 5, y + height/2 + 5);
}

private void dibujarFlecha(Graphics2D g2, int x1, int y1, int x2, int y2, Color color) {
    g2.setColor(color);
    g2.setStroke(new BasicStroke(2));
    g2.drawLine(x1, y1, x2, y2);
    
    // Punta de flecha
    double angle = Math.atan2(y2 - y1, x2 - x1);
    int arrowSize = 8;
    int arrowX1 = (int)(x2 - arrowSize * Math.cos(angle - Math.PI/6));
    int arrowY1 = (int)(y2 - arrowSize * Math.sin(angle - Math.PI/6));
    int arrowX2 = (int)(x2 - arrowSize * Math.cos(angle + Math.PI/6));
    int arrowY2 = (int)(y2 - arrowSize * Math.sin(angle + Math.PI/6));
    
    g2.drawLine(x2, y2, arrowX1, arrowY1);
    g2.drawLine(x2, y2, arrowX2, arrowY2);
}

// Métodos auxiliares necesarios
private Point calcularCentroFilosofo(int id) {
    int centerX = getWidth() / 2;
    int centerY = getHeight() / 2;
    int radio = Math.min(getWidth(), getHeight()) / 3;
    double angle = 2 * Math.PI * (id-1) / 5;
    
    return new Point(
        (int)(centerX + radio * Math.cos(angle)),
        (int)(centerY + radio * Math.sin(angle))
    );
}

private Point calcularCentroTenedor(int id) {
    int centerX = getWidth() / 2;
    int centerY = getHeight() / 2;
    int radio = Math.min(getWidth(), getHeight()) / 3 - 80;
    double angle = 2 * Math.PI * (id-1) / 5 + Math.PI/5;
    
    return new Point(
        (int)(centerX + radio * Math.cos(angle)),
        (int)(centerY + radio * Math.sin(angle))
    );
}

private Point calcularPuntoPerimetro(Point centro, Point destino, int radio) {
    double dx = destino.x - centro.x;
    double dy = destino.y - centro.y;
    double distancia = Math.sqrt(dx*dx + dy*dy);
    
    if (distancia == 0) return centro;
    
    double factor = radio / distancia;
    return new Point(
        (int)(centro.x + dx * factor),
        (int)(centro.y + dy * factor)
    );
}


public void configurarBarberoDormilon(int totalSillas, boolean barberoOcupado) {
    limpiarPanel();
    
    // 1 proceso (barbero) y N recursos (sillas)
    agregarProcesosYRecursos(1, totalSillas);
    
    // Configurar estado inicial
    setEstadoBarbero(barberoOcupado ? "CORTANDO" : "DURMIENDO");
    setEstadoSillaBarbero(barberoOcupado);
}


public void actualizarSillasEspera(int cantidad) {
    this.sillasOcupadas = cantidad;
    repaint();
}

public void setEstadoBarbero(String estado) {
    SwingUtilities.invokeLater(() -> {
        try {
            this.estadoBuffer = estado;
            repaint();
        } catch (Exception e) {
            System.err.println("Error actualizando estado barbero: " + e.getMessage());
        }
    });
}



public void setEstadoSillaBarbero(boolean ocupada) {
    SwingUtilities.invokeLater(() -> {
        this.procesoActual = ocupada ? 0 : -1;
        repaint();
    });
}

    public void limpiarPanel() {
    procesos.clear(); 
    recursos.clear(); 
    repaint(); 
    }

    public boolean estaVacio() {
        return procesos.isEmpty() && recursos.isEmpty(); 
    }
    
}

