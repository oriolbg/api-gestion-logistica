package erp.gestion.apilogistica.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PageableService <T, ID>{
    Page<T> findAll(Pageable pageable, String search);
    T findById(ID id);
    T create(T obj);
    T update(ID id, T obj);
    void delete(ID id);
}
