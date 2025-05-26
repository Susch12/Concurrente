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
        Visualizador.getPanelDiagrama().iniciarTick();

        // Lectores
        for (int i = 1; i <= 2; i++) {
            final int id = i;

            Thread lector = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.BLOQUEADO);

                        mutex.acquire();
                        lectores++;
                        if (lectores == 1) write.acquire();
                        mutex.release();

                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.ACTIVO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Lector " + id + " leyendo...");
                        Thread.sleep(1000);
                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.FINALIZADO);

                        mutex.acquire();
                        lectores--;
                        if (lectores == 0) write.release();
                        mutex.release();

                        Thread.sleep(500);

                    } catch (InterruptedException e) {
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

                    write.acquire();

                    Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Escritor escribiendo...");
                    Thread.sleep(1200);

                    Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Escritor terminó.");

                    write.release();
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Escritor");

        escritor.start();
    }
}
