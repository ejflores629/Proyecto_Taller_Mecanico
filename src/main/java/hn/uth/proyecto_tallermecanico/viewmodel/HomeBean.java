package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.DashboardStats;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import retrofit2.Response;

// Importaciones de XDev
import software.xdev.chartjs.model.charts.BarChart;
import software.xdev.chartjs.model.charts.PieChart;
import software.xdev.chartjs.model.color.RGBAColor;
import software.xdev.chartjs.model.data.BarData;
import software.xdev.chartjs.model.data.PieData;
import software.xdev.chartjs.model.dataset.BarDataset;
import software.xdev.chartjs.model.dataset.PieDataset;
import software.xdev.chartjs.model.options.BarOptions;
import software.xdev.chartjs.model.options.PieOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("homeBean")
@ViewScoped
public class HomeBean implements Serializable {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Getter
    private DashboardStats kpi;

    @Getter
    private String pieModelJson;

    @Getter
    private String barModelJson;

    @PostConstruct
    public void init() {
        cargarKPIs();
        crearGraficoPastel();
        crearGraficoBarras();
    }

    private void cargarKPIs() {
        try {
            Response<DashboardStats> response = apiService.getResumenKpi().execute();
            this.kpi = response.isSuccessful() ? response.body() : new DashboardStats();
        } catch (Exception e) {
            this.kpi = new DashboardStats();
        }
    }

    private void crearGraficoPastel() {
        try {
            PieData data = new PieData();
            PieDataset dataset = new PieDataset();

            // CORRECCIÓN: Usar List<Number> en lugar de List<BigDecimal>
            List<Number> values = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            List<RGBAColor> colors = new ArrayList<>();

            Response<ORDSCollectionResponse<DashboardStats>> response = apiService.getStatsOrdenesPorEstado().execute();

            if (response.isSuccessful() && response.body() != null) {
                for (DashboardStats stat : response.body().getItems()) {
                    labels.add(stat.getLabel());
                    // BigDecimal es hijo de Number, así que se puede agregar sin problemas
                    values.add(stat.getValue());

                    switch (stat.getLabel()) {
                        case "PENDIENTE": colors.add(new RGBAColor(255, 193, 7)); break; // Amarillo
                        case "EN PROCESO": colors.add(new RGBAColor(23, 162, 184)); break; // Azul Cian
                        case "TERMINADA": colors.add(new RGBAColor(40, 167, 69)); break; // Verde
                        case "CERRADA": colors.add(new RGBAColor(108, 117, 125)); break; // Gris
                        default: colors.add(new RGBAColor(0, 123, 255));
                    }
                }
            }

            dataset.setData(values)
                    .addBackgroundColor(colors.toArray(new RGBAColor[0]));

            data.addDataset(dataset)
                    .setLabels(labels);

            PieOptions options = new PieOptions()
                    .setMaintainAspectRatio(false)
                    .setResponsive(true);

            this.pieModelJson = new PieChart(data, options).toJson();

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void crearGraficoBarras() {
        try {
            BarData data = new BarData();

            BarDataset dataset = new BarDataset()
                    .setLabel("Ingresos Mensuales (L.)")
                    .setBackgroundColor(new RGBAColor(54, 162, 235, 0.6f))
                    .setBorderColor(new RGBAColor(54, 162, 235))
                    .setBorderWidth(1);

            // CORRECCIÓN: Usar List<Number> aquí también
            List<Number> values = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            Response<ORDSCollectionResponse<DashboardStats>> response = apiService.getStatsVentasHistorial().execute();

            if (response.isSuccessful() && response.body() != null) {
                for (DashboardStats stat : response.body().getItems()) {
                    labels.add(stat.getLabel());
                    values.add(stat.getValue());
                }
            }

            dataset.setData(values);
            data.addDataset(dataset)
                    .setLabels(labels);

            BarOptions options = new BarOptions()
                    .setMaintainAspectRatio(false)
                    .setResponsive(true);

            this.barModelJson = new BarChart(data, options).toJson();

        } catch (Exception e) { e.printStackTrace(); }
    }
}