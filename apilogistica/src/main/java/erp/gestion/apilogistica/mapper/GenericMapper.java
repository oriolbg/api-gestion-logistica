package erp.gestion.apilogistica.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public interface GenericMapper<E, D> {
    D toDTO(E entity);
    E toEntity(D dto);
    List<D> toDTO(List<E> entities);
    List<E> toEntity(List<D> dtos);

    // Soporte nativo para actualizaciones sin instanciar objetos temporales
    void updateEntityFromDto(D dto, @MappingTarget E entity);
}
