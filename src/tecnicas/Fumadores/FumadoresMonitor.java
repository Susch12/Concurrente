package tecnicas.Fumadores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

public class FumadoresMonitor implements TecnicaSincronizacion {

    private final Mesa mesa = new Mesa();

    class Mesa {
        private int ingrediente = -1;

        public synchronized void poner(int i) throws InterruptedException {
            while (ingrediente != -1) wait();
            ingrediente = i;
            notifyAll();
        }

        public synchronized void tomar(int id) throws InterruptedException {
            while (ingrediente != id) wait();
            ingrediente = -1;
            notifyAll();
        }
    }

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        // Fumadores
        for (int i = 0; i < 3; i++) {
            final int id = i;
            final int displayId = id + 1;

            Thread fumador = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.BLOQUEADO);

                        mesa.tomar(id);

                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.ACTIVO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Fumador " + displayId + " fumando...");
                        Thread.sleep(1000);

                        Visualizador.getPanelDiagrama().actualizarEstado("Fumador " + displayId, Estado.FINALIZADO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Fumador " + displayId + " terminó.");
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Fumador-" + displayId);

            fumador.start();
        }

        // Agente
        Thread agente = new Thread(() -> {
            while (true) {
                try {
                    int i = (int) (Math.random() * 3);
                    int displayId = i + 1;

                    Visualizador.getPanelDiagrama().actualizarEstado("Agente", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Agente entrega ingrediente " + displayId);
                    Thread.sleep(800);

                    Visualizador.getPanelDiagrama().actualizarEstado("Agente", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Esperando fumador...");
                    mesa.poner(i);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Agente");

        agente.start();
    }
}
