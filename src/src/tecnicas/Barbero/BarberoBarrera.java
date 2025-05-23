package tecnicas.Barbero;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.CyclicBarrier;

public class BarberoBarrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(2);

    @Override
    public void ejecutar() {
        Thread barbero = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.BLOQUEADO);
                    barrera.await(); // espera cliente
                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBarbero("CORTANDO");
                    Thread.sleep(1000);
                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.FINALIZADO);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        barbero.start();

        for (int i = 0; i < 5; i++) {
            Thread cliente = new Thread(() -> {
                while (true) {
                    try {
                        barrera.await(); // llega cliente
                        Thread.sleep(1500);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            cliente.start();
        }
    }
}