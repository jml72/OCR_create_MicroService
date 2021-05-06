package com.ecommerce.microcommerce.dao;

import com.ecommerce.microcommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductDao extends JpaRepository<Product, Integer> {

    Product findById(int id);
    List<Product> findByPrixGreaterThan(int prixLimit);
    List<Product> findByNomLike(String recherche);

    @Query("select id, prix from Product p where p.prix > :prixLimit")
    List<Product> cherheProduitCher(@Param("prixLimit") int prix);


}
