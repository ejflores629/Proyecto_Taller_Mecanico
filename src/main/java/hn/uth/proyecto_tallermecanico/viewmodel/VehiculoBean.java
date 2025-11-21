package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Cliente;
import hn.uth.proyecto_tallermecanico.model.Vehiculo;
import hn.uth.proyecto_tallermecanico.repository.ClienteRepository;
import hn.uth.proyecto_tallermecanico.repository.VehiculoRepository;
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
import java.time.Year;
import java.util.*;

@Named("vehiculoBean")
@ViewScoped
public class VehiculoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private VehiculoRepository vehiculoRepository;

    @Inject
    private ClienteRepository clienteRepository;

    @Getter
    private LazyDataModel<Vehiculo> vehiculosLazy;

    @Getter @Setter
    private List<Cliente> listaClientes;

    @Getter @Setter
    private Vehiculo vehiculoSeleccionado;

    @Getter @Setter
    private Vehiculo vehiculoNuevo;

    // --- CAMPOS DE CONTROL UX (No se guardan en BD, solo ayudan) ---
    @Getter @Setter
    private String tipoVehiculoSel; // "CARRO" o "MOTO"

    // --- LISTAS DINÁMICAS ---
    @Getter private List<String> listaMarcas;
    @Getter private List<String> listaModelos;
    @Getter private List<String> listaColores;
    @Getter private List<Integer> listaAnios;

    // --- DATOS ESTÁTICOS (Simulando una DB de marcas/modelos) ---
    private static final Map<String, List<String>> MARCAS_POR_TIPO = new HashMap<>();
    private static final Map<String, List<String>> MODELOS_CARRO = new HashMap<>();
    private static final Map<String, List<String>> MODELOS_MOTO = new HashMap<>();

    static {
        // 1. Configurar Tipos y Marcas
        MARCAS_POR_TIPO.put("CARRO", Arrays.asList("Toyota", "Honda", "Ford", "Hyundai", "Kia", "BMW"));
        MARCAS_POR_TIPO.put("MOTO", Arrays.asList("Yamaha", "Honda", "Suzuki", "Kawasaki", "Ducati", "Genesis"));

        // 2. Configurar Modelos de CARROS
        MODELOS_CARRO.put("Toyota", Arrays.asList("Corolla", "Hilux", "Yaris", "Tacoma", "Rav4"));
        MODELOS_CARRO.put("Honda", Arrays.asList("Civic", "CR-V", "Pilot", "City"));
        MODELOS_CARRO.put("Ford", Arrays.asList("F-150", "Ranger", "Explorer", "Fiesta"));
        MODELOS_CARRO.put("Hyundai", Arrays.asList("Elantra", "Tucson", "Santa Fe", "Accent"));
        MODELOS_CARRO.put("Kia", Arrays.asList("Picanto", "Rio", "Sportage", "Sorento"));
        MODELOS_CARRO.put("BMW", Arrays.asList("Serie 3", "X5", "X3", "M3"));

        // 3. Configurar Modelos de MOTOS
        MODELOS_MOTO.put("Yamaha", Arrays.asList("R6", "R1", "MT-03", "FZ", "Crux"));
        MODELOS_MOTO.put("Honda", Arrays.asList("CBR 600", "Navi", "Dio", "Africa Twin")); // Honda tiene motos y carros
        MODELOS_MOTO.put("Suzuki", Arrays.asList("Gixxer", "Hayabusa", "AX4", "GN125"));
        MODELOS_MOTO.put("Kawasaki", Arrays.asList("Ninja 400", "Z900", "Versys", "KLR 650"));
        MODELOS_MOTO.put("Ducati", Arrays.asList("Monster", "Panigale", "Multistrada"));
        MODELOS_MOTO.put("Genesis", Arrays.asList("G125", "HJ 125", "K5"));
    }

    @PostConstruct
    public void init() {
        this.vehiculoNuevo = new Vehiculo();
        this.tipoVehiculoSel = "CARRO"; // Default
        iniciarLazyModel();
        cargarListasBase();
        actualizarMarcas(); // Cargar marcas iniciales
    }

    private void cargarListasBase() {
        this.listaClientes = clienteRepository.findAll();
        this.listaColores = Arrays.asList("Blanco", "Negro", "Plateado", "Gris", "Rojo", "Azul", "Verde", "Amarillo");

        this.listaAnios = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear + 1; i >= 1980; i--) {
            this.listaAnios.add(i);
        }
    }

    // --- LÓGICA DE EVENTOS AJAX ---

    public void onTipoChange() {
        // Cuando cambia Carro/Moto -> Actualizamos Marcas y limpiamos Modelo
        actualizarMarcas();
        this.listaModelos = new ArrayList<>(); // Limpiar modelos anteriores

        // Limpiar selecciones actuales en el objeto
        if (this.vehiculoNuevo != null) {
            this.vehiculoNuevo.setMarca(null);
            this.vehiculoNuevo.setModelo(null);
        }
        if (this.vehiculoSeleccionado != null) {
            this.vehiculoSeleccionado.setMarca(null);
            this.vehiculoSeleccionado.setModelo(null);
        }
    }

    public void onMarcaChange() {
        // Cuando cambia Marca -> Actualizamos Modelos
        String marcaSel = (vehiculoSeleccionado != null && vehiculoSeleccionado.getPlaca() != null)
                ? vehiculoSeleccionado.getMarca()
                : vehiculoNuevo.getMarca();

        if (marcaSel != null && !marcaSel.isEmpty()) {
            if ("CARRO".equals(tipoVehiculoSel)) {
                this.listaModelos = MODELOS_CARRO.getOrDefault(marcaSel, new ArrayList<>());
            } else {
                this.listaModelos = MODELOS_MOTO.getOrDefault(marcaSel, new ArrayList<>());
            }
        } else {
            this.listaModelos = new ArrayList<>();
        }
    }

    // Método auxiliar para llenar la lista de marcas según el tipo seleccionado
    private void actualizarMarcas() {
        this.listaMarcas = MARCAS_POR_TIPO.getOrDefault(tipoVehiculoSel, new ArrayList<>());
    }

    // --- LÓGICA PREPARAR EDICIÓN (DETECTAR TIPO) ---
    // Como no guardamos "TIPO" en BD, tratamos de adivinarlo al abrir editar
    public void prepararEdicion(Vehiculo v) {
        this.vehiculoSeleccionado = v;

        // Lógica simple: Si la marca está en la lista de Motos, es Moto. Si no, asumimos Carro.
        // (Esto puede fallar con Honda, pero asumimos Carro por defecto si hay ambigüedad o ajustamos prioridad)
        boolean esMoto = MARCAS_POR_TIPO.get("MOTO").contains(v.getMarca())
                && MODELOS_MOTO.containsKey(v.getMarca())
                && MODELOS_MOTO.get(v.getMarca()).contains(v.getModelo());

        this.tipoVehiculoSel = esMoto ? "MOTO" : "CARRO";

        // Refrescar listas para que el combo muestre el valor correcto
        actualizarMarcas();
        onMarcaChange();
    }

    private void iniciarLazyModel() {
        this.vehiculosLazy = new LazyDataModel<Vehiculo>() {
            private static final long serialVersionUID = 1L;
            @Override
            public int count(Map<String, FilterMeta> filterBy) { return (int) vehiculoRepository.count(); }
            @Override
            public List<Vehiculo> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                List<Vehiculo> data = vehiculoRepository.findRange(first, pageSize);
                setRowCount((int) vehiculoRepository.count());
                return data;
            }
            @Override
            public String getRowKey(Vehiculo vehiculo) { return vehiculo.getPlaca(); }
            @Override
            public Vehiculo getRowData(String rowKey) { return vehiculoRepository.findById(rowKey); }
        };
    }

    public void guardarVehiculo() {
        try {
            if (this.vehiculoSeleccionado != null && this.vehiculoSeleccionado.getPlaca() != null) {
                vehiculoRepository.save(this.vehiculoSeleccionado);
                addMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Vehículo actualizado correctamente.");
                this.vehiculoSeleccionado = null;
            } else {
                vehiculoRepository.save(this.vehiculoNuevo);
                addMessage(FacesMessage.SEVERITY_INFO, "Creado", "Vehículo registrado correctamente.");
                this.vehiculoNuevo = new Vehiculo();
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    public void eliminarVehiculo(Vehiculo vehiculo) {
        try {
            vehiculoRepository.delete(vehiculo.getPlaca());
            addMessage(FacesMessage.SEVERITY_INFO, "Eliminado", "Vehículo dado de baja.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar: " + e.getMessage());
        }
    }

    public void resetFormulario() {
        this.vehiculoNuevo = new Vehiculo();
        this.vehiculoSeleccionado = null;
        this.tipoVehiculoSel = "CARRO";
        onTipoChange(); // Resetear listas
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}