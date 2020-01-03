package o.horbenko.nnettysample.repository;

import o.horbenko.nnettysample.domain.FooDomain;

import java.util.List;
import java.util.Optional;

public interface FooDomainRepository {

    List<FooDomain> findAll();

    Optional<FooDomain> findById(Long id);

    FooDomain save(FooDomain fooDomainToSave);

}
