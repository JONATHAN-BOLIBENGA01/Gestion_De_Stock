package org.example.repository;

import org.example.product.Produit;

import java.util.List;

public interface ProductRepository {
    void saveProducts(List<Produit> products, String filename);
    List<Produit> loadProducts(String filename);
}