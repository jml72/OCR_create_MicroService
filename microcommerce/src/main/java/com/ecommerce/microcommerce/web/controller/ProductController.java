package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;


@Api( description="API pour es opérations CRUD sur les produits.")
@RestController
public class ProductController {

    @Autowired
    ProductDao productDao;

    //Récupérer la liste des produit
    /*@GetMapping("/Produits")
    @RequestMapping(value="/Produits", method = RequestMethod.GET)
    public List<Product> listeProduits() {
        //return productDao.findAll();
    }*/

    //Récupérer la liste des produit
    @RequestMapping(value="/Produits", method = RequestMethod.GET)

    public MappingJacksonValue listeProduits() {

        List<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }



    //Récupérer un produit par son id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @RequestMapping(value="/Produits/{id}", method = RequestMethod.GET)
    public Product AfficherUnProduit(@PathVariable int id) {
        Product produit = productDao.findById(id);
        if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");
        return produit;
    }

    //Ajouter un nouveau produit
    @RequestMapping(value="/Produits", method = RequestMethod.POST)
    public ResponseEntity<Void> EnregistrerUnProduit(@Valid @RequestBody(required = true) Product product) {
        Product productAdded = productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "test/produits/{prixLimit}", method = RequestMethod.GET)
    public List<Product> testeRequetes(@PathVariable int prixLimit) {
        return productDao.findByPrixGreaterThan(prixLimit);
    }

    @RequestMapping(value= "recherche/produits/{recherche}", method = RequestMethod.GET)
    public List<Product> testRecherche(@PathVariable String recherche) {
        return productDao.findByNomLike("%"+ recherche +"%");
    }

    @RequestMapping(value = "Produits/{id}", method = RequestMethod.DELETE)
    public void supprimerProduit(@PathVariable int id) {
        productDao.deleteById(id);
    }

    @RequestMapping(value = "/Produits", method = RequestMethod.PUT)
    public void mettreAJourProduit(@RequestBody Product product) {
        productDao.save(product);
    }

}
