package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class PC_Semaforo implements TecnicaSincronizacion {

    private final Queue<Integer> buffer = new LinkedList<>();
    private final int BUFFER_SIZE = 5;
    private final Semaphore lleno = new Semaphore(0);
    private final Semaphore vacio = new Semaphore(BUFFER_SIZE);
    private final Semaphore mutex = new Semaphore(1);
    private int contador = 1;

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();
        Visualizador.getPanelGrafo().setEstadoBuffer("Buffer vacío");

        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.BLOQUEADO);

                    vacio.acquire();
                    mutex.acquire();

                    buffer.add(contador++);
                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Produciendo ítem (" + buffer.size() + ")");

                    Thread.sleep(800);

                    mutex.release();
                    lleno.release();

                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.FINALIZADO);
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Productor");

        Thread consumidor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.BLOQUEADO);

                    lleno.acquire();
                    mutex.acquire();

                    buffer.poll();
                    Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Consumiendo ítem (" + buffer.size() + ")");

                    Thread.sleep(800);

                    mutex.release();
                    vacio.release();

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
