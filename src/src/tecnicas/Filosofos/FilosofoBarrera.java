package tecnicas.Filosofos;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.CyclicBarrier;

public class FilosofoBarrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(5);

    @Override
    public void ejecutar() {
        for (int i = 0; i < 5; i++) {
            final int id = i;
            Thread filosofo = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.BLOQUEADO);
                        barrera.await();
                        Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.ACTIVO);
                        Thread.sleep(1000);
                        Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.FINALIZADO);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            filosofo.start();
        }
    }
}