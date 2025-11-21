package sync.repositories;

import sync.entities.CustomerArchive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerArchiveRepository extends JpaRepository<CustomerArchive, UUID> {

}
