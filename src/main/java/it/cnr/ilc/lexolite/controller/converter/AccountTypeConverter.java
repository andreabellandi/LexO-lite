package it.cnr.ilc.lexolite.controller.converter;

import it.cnr.ilc.lexolite.controller.converter.AccountTypeConverter.AccountTypeData;
import it.cnr.ilc.lexolite.domain.AccountType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author oakgen
 */
@FacesConverter(forClass = AccountTypeData.class)
public class AccountTypeConverter implements Converter {

    private static final String SEPARATOR = "$";

    private static final Map<Long, AccountType> ACCOUNT_TYPES = new HashMap<>();

    public static void setAccountTypes(List<AccountType> accountTypes) {
        AccountTypeConverter.ACCOUNT_TYPES.clear();
        for (AccountType accountType : accountTypes) {
            AccountTypeConverter.ACCOUNT_TYPES.put(accountType.getId(), accountType);
        }
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, SEPARATOR);
        Long id = Long.valueOf(tokenizer.nextToken());
        String name = tokenizer.nextToken();
        String color = tokenizer.nextToken();
        AccountType accountType = ACCOUNT_TYPES.get(id);
        if (accountType == null) {
            accountType = new AccountType();
            accountType.setId(id);
            accountType.setName(name);
            accountType.setColor(color);
        }
        return new AccountTypeData(accountType);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        AccountTypeData typeData = (AccountTypeData) value;
        return typeData.getId() + SEPARATOR + typeData.getName() + SEPARATOR + typeData.getColor();
    }

    public static class AccountTypeData implements Serializable {

        private final Long id;
        private final String color;
        private final String name;

        public AccountTypeData(AccountType accountType) {
            this.id = accountType.getId();
            this.color = accountType.getColor();
            this.name = accountType.getName();
        }

        public Long getId() {
            return id;
        }

        public String getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof AccountTypeData) {
                return ((AccountTypeData) other).getId().equals(id);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

    }

}
