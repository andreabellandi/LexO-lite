/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

/**
 *
 * @author andrea
 */
public class ReferenceMenuTheme {

    public enum itemType {
        clazz,
        objectProperty,
        dataProperty,
        instance;
    }
    private int id;
    private itemType type;
    private String name;

    public ReferenceMenuTheme() {
    }

    public ReferenceMenuTheme(int id, itemType type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type.toString();
    }

    public void setType(itemType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
