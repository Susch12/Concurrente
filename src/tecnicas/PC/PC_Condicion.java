package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;

public class PC_Condicion implements TecnicaSincronizacion {

    private final Lock lock = new ReentrantLock();
    private final Condition bufferLleno = lock.newCondition();
    private final Condition bufferVacio = lock.newCondition();
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int CAPACIDAD = 5;
    private int contador = 0;

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        // Productor
        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.BLOQUEADO);

                    lock.lock();
                    try {
                        while (buffer.size() == CAPACIDAD) {
                            bufferLleno.await();
                        }
                        buffer.add(++contador);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Produciendo ítem " + contador + " (total: " + buffer.size() + ")");
                        Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.ACTIVO);
                        bufferVacio.signal(); // avisar a consumidor
                    } finally {
                        lock.unlock();
                    }

                    Thread.sleep(800);
                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.FINALIZADO);
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Productor");

        // Consumidor
        Thread consumidor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.BLOQUEADO);

                    int itemConsumido = -1;
                    lock.lock();
                    try {
                        while (buffer.isEmpty()) {
                            bufferVacio.await();
                        }
                        itemConsumido = buffer.poll();
                        Visualizador.getPanelGrafo().setEstadoBuffer("Consumiendo ítem " + itemConsumido + " (restantes: " + buffer.size() + ")");
                        Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.ACTIVO);
                        bufferLleno.signal(); // avisar a productor
                    } finally {
                        lock.unlock();
                    }

                    Thread.sleep(800);
                    Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.FINALIZADO);
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumidor");

        productor.start();
        consumidor.start();
    }
}
