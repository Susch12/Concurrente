package tecnicas.Lectores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.CyclicBarrier;

public class LectoresBarrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(3);

    @Override
    public void ejecutar() {
        for (int i = 1; i <= 2; i++) {
            final int id = i;
            Thread lector = new Thread(() -> {
                while (true) {
                    try {
                        barrera.await();
                        Visualizador.panelDiagrama.actualizarEstado("Lector " + id, Estado.ACTIVO);
                        Visualizador.panelGrafo.setEstadoBuffer("Lector " + id + " leyendo...");
                        Thread.sleep(800);
                        Visualizador.panelDiagrama.actualizarEstado("Lector " + id, Estado.FINALIZADO);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            lector.start();
        }

        Thread escritor = new Thread(() -> {
            while (true) {
                try {
                    barrera.await();
                    Visualizador.panelDiagrama.actualizarEstado("Escritor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Escribiendo...");
                    Thread.sleep(1000);
                    Visualizador.panelDiagrama.actualizarEstado("Escritor", Estado.FINALIZADO);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        escritor.start();
    }
}