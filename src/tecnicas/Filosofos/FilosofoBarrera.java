package tecnicas.Filosofos;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.awt.Color;
import java.util.concurrent.CyclicBarrier;

public class FilosofoBarrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(5);

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        for (int i = 1; i <= 5; i++) {
            final int id = i;

            Thread filosofo = new Thread(() -> {
                while (true) {
                    try {
                        // Pensar
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + id, Estado.BLOQUEADO);
                        Visualizador.getPanelGrafo().setFilosofoActivo(id, false);
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(id, false);
                        Thread.sleep((long) (Math.random() * 2000));

                        // Espera a que todos lleguen
                        barrera.await();

                        // Comer
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + id, Estado.ACTIVO);
                        Visualizador.getPanelGrafo().setFilosofoActivo(id, true);
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(id, true);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(id, "COMIENDO", Color.GREEN);
                        Thread.sleep(1000);

                        // Terminar
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + id, Estado.FINALIZADO);
                        Visualizador.getPanelGrafo().setFilosofoActivo(id, false);
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(id, false);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(id, "PENSANDO", Color.BLUE);
                        Thread.sleep(500);

                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Filosofo-" + id);

            filosofo.start();
        }
    }
}
