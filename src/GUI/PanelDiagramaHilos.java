package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

public class PanelDiagramaHilos extends JPanel implements Runnable {

    public enum Estado {
        ACTIVO, BLOQUEADO, FINALIZADO
    }

    private final Map<String, List<Estado>> historialPorHilo = new LinkedHashMap<>();
    private volatile boolean ejecutando = false;
    private int tickActual = 0;

    public PanelDiagramaHilos() {
        setBackground(Color.DARK_GRAY);
    }

    public void iniciarTick() {
        if (!ejecutando) {
            ejecutando = true;
            Thread refrescador = new Thread(this);
            refrescador.setDaemon(true);
            refrescador.start();
        }
    }

    public void reset() {
        ejecutando = false;
        historialPorHilo.clear();
        tickActual = 0;

        ejecutando = true;
        Thread refrescador = new Thread(this);
        refrescador.setDaemon(true);
        refrescador.start();

        repaint();
    }

    public synchronized void actualizarEstado(String nombreHilo, Estado estado) {
        historialPorHilo.putIfAbsent(nombreHilo, new ArrayList<>());
        historialPorHilo.get(nombreHilo).add(estado);
        repaint();
    }

    public void detener() {
        ejecutando = false;
    }

    @Override
    public void run() {
        while (ejecutando) {
            tickActual++;
            repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cellWidth = 60;
        int cellHeight = 30;
        int yStart = 50;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Diagrama de Estados de Procesos", 10, 25);

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        int maxTicks = historialPorHilo.values().stream().mapToInt(List::size).max().orElse(0);
        for (int t = 0; t < maxTicks; t++) {
            g2.drawString("T" + (t + 1), 100 + t * cellWidth, yStart);
        }

        int fila = 1;
        for (Map.Entry<String, List<Estado>> entry : historialPorHilo.entrySet()) {
            String nombre = entry.getKey();
            List<Estado> historial = entry.getValue();

            g2.setColor(Color.WHITE);
            g2.drawString(nombre, 10, yStart + fila * cellHeight + 20);

            int t = 0;
            for (Estado estado : historial) {
                Color color = switch (estado) {
                    case ACTIVO -> Color.GREEN;
                    case BLOQUEADO -> Color.ORANGE;
                    case FINALIZADO -> Color.RED;
                };
                g2.setColor(color);
                g2.fillRect(100 + t * cellWidth, yStart + fila * cellHeight, cellWidth - 2, cellHeight - 2);
                t++;
            }
            fila++;
        }

        int leyendaY = yStart + (fila + 1) * cellHeight;
        g2.setColor(Color.WHITE);
        g2.drawString("Leyenda:", 10, leyendaY);

        dibujarLeyenda(g2, "ACTIVO", Color.GREEN, 100, leyendaY - 12);
        dibujarLeyenda(g2, "BLOQUEADO", Color.ORANGE, 200, leyendaY - 12);
        dibujarLeyenda(g2, "FINALIZADO", Color.RED, 340, leyendaY - 12);
    }

    private void dibujarLeyenda(Graphics2D g2, String texto, Color color, int x, int y) {
        g2.setColor(color);
        g2.fillRect(x, y, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, 20, 20);
        g2.drawString(texto, x + 30, y + 15);
    }

    @Override
    public Dimension getPreferredSize() {
        int cellWidth = 60;
        int baseWidth = 100;
        int baseHeight = 120;
        int maxTicks = historialPorHilo.values().stream().mapToInt(List::size).max().orElse(0);
        int maxProcesos = historialPorHilo.size();

        int width = baseWidth + maxTicks * cellWidth;
        int height = baseHeight + maxProcesos * 40;

        return new Dimension(width, height);
    }
}
