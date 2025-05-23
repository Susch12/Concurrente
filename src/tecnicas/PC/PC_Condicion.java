package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;

public class PC_Condicion implements TecnicaSincronizacion{

    private final Lock lock = new ReentrantLock();
    private final Condition noLleno = lock.newCondition();
    private final Condition noVacio = lock.newCondition();
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int BUFFER_SIZE = 5;
    private int contador = 1;

    @Override
    public void ejecutar() {
        Visualizador.panelGrafo.limpiarPanel();
        Visualizador.panelGrafo.agregarProcesosYRecursos(2, 1);
        Visualizador.panelGrafo.setEstadoBuffer("Buffer vacío");

        Thread productor = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    while (buffer.size() == BUFFER_SIZE) {
                        Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.BLOQUEADO);
                        noLleno.await();
                    }

                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.ACTIVO);
                    buffer.add(contador++);
                    Visualizador.panelGrafo.setEstadoBuffer("Produciendo... (" + buffer.size() + ")");
                    Thread.sleep(800);
                    noVacio.signal();
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.FINALIZADO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        });

        Thread consumidor = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    while (buffer.isEmpty()) {
                        Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.BLOQUEADO);
                        noVacio.await();
                    }

                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.ACTIVO);
                    buffer.poll();
                    Visualizador.panelGrafo.setEstadoBuffer("Consumiendo... (" + buffer.size() + ")");
                    Thread.sleep(800);
                    noLleno.signal();
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.FINALIZADO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        });

        productor.start();
        consumidor.start();
    }
}
