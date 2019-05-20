package org.keycloak.datapath.model;

import javax.persistence.*;

@Entity
@Table(name = "LONG_ATTRIBUTES_MAPPING")
@NamedQueries({
        @NamedQuery(name = "LongAttributesMapping.findByUserId", query = "from LongAttributesMapping where userId = :userId"),
        @NamedQuery(name = "LongAttributesMapping.findByUserIdAndAttributeKey", query = "from LongAttributesMapping where (userId = :userId and attributeKey = :attributeKey)")
})
public class LongAttributesMapping extends AbstractEntity {

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "ATTRIBUTE_KEY", nullable = false)
    private String attributeKey;

    @Column(name = "ATTRIBUTE_VALUE", nullable = false)
    private String attributeValue;

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getSimpleName());
        sb.append("{");
        sb.append("id=");
        sb.append(id);
        sb.append(", uuid='");
        sb.append(uuid);
        sb.append("'}");

        return sb.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
