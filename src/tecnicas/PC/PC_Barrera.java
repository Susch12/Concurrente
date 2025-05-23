package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.CyclicBarrier;

public class PC_Barrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(2);

    @Override
    public void ejecutar() {
        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Productor esperando...");
                    barrera.await();
                    Visualizador.panelGrafo.setEstadoBuffer("Produciendo...");
                    Thread.sleep(800);
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.FINALIZADO);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumidor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Consumidor esperando...");
                    barrera.await();
                    Visualizador.panelGrafo.setEstadoBuffer("Consumiendo...");
                    Thread.sleep(800);
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.FINALIZADO);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        productor.start();
        consumidor.start();
    }
}