package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.LinkedList;
import java.util.Queue;

public class PC_Monitor implements TecnicaSincronizacion {

    private final Buffer buffer = new Buffer();

    class Buffer {
        private final Queue<Integer> datos = new LinkedList<>();
        private final int CAPACIDAD = 5;
        private int contador = 1;

        public synchronized void producir() throws InterruptedException {
            while (datos.size() == CAPACIDAD) {
                wait();
            }
            datos.add(contador++);
            Visualizador.getPanelGrafo().setEstadoBuffer("Produciendo ítem (" + datos.size() + ")");
            notifyAll();
        }

        public synchronized void consumir() throws InterruptedException {
            while (datos.isEmpty()) {
                wait();
            }
            datos.poll();
            Visualizador.getPanelGrafo().setEstadoBuffer("Consumiendo ítem (" + datos.size() + ")");
            notifyAll();
        }
    }

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        // Productor
        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.BLOQUEADO);

                    buffer.producir();

                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.ACTIVO);
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

                    buffer.consumir();

                    Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.ACTIVO);
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
