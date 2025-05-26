package tecnicas.Barbero;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BarberoMonitor implements TecnicaSincronizacion {

    private final Barberia barberia = new Barberia();

    class Barberia {
        private final Queue<Integer> clientes = new LinkedList<>();
        private final int maxSillas = 3;

        public synchronized boolean entrarCliente(int id) {
            if (clientes.size() < maxSillas) {
                clientes.add(id);
                notify(); // Notificar al barbero que llegó alguien
                return true;
            }
            return false;
        }

        public synchronized int esperarCliente() throws InterruptedException {
            while (clientes.isEmpty()) {
                Visualizador.getPanelGrafo().setEstadoBarbero("DURMIENDO");
                Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.BLOQUEADO);
                wait();
            }
            return clientes.poll();
        }

        public synchronized List<Integer> obtenerCola() {
            return new LinkedList<>(clientes); // Copia segura
        }
    }

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        Thread barbero = new Thread(() -> {
            while (true) {
                try {
                    int clienteId = barberia.esperarCliente();
                    Visualizador.getPanelGrafo().setEstadoBarbero("CORTANDO");
                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.ACTIVO);
                    Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + clienteId, Estado.ACTIVO);
                    Visualizador.getPanelGrafo().actualizarSillasEspera(barberia.obtenerCola());

                    Thread.sleep(1000); // tiempo de corte

                    Visualizador.getPanelDiagrama().actualizarEstado("Barbero", Estado.BLOQUEADO);
                    Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + clienteId, Estado.FINALIZADO);
                } catch (InterruptedException e) {
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

                        boolean pudoEntrar = barberia.entrarCliente(cid);
                        if (pudoEntrar) {
                            Visualizador.getPanelGrafo().actualizarSillasEspera(barberia.obtenerCola());
                        } else {
                            Visualizador.getPanelDiagrama().actualizarEstado("Cliente-" + cid, Estado.FINALIZADO);
                            System.out.println("❌ Cliente " + cid + " se fue: no hay sillas");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Cliente-" + cid);

            cliente.start();
        }
    }
}
