package problemas;

import tecnicas.Filosofos.TecnicaSincronizacion;

/**
 * Clase problema para los Filósofos Comensales que usa el patrón de estrategia.
 * Se le debe asignar una técnica concreta que implemente la interfaz TecnicaSincronizacion.
 */
public class Filosofos {

    // Técnica actual que será usada (puede ser Semáforo, Condición, Monitor, Barrera)
    private static TecnicaSincronizacion tecnica;

    /**
     * Asigna la técnica de sincronización a usar.
     * @param t instancia de una clase que implemente TecnicaSincronizacion
     */
    public static void setTecnica(TecnicaSincronizacion t) {
        tecnica = t;
    }

    /**
     * Inicia el problema de los filósofos con la técnica previamente asignada.
     */
    public static void iniciar() {
        if (tecnica == null) {
            System.err.println("⚠ Error: No se ha establecido una técnica de sincronización para los filósofos.");
            return;
        }

        System.out.println("✅ Iniciando problema de los Filósofos con técnica: " + tecnica.getClass().getSimpleName());
        tecnica.ejecutar();
    }
}
