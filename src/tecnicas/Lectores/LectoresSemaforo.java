package tecnicas.Lectores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.Semaphore;

public class LectoresSemaforo implements TecnicaSincronizacion {

    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore write = new Semaphore(1);
    private int lectores = 0;

    @Override
    public void ejecutar() {
        // Lectores
        for (int i = 1; i <= 2; i++) {
            final int id = i;
            Thread lector = new Thread(() -> {
                while (true) {
                    try {
                        mutex.acquire();
                        lectores++;
                        if (lectores == 1) write.acquire();
                        mutex.release();

                        Visualizador.panelDiagrama.actualizarEstado("Lector " + id, Estado.ACTIVO);
                        Visualizador.panelGrafo.setEstadoBuffer("Lector " + id + " leyendo...");
                        Thread.sleep(1000);
                        Visualizador.panelDiagrama.actualizarEstado("Lector " + id, Estado.FINALIZADO);

                        mutex.acquire();
                        lectores--;
                        if (lectores == 0) write.release();
                        mutex.release();

                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            lector.start();
        }

        // Escritor
        Thread escritor = new Thread(() -> {
            while (true) {
                try {
                    write.acquire();
                    Visualizador.panelDiagrama.actualizarEstado("Escritor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Escribiendo...");
                    Thread.sleep(1200);
                    Visualizador.panelDiagrama.actualizarEstado("Escritor", Estado.FINALIZADO);
                    write.release();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        escritor.start();
    }
}