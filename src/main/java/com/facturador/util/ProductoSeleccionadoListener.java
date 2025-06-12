package com.facturador.util;

import com.facturador.database.Producto;

@FunctionalInterface
public interface ProductoSeleccionadoListener {
    void onProductoSeleccionado(Producto producto);
}
