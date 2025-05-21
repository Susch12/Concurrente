package tecnicas.PC;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PC_Condicion implements TecnicaSincronizacion {

    private final Lock lock = new ReentrantLock();
    private final Condition lleno = lock.newCondition();
    private final Condition vacio = lock.newCondition();
    private boolean disponible = false;

    @Override
    public void ejecutar() {
        Thread productor = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    while (disponible) vacio.await();
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Produciendo...");
                    Thread.sleep(800);
                    disponible = true;
                    lleno.signal();
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.FINALIZADO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread consumidor = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    while (!disponible) lleno.await();
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Consumiendo...");
                    Thread.sleep(800);
                    disponible = false;
                    vacio.signal();
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.FINALIZADO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        productor.start();
        consumidor.start();
    }
}