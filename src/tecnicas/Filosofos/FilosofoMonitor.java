package tecnicas.Filosofos;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.awt.Color;

public class FilosofoMonitor implements TecnicaSincronizacion {

    private final Object[] tenedores = new Object[5];

    public FilosofoMonitor() {
        for (int i = 0; i < 5; i++) {
            tenedores[i] = new Object();
        }
    }

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        for (int i = 0; i < 5; i++) {
            final int id = i;
            final int displayId = id + 1;

            Thread filosofo = new Thread(() -> {
                while (true) {
                    int izq = id;
                    int der = (id + 1) % 5;

                    try {
                        // Pensar
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + displayId, Estado.BLOQUEADO);
                        Visualizador.getPanelGrafo().setFilosofoActivo(displayId, false);
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(displayId, false);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(displayId, "PENSANDO", Color.BLUE);
                        Thread.sleep((long)(Math.random() * 2000));

                        // Tomar tenedores y comer
                        synchronized (tenedores[izq]) {
                            synchronized (tenedores[der]) {
                                Visualizador.getPanelGrafo().asignarTenedoresFilosofo(displayId, true);
                                Visualizador.getPanelGrafo().setFilosofoActivo(displayId, true);
                                Visualizador.getPanelGrafo().setEstadoFilosofo(displayId, "COMIENDO", Color.GREEN);
                                Visualizador.getPanelDiagrama().actualizarEstado("F" + displayId, Estado.ACTIVO);

                                Thread.sleep(1000);
                            }
                        }

                        // Terminar
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(displayId, false);
                        Visualizador.getPanelGrafo().setFilosofoActivo(displayId, false);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(displayId, "FINALIZADO", Color.LIGHT_GRAY);
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + displayId, Estado.FINALIZADO);
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Filosofo-" + displayId);

            filosofo.start();
        }
    }
}
