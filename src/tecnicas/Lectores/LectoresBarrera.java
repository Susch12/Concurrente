package tecnicas.Lectores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.concurrent.CyclicBarrier;

public class LectoresBarrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(3);

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        // Lectores
        for (int i = 1; i <= 2; i++) {
            final int id = i;

            Thread lector = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.BLOQUEADO);

                        barrera.await();

                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.ACTIVO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Lector " + id + " leyendo...");
                        Thread.sleep(800);

                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.FINALIZADO);
                        Thread.sleep(400);

                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Lector-" + id);

            lector.start();
        }

        // Escritor
        Thread escritor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.BLOQUEADO);

                    barrera.await();

                    Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Escritor escribiendo...");
                    Thread.sleep(1000);

                    Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Esperando sincronización...");
                    Thread.sleep(500);

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Escritor");

        escritor.start();
    }
}
