package br.dev.javacom.repository;

import br.dev.javacom.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByActiveTrueOrderByIdAsc();

    Page<Product> findAllByActiveTrue(Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}
