package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.concurrent.CyclicBarrier;

public class PC_Barrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(2);

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        // Productor
        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.BLOQUEADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Productor esperando sincronización...");

                    barrera.await();

                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Produciendo...");
                    Thread.sleep(800);

                    Visualizador.getPanelDiagrama().actualizarEstado("Productor", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Productor terminó.");

                    Thread.sleep(500);

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Productor");

        // Consumidor
        Thread consumidor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.BLOQUEADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Consumidor esperando sincronización...");

                    barrera.await();

                    Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Consumiendo...");
                    Thread.sleep(800);

                    Visualizador.getPanelDiagrama().actualizarEstado("Consumidor", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Consumidor terminó.");

                    Thread.sleep(500);

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumidor");

        productor.start();
        consumidor.start();
    }
}
