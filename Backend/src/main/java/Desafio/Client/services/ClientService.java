package Desafio.Client.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Desafio.Client.dto.ClientDTO;
import Desafio.Client.entitites.Client;
import Desafio.Client.repositories.ClientRepository;
import Desafio.Client.services.exceptions.DatabaseException;
import Desafio.Client.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repository;

	@Transactional(readOnly = true) // garantia das propriedades ACID
	public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
		Page<Client> list = repository.findAll(pageRequest);
		return list.map(x -> new ClientDTO(x));
	}

	// optional usado para evitar valores nulos vindos do findById
	@Transactional(readOnly = true) // garantia das propriedades ACID
	public ClientDTO findById(Long id) {
		Optional<Client> obj = repository.findById(id);
		Client entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ClientDTO(entity);
	}

	@Transactional
	public ClientDTO insert(ClientDTO dto) {
		Client entity = new Client();
		copyDtotoEntity(dto, entity); 
		entity = repository.save(entity);
		return new ClientDTO(entity);
	}

	// Método para atualizar um dado nas entidades e trata exceção
	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		try {
			Client entity = repository.getOne(id); // instancia com getOne antes de salvar
			copyDtotoEntity(dto, entity);
			entity = repository.save(entity);
			return new ClientDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id); // criada no services.exceptions
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation"); // criada no services.exceptions
		}
	}

	// copiador dos dados dos atributos para insert e update //não precisa fazer
	// para os atributos chave
	private void copyDtotoEntity(ClientDTO dto, Client entity) {
		entity.setName(dto.getName());
		entity.setCpf(dto.getCpf());
		entity.setIncome(dto.getIncome());
		entity.setBirthDate(dto.getBirthDate());
		entity.setChildren(dto.getChildren());
	}

}