package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Factura;
import hn.uth.proyecto_tallermecanico.model.Orden;
import hn.uth.proyecto_tallermecanico.repository.FacturaRepository;
import hn.uth.proyecto_tallermecanico.repository.OrdenRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("facturaBean")
@ViewScoped
public class FacturaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacturaRepository facturaRepository;

    @Inject
    private OrdenRepository ordenRepository;

    @Getter
    private LazyDataModel<Factura> facturasLazy;

    @Getter @Setter
    private Factura facturaNueva;

    @Getter @Setter
    private Factura facturaSeleccionada; // Objeto para Anular o Ver Detalle

    @Getter
    private List<Orden> listaOrdenesPendientes;

    @PostConstruct
    public void init() {
        this.facturaNueva = new Factura();
        this.facturaNueva.setMetodo_pago("EFECTIVO");
        generarNumeroFactura();
        iniciarLazyModel();
        cargarOrdenesPendientes();
    }



    public void generarNumeroFactura() {
        try {
            int year = Year.now().getValue();
            // MEJORA: Usamos un contador base, pero si falla, incrementamos hasta encontrar hueco.
            // Iniciamos en count + 1, pero le sumamos un offset de seguridad por si hay anuladas.
            long total = facturaRepository.count();
            int secuencia = (int) total + 1;

            String codigo;
            int intentos = 0;

            while (true) {
                codigo = String.format("FAC-%d-%06d", year, secuencia);

                // Verificamos si existe en BD (activo o inactivo/anulado)
                if (facturaRepository.findById(codigo) == null) {
                    // ¡Libre!
                    break;
                }
                // Si existe, probamos el siguiente
                secuencia++;

                // Protección contra bucles infinitos (opcional)
                intentos++;
                if (intentos > 1000) throw new RuntimeException("No se pudo asignar folio");
            }

            this.facturaNueva.setNumero_factura(codigo);

            // Mensaje de éxito discreto para confirmar que el botón funciona
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Folio Generado", codigo));

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fallo al generar folio.");
        }
    }

    public void cargarOrdenesPendientes() {
        List<Orden> todas = ordenRepository.findAll();
        this.listaOrdenesPendientes = todas.stream()
                .filter(o -> "TERMINADA".equals(o.getEstado()))
                .collect(Collectors.toList());
    }

    public void onOrdenSelect() {
        if (facturaNueva.getNumero_orden() != null) {
            Orden orden = ordenRepository.findById(facturaNueva.getNumero_orden());
            if (orden != null) {
                BigDecimal costo = orden.getCosto_total();
                if(costo == null) costo = BigDecimal.ZERO;

                this.facturaNueva.setSubtotal(costo);
                this.facturaNueva.setImpuesto(costo.multiply(new BigDecimal("0.15")));
                this.facturaNueva.setTotal(costo.add(this.facturaNueva.getImpuesto()));
            }
        }
    }

    public void guardarFactura() {
        try {
            if (this.facturaNueva.getNumero_orden() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione una orden.");
                return;
            }
            facturaRepository.create(this.facturaNueva);
            addMessage(FacesMessage.SEVERITY_INFO, "Facturado", "Factura emitida correctamente.");
            resetFormulario();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo facturar: " + e.getMessage());
        }
    }

    // CAMBIO: Método sin parámetros, usa 'facturaSeleccionada'
    public void anularFactura() {
        if (this.facturaSeleccionada == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se seleccionó ninguna factura.");
            return;
        }
        try {
            facturaRepository.anular(this.facturaSeleccionada.getNumero_factura());
            addMessage(FacesMessage.SEVERITY_WARN, "Anulada", "La factura " + this.facturaSeleccionada.getNumero_factura() + " ha sido anulada.");
            this.facturaSeleccionada = null; // Limpiar selección
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fallo al anular: " + e.getMessage());
        }
    }

    public void resetFormulario() {
        this.facturaNueva = new Factura();
        this.facturaNueva.setMetodo_pago("EFECTIVO");
        generarNumeroFactura();
        cargarOrdenesPendientes();
    }

    private void iniciarLazyModel() {
        this.facturasLazy = new LazyDataModel<Factura>() {
            private static final long serialVersionUID = 1L;
            @Override
            public int count(Map<String, FilterMeta> filterBy) { return (int) facturaRepository.count(); }
            @Override
            public List<Factura> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                List<Factura> data = facturaRepository.findRange(first, pageSize);
                setRowCount((int) facturaRepository.count());
                return data;
            }
            @Override
            public String getRowKey(Factura f) { return f.getNumero_factura(); }
            @Override
            public Factura getRowData(String rowKey) { return facturaRepository.findById(rowKey); }
        };
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}