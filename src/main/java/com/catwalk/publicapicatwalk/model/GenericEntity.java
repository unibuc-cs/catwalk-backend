package com.catwalk.publicapicatwalk.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class GenericEntity implements Serializable {

    @Id
    @GeneratedValue(generator = IdGenerator.generatorName)
    @GenericGenerator(name = IdGenerator.generatorName, strategy = "com.catwalk.publicapicatwalk.model.IdGenerator")
    private String id;

    @Column(name = "created_on", nullable = false, updatable = false)
    private Date createdOn;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "last_modified_on")
    private Date lastModifiedOn;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    private void audit(String operation) {
        if (operation.compareTo("INSERT") == 0) {
            createdBy = "SYSTEM";
            createdOn = new Date();
        }
        if (operation.compareTo("UPDATE") == 0) {
            lastModifiedBy = "SYSTEM";
            lastModifiedOn = new Date();
        }
    }

    @PrePersist
    public void onPrePersist() {
        audit("INSERT");
    }

    @PreUpdate
    public void onPreUpdate() {
        audit("UPDATE");
    }

}
