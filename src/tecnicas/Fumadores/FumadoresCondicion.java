package tecnicas.Fumadores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.locks.*;

public class FumadoresCondicion implements TecnicaSincronizacion {

    private final Lock lock = new ReentrantLock();
    private final Condition[] condiciones = {
        lock.newCondition(), lock.newCondition(), lock.newCondition()
    };
    private final Condition mesaLibre = lock.newCondition();
    private int ingrediente = -1;

    @Override
    public void ejecutar() {
        for (int i = 0; i < 3; i++) {
            final int id = i;
            Thread fumador = new Thread(() -> {
                while (true) {
                    lock.lock();
                    try {
                        while (ingrediente != id) condiciones[id].await();
                        ingrediente = -1;
                        mesaLibre.signal();
                        Visualizador.panelDiagrama.actualizarEstado("Fumador " + (id + 1), Estado.ACTIVO);
                        Visualizador.panelGrafo.setEstadoBuffer("Fumador " + (id + 1) + " fumando...");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }

                    try {
                        Thread.sleep(1000);
                        Visualizador.panelDiagrama.actualizarEstado("Fumador " + (id + 1), Estado.FINALIZADO);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            fumador.start();
        }

        Thread agente = new Thread(() -> {
            while (true) {
                int i = (int) (Math.random() * 3);
                lock.lock();
                try {
                    while (ingrediente != -1) mesaLibre.await();
                    ingrediente = i;
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Agente entrega ingrediente " + (i + 1));
                    Thread.sleep(800);
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.FINALIZADO);
                    condiciones[i].signal();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        agente.start();
    }
}