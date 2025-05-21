package tecnicas.Lectores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.locks.*;

public class LectoresCondicion implements TecnicaSincronizacion {

    private final Lock lock = new ReentrantLock();
    private final Condition puedeLeer = lock.newCondition();
    private final Condition puedeEscribir = lock.newCondition();
    private int lectores = 0;
    private boolean escribiendo = false;

    @Override
    public void ejecutar() {
        for (int i = 1; i <= 2; i++) {
            final int id = i;
            Thread lector = new Thread(() -> {
                while (true) {
                    lock.lock();
                    try {
                        while (escribiendo) puedeLeer.await();
                        lectores++;
                        Visualizador.panelDiagrama.actualizarEstado("Lector " + id, Estado.ACTIVO);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }

                    Visualizador.panelGrafo.setEstadoBuffer("Lector " + id + " leyendo...");
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

                    lock.lock();
                    try {
                        lectores--;
                        if (lectores == 0) puedeEscribir.signal();
                        Visualizador.panelDiagrama.actualizarEstado("Lector " + id, Estado.FINALIZADO);
                    } finally {
                        lock.unlock();
                    }

                    try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                }
            });
            lector.start();
        }

        Thread escritor = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    while (lectores > 0 || escribiendo) puedeEscribir.await();
                    escribiendo = true;
                    Visualizador.panelDiagrama.actualizarEstado("Escritor", Estado.ACTIVO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

                Visualizador.panelGrafo.setEstadoBuffer("Escribiendo...");
                try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

                lock.lock();
                try {
                    escribiendo = false;
                    Visualizador.panelDiagrama.actualizarEstado("Escritor", Estado.FINALIZADO);
                    puedeLeer.signalAll();
                } finally {
                    lock.unlock();
                }

                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        });
        escritor.start();
    }
}