package tecnicas;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.Semaphore;

public class FilosofoSemaforo implements TecnicaSincronizacion {

    private final Semaphore[] tenedores = {
        new Semaphore(1), new Semaphore(1), new Semaphore(1), new Semaphore(1), new Semaphore(1)
    };

    @Override
    public void ejecutar() {
        for (int i = 0; i < 5; i++) {
            final int id = i;
            Thread filosofo = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.BLOQUEADO);
                        tenedores[id].acquire();
                        tenedores[(id + 1) % 5].acquire();

                        Visualizador.panelGrafo.asignarTenedoresFilosofo(id + 1, true);
                        Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.ACTIVO);
                        Thread.sleep(1000);

                        Visualizador.panelGrafo.asignarTenedoresFilosofo(id + 1, false);
                        Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.FINALIZADO);

                        tenedores[id].release();
                        tenedores[(id + 1) % 5].release();
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