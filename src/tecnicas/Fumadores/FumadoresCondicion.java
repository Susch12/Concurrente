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
        Visualizador.getPanelDiagrama().iniciarTick();

        // Fumadores
        for (int i = 0; i < 3; i++) {
            final int id = i;
            final int displayId = id + 1;

            Thread fumador = new Thread(() -> {
                while (true) {
                    Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.BLOQUEADO);

                    lock.lock();
                    try {
                        while (ingrediente != id) condiciones[id].await();

                        // Fumador activo
                        ingrediente = -1;
                        mesaLibre.signal();

                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.ACTIVO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Fumador " + displayId + " fumando...");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }

                    try {
                        Thread.sleep(1000); // fumar
                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.FINALIZADO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Fumador " + displayId + " terminó.");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Fumador-" + displayId);

            fumador.start();
        }

        // Agente
        Thread agente = new Thread(() -> {
            while (true) {
                int i = (int) (Math.random() * 3);
                int displayId = i + 1;

                lock.lock();
                try {
                    while (ingrediente != -1) mesaLibre.await();

                    ingrediente = i;
                    Visualizador.getPanelDiagrama().actualizarEstado("Agente", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Agente entrega ingrediente " + displayId);

                    Thread.sleep(800);

                    Visualizador.getPanelDiagrama().actualizarEstado("Agente", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Esperando fumador...");
                    condiciones[i].signal();

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        }, "Agente");

        agente.start();
    }
}
