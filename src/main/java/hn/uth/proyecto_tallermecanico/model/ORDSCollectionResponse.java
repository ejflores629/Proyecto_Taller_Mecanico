package hn.uth.proyecto_tallermecanico.model;

import java.io.Serializable;
import java.util.List;

public class ORDSCollectionResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> items;

    public ORDSCollectionResponse() {
    }

    public ORDSCollectionResponse(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    // Opcional: Se pueden añadir getters y setters para hasMore, limit, etc.,
    // pero para la funcionalidad básica, solo 'items' es necesario.
}