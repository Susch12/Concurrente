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

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        Thread hiloBarbero = new Thread(() -> {
            while (true) {
                try {
                    clientes.acquire();

                    accesoSillas.acquire();
                    sillasLibres++;
                    Visualizador.getPanelGrafo().setEstadoBarbero("CORTANDO");
                    Visualizador.getPanelGrafo().actualizarSillasEspera(new ArrayList<>(colaClientes));
                    accesoSillas.release();

                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.ACTIVO);
                    Thread.sleep(1000); // simular corte
                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.BLOQUEADO);

                    barbero.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Barbero");

        hiloBarbero.start();

        for (int i = 1; i <= 5; i++) {
            final int id = i;

            Thread cliente = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep((long) (Math.random() * 2500));

                        Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + id, Estado.BLOQUEADO);

                        accesoSillas.acquire();
                        if (sillasLibres > 0) {
                            sillasLibres--;
                            colaClientes.add(id);
                            Visualizador.getPanelGrafo().actualizarSillasEspera(new ArrayList<>(colaClientes));
                            accesoSillas.release();

                            clientes.release();
                            barbero.acquire();

                            Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + id, Estado.ACTIVO);
                            Thread.sleep(1000); // corte
                            Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + id, Estado.FINALIZADO);

                            accesoSillas.acquire();
                            colaClientes.remove((Integer) id);
                            Visualizador.getPanelGrafo().actualizarSillasEspera(new ArrayList<>(colaClientes));
                            accesoSillas.release();

                        } else {
                            accesoSillas.release();
                            Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + id, Estado.FINALIZADO);
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Cliente-" + id);

            cliente.start();
        }
    }
}
