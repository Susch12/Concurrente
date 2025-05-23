package tecnicas.Barbero;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;

public class BarberoSemaforo implements TecnicaSincronizacion {

    private final Semaphore clientes = new Semaphore(0);
    private final Semaphore barbero = new Semaphore(0);
    private final Semaphore accesoSillas = new Semaphore(1);
    private int sillasLibres = 3;
    private final List<Integer> colaClientes = new ArrayList<>();
    private int idCliente = 1;

    @Override
    public void ejecutar() {
        Thread barberoHilo = new Thread(() -> {
            while (true) {
                try {
                    clientes.acquire();
                    accesoSillas.acquire();
                    sillasLibres++;
                    Visualizador.panelGrafo.actualizarSillasEspera(colaClientes);
                    accesoSillas.release();

                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBarbero("CORTANDO");
                    Thread.sleep(1000);
                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.FINALIZADO);
                    barbero.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        barberoHilo.start();

        // Clientes recurrentes
        for (int i = 0; i < 5; i++) {
            final int cid = i;
            Thread cliente = new Thread(() -> {
                while (true) {
                    try {
                        accesoSillas.acquire();
                        if (sillasLibres > 0) {
                            sillasLibres--;
                            colaClientes.add(cid);
                            Visualizador.panelGrafo.actualizarSillasEspera(colaClientes);
                            accesoSillas.release();

                            clientes.release();
                            barbero.acquire();

                            colaClientes.remove((Integer) cid);
                            Visualizador.panelGrafo.actualizarSillasEspera(colaClientes);
                        } else {
                            accesoSillas.release(); // Se va sin corte
                        }
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            cliente.start();
        }
    }
}