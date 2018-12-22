package demo.app.example1;

import org.springframework.data.repository.CrudRepository;

import demo.app.example1.model.Hospital;

public interface HospRepository  extends CrudRepository<Hospital, Integer>{

}
