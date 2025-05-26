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
        Visualizador.getPanelDiagrama().iniciarTick();

        // Lectores
        for (int i = 1; i <= 2; i++) {
            final int id = i;

            Thread lector = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.BLOQUEADO);

                        lock.lock();
                        try {
                            while (escribiendo) puedeLeer.await();
                            lectores++;
                            Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.ACTIVO);
                        } finally {
                            lock.unlock();
                        }

                        Visualizador.getPanelGrafo().setEstadoBuffer("Lector " + id + " leyendo...");
                        Thread.sleep(1000);

                        lock.lock();
                        try {
                            lectores--;
                            Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.FINALIZADO);
                            if (lectores == 0) puedeEscribir.signal();
                        } finally {
                            lock.unlock();
                        }

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

                    lock.lock();
                    try {
                        while (lectores > 0 || escribiendo) puedeEscribir.await();
                        escribiendo = true;
                        Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.ACTIVO);
                    } finally {
                        lock.unlock();
                    }

                    Visualizador.getPanelGrafo().setEstadoBuffer("Escritor escribiendo...");
                    Thread.sleep(1200);

                    lock.lock();
                    try {
                        escribiendo = false;
                        Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.FINALIZADO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Escritor terminó.");
                        puedeLeer.signalAll();
                    } finally {
                        lock.unlock();
                    }

                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Escritor");

        escritor.start();
    }
}
