package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

public class PC_Monitor implements TecnicaSincronizacion {

    private final Buffer buffer = new Buffer();

    class Buffer {
        private int data = 0;
        private boolean disponible = false;

        public synchronized void producir() throws InterruptedException {
            while (disponible) wait();
            Visualizador.panelGrafo.setEstadoBuffer("Produciendo...");
            data++;
            disponible = true;
            notifyAll();
        }

        public synchronized void consumir() throws InterruptedException {
            while (!disponible) wait();
            Visualizador.panelGrafo.setEstadoBuffer("Consumiendo...");
            disponible = false;
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