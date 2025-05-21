package problemas;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.concurrent.Semaphore;

public class LectoresEscritores {

    private static final Semaphore mutex = new Semaphore(1); // Protege lectores
    private static final Semaphore accesoBD = new Semaphore(1); // Exclusión para la BD
    private static int lectores = 0;

    public static void iniciar() {
        Visualizador.panelGrafo.limpiarPanel();
        Visualizador.panelGrafo.setNombresProcesos(java.util.List.of("Lector 1", "Lector 2", "Escritor"));
        Visualizador.panelGrafo.agregarProcesosYRecursos(3, 1);  // 3 procesos, 1 recurso
        Visualizador.panelGrafo.setEstadoBuffer("Base de Datos disponible");

        Thread lector1 = new Thread(() -> cicloLector("Lector 1"));
        Thread lector2 = new Thread(() -> cicloLector("Lector 2"));
        Thread escritor = new Thread(() -> cicloEscritor("Escritor"));

        lector1.start();
        lector2.start();
        escritor.start();
    }

    private static void cicloLector(String nombre) {
        while (true) {
            try {
                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.BLOQUEADO);
                mutex.acquire();
                lectores++;
                if (lectores == 1) accesoBD.acquire();
                mutex.release();

                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.ACTIVO);
                Visualizador.panelGrafo.setEstadoBuffer(nombre + " leyendo...");
                Thread.sleep(1000);

                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.FINALIZADO);
                mutex.acquire();
                lectores--;
                if (lectores == 0) accesoBD.release();
                mutex.release();

                Thread.sleep(1000); // Pausa antes de volver a leer

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void cicloEscritor(String nombre) {
        while (true) {
            try {
                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.BLOQUEADO);
                accesoBD.acquire();

                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.ACTIVO);
                Visualizador.panelGrafo.setEstadoBuffer(nombre + " escribiendo...");
                Thread.sleep(1500);

                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.FINALIZADO);
                accesoBD.release();

                Thread.sleep(1500); // Descanso del escritor

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
