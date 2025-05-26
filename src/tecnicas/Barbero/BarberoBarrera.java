package tecnicas.Barbero;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class BarberoBarrera implements TecnicaSincronizacion {

    private final CyclicBarrier barrera = new CyclicBarrier(2);
    private final AtomicInteger clienteActual = new AtomicInteger(-1); // ID del cliente que entra

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        Thread barbero = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelGrafo().setEstadoBarbero("DURMIENDO");
                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.BLOQUEADO);
                    barrera.await(); // Espera a un cliente

                    int cid = clienteActual.get();

                    Visualizador.getPanelGrafo().setEstadoBarbero("CORTANDO");
                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.ACTIVO);
                    Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + cid, Estado.ACTIVO);

                    Thread.sleep(1000);

                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.FINALIZADO);
                    Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + cid, Estado.FINALIZADO);

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Barbero");

        barbero.start();

        for (int i = 1; i <= 5; i++) {
            final int cid = i;

            Thread cliente = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep((long) (Math.random() * 2000));

                        Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + cid, Estado.BLOQUEADO);
                        clienteActual.set(cid); // Indicar al barbero quién llegó
                        barrera.await(); // sincroniza con barbero

                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Cliente-" + cid);

            cliente.start();
        }
    }
}
