package tecnicas.Lectores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

public class LectoresMonitor implements TecnicaSincronizacion {

    private final RecursoCompartido recurso = new RecursoCompartido();

    class RecursoCompartido {
        private int lectores = 0;
        private boolean escribiendo = false;

        public synchronized void empezarLeer(int id) throws InterruptedException {
            while (escribiendo) wait();
            lectores++;
        }

        public synchronized void terminarLeer(int id) {
            lectores--;
            if (lectores == 0) notifyAll();
        }

        public synchronized void empezarEscribir() throws InterruptedException {
            while (lectores > 0 || escribiendo) wait();
            escribiendo = true;
        }

        public synchronized void terminarEscribir() {
            escribiendo = false;
            notifyAll();
        }
    }

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        // Lectores
        for (int i = 1; i <= 2; i++) {
            final int id = i;

            Thread lector = new Thread(() -> {
                while (true) {
                    try {
                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.BLOQUEADO);

                        recurso.empezarLeer(id);

                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.ACTIVO);
                        Visualizador.getPanelGrafo().setEstadoBuffer("Lector " + id + " leyendo...");
                        Thread.sleep(1000);

                        Visualizador.getPanelDiagrama().actualizarEstado("Lector " + id, Estado.FINALIZADO);
                        recurso.terminarLeer(id);
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Lector-" + id);

            lector.start();
        }

        // Escritor
        Thread escritor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.BLOQUEADO);

                    recurso.empezarEscribir();

                    Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.ACTIVO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Escritor escribiendo...");
                    Thread.sleep(1200);

                    Visualizador.getPanelDiagrama().actualizarEstado("Escritor", Estado.FINALIZADO);
                    Visualizador.getPanelGrafo().setEstadoBuffer("Escritor terminó.");
                    recurso.terminarEscribir();

                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Escritor");

        escritor.start();
    }
}
