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
        Visualizador.getPanelDiagrama().iniciarTick();

        // Fumadores
        for (int i = 0; i < 3; i++) {
            final int id = i;
            final int displayId = id + 1;

            Thread fumador = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.BLOQUEADO);

                        fumadores[id].acquire();

                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.ACTIVO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Fumador " + displayId + " fumando...");
                        Thread.sleep(1000);

                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.FINALIZADO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Fumador " + displayId + " terminó.");

                        agente.release();
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Fumador-" + displayId);

            fumador.start();
        }

        // Agente
        Thread ag = new Thread(() -> {
            while (true) {
                try {
                    agente.acquire();

                    int i = (int) (Math.random() * 3);
                    int displayId = i + 1;

                    Visualizador.getPanelDiagrama().actualizarEstado("Agente", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Agente entrega ingredientes al Fumador " + displayId);
                    Thread.sleep(800);

                    Visualizador.getPanelDiagrama().actualizarEstado("Agente", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Esperando fumador...");
                    fumadores[i].release();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Agente");

        ag.start();
    }
}
