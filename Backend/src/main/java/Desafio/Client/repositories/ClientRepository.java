package Desafio.Client.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Desafio.Client.entitites.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{

}
