package com.chananel.order.service.mapper;

import org.mapstruct.*;

/**
 * Mapper for the entity Testa and its DTO TestaDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TestaMapper extends EntityMapper <TestaDTO, Testa> {


    /**
     * generating the fromId for all mappers if the databaseType is sql, as the class has relationship to it might need it, instead of
     * creating a new attribute to know if the entity has any relationship from some other entity
     *
     * @param id id of the entity
     * @return the entity instance
     */

    default Testa fromId(Long id) {
        if (id == null) {
            return null;
        }
        Testa testa = new Testa();
        testa.setId(id);
        return testa;
    }
}
