package tecnicas.Fumadores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.CyclicBarrier;

public class FumadoresBarrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(2);

    @Override
    public void ejecutar() {
        for (int i = 0; i < 3; i++) {
            final int id = i;
            Thread fumador = new Thread(() -> {
                while (true) {
                    try {
                        barrera.await();
                        Visualizador.panelDiagrama.actualizarEstado("Fumador " + (id + 1), Estado.ACTIVO);
                        Visualizador.panelGrafo.setEstadoBuffer("Fumador " + (id + 1) + " fumando...");
                        Thread.sleep(1000);
                        Visualizador.panelDiagrama.actualizarEstado("Fumador " + (id + 1), Estado.FINALIZADO);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            fumador.start();
        }

        Thread agente = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.ACTIVO);
                    Thread.sleep(800);
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.FINALIZADO);
                    barrera.await();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        agente.start();
    }
}