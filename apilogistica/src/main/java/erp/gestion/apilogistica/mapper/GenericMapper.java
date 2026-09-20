package erp.gestion.apilogistica.mapper;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericMapper<E, D> {
    public abstract D toDTO(E entity);
    public abstract E toEntity(D dto);

    public List<D> toDTO(List<E> entitys){
        return entitys.stream()
                .map(e -> toDTO(e))
                .collect(Collectors.toList());
    }

    public List<E> toEntity(List<D> dtos){
        return dtos.stream()
                .map(d -> toEntity(d))
                .collect(Collectors.toList());
    }
}
