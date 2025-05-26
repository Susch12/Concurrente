package tecnicas.Barbero;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.*;

public class BarberoCondicion implements TecnicaSincronizacion {

    private final Lock lock = new ReentrantLock();
    private final Condition clientes = lock.newCondition();
    private final Queue<Integer> espera = new LinkedList<>();
    private final int maxSillas = 3;

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        Thread barbero = new Thread(() -> {
            while (true) {
                int clienteId = -1;

                lock.lock();
                try {
                    while (espera.isEmpty()) {
                        Visualizador.getPanelGrafo().setEstadoBarbero("DURMIENDO");
                        Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.BLOQUEADO);
                        clientes.await();
                    }
                    clienteId = espera.poll();
                    Visualizador.getPanelGrafo().actualizarSillasEspera(new LinkedList<>(espera));
                    Visualizador.getPanelGrafo().setEstadoBarbero("CORTANDO");
                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.ACTIVO);
                    Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + clienteId, Estado.ACTIVO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

                try {
                    Thread.sleep(1000);
                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.BLOQUEADO);
                    Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + clienteId, Estado.FINALIZADO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Barbero");

        barbero.start();

        for (int i = 1; i <= 5; i++) {
            final int cid = i;
            Thread cliente = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep((long) (Math.random() * 2000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + cid, Estado.BLOQUEADO);

                    lock.lock();
                    try {
                        if (espera.size() < maxSillas) {
                            espera.add(cid);
                            Visualizador.getPanelGrafo().actualizarSillasEspera(new LinkedList<>(espera));
                            clientes.signal();
                        } else {
                            Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + cid, Estado.FINALIZADO);
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }, "Cliente-" + cid);

            cliente.start();
        }
    }
}
