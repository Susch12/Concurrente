package tecnicas.Fumadores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.Semaphore;

public class FumadoresSemaforo implements TecnicaSincronizacion {

    private final Semaphore[] fumadores = {
        new Semaphore(0), new Semaphore(0), new Semaphore(0)
    };
    private final Semaphore agente = new Semaphore(1);

    @Override
    public void ejecutar() {
        // Fumadores
        for (int i = 0; i < 3; i++) {
            final int id = i;
            Thread f = new Thread(() -> {
                while (true) {
                    try {
                        fumadores[id].acquire();
                        Visualizador.panelDiagrama.actualizarEstado("Fumador " + (id + 1), Estado.ACTIVO);
                        Visualizador.panelGrafo.setEstadoBuffer("Fumador " + (id + 1) + " fumando...");
                        Thread.sleep(1000);
                        Visualizador.panelDiagrama.actualizarEstado("Fumador " + (id + 1), Estado.FINALIZADO);
                        agente.release();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            f.start();
        }

        // Agente
        Thread ag = new Thread(() -> {
            while (true) {
                try {
                    agente.acquire();
                    int i = (int) (Math.random() * 3);
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Proporcionando ingredientes al Fumador " + (i + 1));
                    Thread.sleep(800);
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.FINALIZADO);
                    fumadores[i].release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        ag.start();
    }
}