package com.themoviedb.models;

import com.themoviedb.apis.entity.responses.ProductionCompanyParser;

/**
 * Created by vaibhav on 6/10/17.
 */

public class ProductionCompanyModel {

    private String name;

    private int id;

    public ProductionCompanyModel() {
    }

    public ProductionCompanyModel(ProductionCompanyParser parser) {

        if(parser == null) {
            return;
        }

        this.id = parser.getId();
        this.name = parser.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
