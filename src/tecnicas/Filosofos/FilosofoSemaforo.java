package tecnicas.Filosofos;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.awt.Color;
import java.util.concurrent.Semaphore;

public class FilosofoSemaforo implements TecnicaSincronizacion {

    private final Semaphore[] tenedores = {
        new Semaphore(1), new Semaphore(1), new Semaphore(1), new Semaphore(1), new Semaphore(1)
    };

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        for (int i = 0; i < 5; i++) {
            final int id = i;
            final int displayId = id + 1;

            Thread filosofo = new Thread(() -> {
                while (true) {
                    try {
                        // Pensar
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + displayId, Estado.BLOQUEADO);
                        Visualizador.getPanelGrafo().setFilosofoActivo(displayId, false);
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(displayId, false);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(displayId, "PENSANDO", Color.BLUE);
                        Thread.sleep((long)(Math.random() * 2000));

                        // Intentar tomar tenedores
                        tenedores[id].acquire();
                        tenedores[(id + 1) % 5].acquire();

                        // Comer
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(displayId, true);
                        Visualizador.getPanelGrafo().setFilosofoActivo(displayId, true);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(displayId, "COMIENDO", Color.GREEN);
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + displayId, Estado.ACTIVO);
                        Thread.sleep(1000);

                        // Soltar tenedores
                        tenedores[id].release();
                        tenedores[(id + 1) % 5].release();

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
