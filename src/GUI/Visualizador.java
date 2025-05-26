package GUI;

public class Visualizador {
    private static PanelDiagramaHilos panelDiagrama;
    private static PanelGrafoHilos panelGrafo;

    public static void setPaneles(PanelDiagramaHilos diagrama, PanelGrafoHilos grafo) {
        panelDiagrama = diagrama;
        panelGrafo = grafo;
    }

    public static PanelDiagramaHilos getPanelDiagrama() {
        return panelDiagrama;
    }

    public static PanelGrafoHilos getPanelGrafo() {
        return panelGrafo;
    }
}
