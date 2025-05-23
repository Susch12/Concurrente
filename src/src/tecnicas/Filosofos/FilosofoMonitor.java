package tecnicas.Filosofos;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

public class FilosofoMonitor implements TecnicaSincronizacion {

    private final Object[] tenedores = new Object[5];

    public FilosofoMonitor() {
        for (int i = 0; i < 5; i++) tenedores[i] = new Object();
    }

    @Override
    public void ejecutar() {
        for (int i = 0; i < 5; i++) {
            final int id = i;
            Thread filosofo = new Thread(() -> {
                while (true) {
                    int izq = id;
                    int der = (id + 1) % 5;

                    try {
                        synchronized (tenedores[izq]) {
                            synchronized (tenedores[der]) {
                                Visualizador.panelGrafo.asignarTenedoresFilosofo(id + 1, true);
                                Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.ACTIVO);
                                Thread.sleep(1000);
                                Visualizador.panelGrafo.asignarTenedoresFilosofo(id + 1, false);
                                Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.FINALIZADO);
                            }
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            filosofo.start();
        }
    }
}