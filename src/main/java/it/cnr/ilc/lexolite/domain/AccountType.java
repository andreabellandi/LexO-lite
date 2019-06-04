/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.domain;

import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

/**
 *
 * @author oakgen
 */
@Entity
public class AccountType extends SuperEntity implements Labeled {

    public enum Permission {

        READ_MINE,
        READ_ALL,
        WRITE_MINE,
        WRITE_MINE_READ_ALL,
        WRITE_ALL;

        public static boolean hasPermission(Permission accountPermission, Permission permission) {
            if (accountPermission == null
                    || permission == null
                    || (accountPermission == WRITE_MINE && permission == READ_ALL)) {
                return false;
            } else {
                return accountPermission.ordinal() >= permission.ordinal();
            }
        }
    }

    private String name;
    private String color;
    private Map<String, Permission> permissions;
    private Map<String, String> labels;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    public Map<String, Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Permission> permissions) {
        this.permissions = permissions;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

}
