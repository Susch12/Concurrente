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
        Visualizador.panelGrafo.limpiarPanel();
        Visualizador.panelGrafo.agregarProcesosYRecursos(2, 1);
        Visualizador.panelGrafo.setEstadoBuffer("Buffer vacío");

        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    vacio.acquire();
                    mutex.acquire();

                    buffer.add(contador++);
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Produciendo... (" + buffer.size() + ")");
                    Thread.sleep(800);

                    mutex.release();
                    lleno.release();

                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.FINALIZADO);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumidor = new Thread(() -> {
            while (true) {
                try {
                    lleno.acquire();
                    mutex.acquire();

                    buffer.poll();
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Consumiendo... (" + buffer.size() + ")");
                    Thread.sleep(800);

                    mutex.release();
                    vacio.release();

                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.FINALIZADO);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        productor.start();
        consumidor.start();
    }
}
