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
            while (datos.size() == CAPACIDAD) wait();
            datos.add(contador++);
            Visualizador.panelGrafo.setEstadoBuffer("Produciendo... (" + datos.size() + ")");
            notifyAll();
        }

        public synchronized void consumir() throws InterruptedException {
            while (datos.isEmpty()) wait();
            datos.poll();
            Visualizador.panelGrafo.setEstadoBuffer("Consumiendo... (" + datos.size() + ")");
            notifyAll();
        }
    }

    @Override
    public void ejecutar() {
        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.ACTIVO);
                    buffer.producir();
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.FINALIZADO);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumidor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.ACTIVO);
                    buffer.consumir();
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.FINALIZADO);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        productor.start();
        consumidor.start();
    }
}
