package tecnicas.Fumadores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.concurrent.CyclicBarrier;

public class FumadoresBarrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(2);

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        for (int i = 0; i < 3; i++) {
            final int id = i;
            final int fumadorId = id + 1;

            Thread fumador = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + fumadorId, Estado.BLOQUEADO);

                        barrera.await(); // espera al agente

                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + fumadorId, Estado.ACTIVO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Fumador " + fumadorId + " fumando...");
                        Thread.sleep(1000);

                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + fumadorId, Estado.FINALIZADO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Fumador " + fumadorId + " terminó.");

                        Thread.sleep(500);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Fumador-" + fumadorId);

            fumador.start();
        }

        Thread agente = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Agente", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Agente coloca ingredientes...");
                    Thread.sleep(800);

                    Visualizador.getPanelDiagrama().actualizarEstado("Agente", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Esperando fumador...");

                    barrera.await(); // sincroniza con fumador

                    Thread.sleep(300); // pequeño delay antes de siguiente ronda
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Agente");

        agente.start();
    }
}
