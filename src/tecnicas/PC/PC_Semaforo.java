package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.Semaphore;

public class PC_Semaforo implements TecnicaSincronizacion {

    private final Semaphore lleno = new Semaphore(0);
    private final Semaphore vacio = new Semaphore(5);
    private final Semaphore mutex = new Semaphore(1);

    @Override
    public void ejecutar() {
        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    vacio.acquire();
                    mutex.acquire();

                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Produciendo...");
                    Thread.sleep(800);

                    mutex.release();
                    lleno.release();

                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.FINALIZADO);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumidor = new Thread(() -> {
            while (true) {
                try {
                    lleno.acquire();
                    mutex.acquire();

                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Consumiendo...");
                    Thread.sleep(800);

                    mutex.release();
                    vacio.release();

                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.FINALIZADO);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        productor.start();
        consumidor.start();
    }
}