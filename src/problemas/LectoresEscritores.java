package problemas;

import tecnicas.Lectores.TecnicaSincronizacion;

public class LectoresEscritores {

    private static TecnicaSincronizacion estrategia;

    public static void setEstrategia(TecnicaSincronizacion e) {
        estrategia = e;
    }

    public static void iniciar() {
        if (estrategia == null) {
            System.err.println("⚠ No se ha establecido una estrategia para Lectores/Escritores.");
            return;
        }

        System.out.println("✅ Iniciando Lectores/Escritores con: " + estrategia.getClass().getSimpleName());
        estrategia.ejecutar();
    }
}
