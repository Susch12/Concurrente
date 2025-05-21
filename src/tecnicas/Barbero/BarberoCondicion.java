package tecnicas.Barbero;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;

public class BarberoCondicion implements TecnicaSincronizacion {

    private final Lock lock = new ReentrantLock();
    private final Condition clientes = lock.newCondition();
    private final Queue<Integer> espera = new LinkedList<>();
    private final int maxSillas = 3;

    @Override
    public void ejecutar() {
        Thread barbero = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    while (espera.isEmpty()) clientes.await();
                    int cliente = espera.poll();
                    Visualizador.panelGrafo.actualizarSillasEspera(espera.stream().toList());
                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBarbero("CORTANDO");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

                try {
                    Thread.sleep(1000);
                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.FINALIZADO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        barbero.start();

        for (int i = 0; i < 5; i++) {
            final int cid = i;
            Thread cliente = new Thread(() -> {
                while (true) {
                    lock.lock();
                    try {
                        if (espera.size() < maxSillas) {
                            espera.add(cid);
                            clientes.signal();
                            Visualizador.panelGrafo.actualizarSillasEspera(espera.stream().toList());
                        }
                    } finally {
                        lock.unlock();
                    }

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            cliente.start();
        }
    }
}