package problemas;

import tecnicas.Barbero.TecnicaSincronizacion;

public class BarberoDormilon {

    private static TecnicaSincronizacion estrategia;

    public static void setEstrategia(TecnicaSincronizacion e) {
        estrategia = e;
    }

    public static void iniciar() {
        if (estrategia == null) {
            System.err.println("⚠ No se ha establecido una estrategia para Barbero Dormilón.");
            return;
        }

        System.out.println("✅ Iniciando Barbero Dormilón con: " + estrategia.getClass().getSimpleName());
        estrategia.ejecutar();
    }
}
