package br.dev.kajosama.dropship.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.kajosama.dropship.domain.model.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
